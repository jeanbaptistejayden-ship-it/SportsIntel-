from datetime import datetime
from functools import lru_cache
from concurrent.futures import ThreadPoolExecutor, as_completed
import time

import pandas as pd
from nba_api.stats.endpoints import playergamelog
from nba_api.stats.endpoints import commonplayerinfo
from nba_api.stats.static import players as nba_players

SEASON_TYPE_MAP = {
    "regular": "Regular Season",
    "playoffs": "Playoffs",
}

STAT_MAP = {
    "points": "pts",
    "ppg": "pts",
    "pts": "pts",
    "assists": "ast",
    "apg": "ast",
    "ast": "ast",
    "rebounds": "reb",
    "rpg": "reb",
    "reb": "reb",
    "field_goal_percentage": "fg_pct",
    "fg_percentage": "fg_pct",
    "fg%": "fg_pct",
    "fg_pct": "fg_pct",
    "three_point_percentage": "fg3_pct",
    "3pt_percentage": "fg3_pct",
    "fg3%": "fg3_pct",
    "fg3_pct": "fg3_pct",
    "free_throw_percentage": "ft_pct",
    "ft_percentage": "ft_pct",
    "ft%": "ft_pct",
    "ft_pct": "ft_pct",
    "steals": "stl",
    "spg": "stl",
    "stl": "stl",
    "blocks": "blk",
    "bpg": "blk",
    "blk": "blk",
    "turnovers": "tov",
    "topg": "tov",
    "tov": "tov",
    "minutes": "min",
    "mpg": "min",
    "min": "min",
    "plus_minus": "plus_minus",
    "+/-": "plus_minus",
}


def player_image_url(player_id: int) -> str:
    return f"https://cdn.nba.com/headshots/nba/latest/1040x760/{player_id}.png"


@lru_cache(maxsize=256)
def get_player_lookup(name: str):
    matches = nba_players.find_players_by_full_name(name)

    if not matches:
        return None, {"error": f"Could not find player '{name}' in nba_api player index."}

    best_match = next((p for p in matches if p.get("is_active")), matches[0])

    player_id = int(best_match["id"])
    player_name = best_match["full_name"]

    from_year = get_player_from_year(player_id)

    return {
        "id": player_id,
        "name": player_name,
        "from_year": from_year,
    }, None

@lru_cache(maxsize=512)
def get_player_from_year(player_id: int) -> int:
    try:
        info = commonplayerinfo.CommonPlayerInfo(
            player_id=player_id,
            timeout=20
        )

        df = info.get_data_frames()[0]

        if not df.empty and "FROM_YEAR" in df.columns:
            return int(df.iloc[0]["FROM_YEAR"])

    except Exception as e:
        print(f"Could not fetch FROM_YEAR for player {player_id}: {e}")

    return 2003

def get_default_season() -> str:
    now = datetime.utcnow()
    if now.month >= 10:
        start_year = now.year
    else:
        start_year = now.year - 1
    return f"{start_year}-{str(start_year + 1)[-2:]}"


@lru_cache(maxsize=512)
def fetch_gamelog(player_id: int, season: str, season_type: str = "Regular Season"):
    """Fetch gamelog with stronger retries. Do not silently fail."""
    max_retries = 5
    wait_times = [2, 4, 8, 12, 16]

    last_error = None

    for attempt in range(max_retries):
        try:
            endpoint = playergamelog.PlayerGameLog(
                player_id=player_id,
                season=season,
                season_type_all_star=season_type,
                timeout=45,
            )

            data_frames = endpoint.get_data_frames()

            if data_frames and data_frames[0] is not None:
                return data_frames[0]

            raise RuntimeError(f"No data returned for {season}")

        except Exception as e:
            last_error = e

            if attempt < max_retries - 1:
                wait_time = wait_times[attempt]
                print(
                    f"API failed for season {season}, retrying in {wait_time}s... "
                    f"(attempt {attempt + 1}/{max_retries})"
                )
                time.sleep(wait_time)
            else:
                raise RuntimeError(
                    f"Failed to fetch required season {season} after {max_retries} attempts: {last_error}"
                )


def normalize_season_type(season_type: str) -> str:
    if season_type is None or season_type.strip() == "":
        return "career"

    normalized = season_type.strip().lower()
    if normalized in {"regular", "regular season"}:
        return "regular"
    if normalized == "playoffs":
        return "playoffs"
    raise ValueError(f"Invalid season_type '{season_type}'. Use regular or playoffs.")


def fetch_gamelog_by_type(player_id: int, season: str, season_type: str = "regular"):
    normalized = normalize_season_type(season_type)
    mapped = SEASON_TYPE_MAP.get(normalized)
    if not mapped:
        raise ValueError(f"Invalid season_type '{season_type}'. Use regular or playoffs.")
    return fetch_gamelog(player_id, season, mapped)


@lru_cache(maxsize=512)
def fetch_and_parse_gamelog(player_id: int, season: str, season_type: str = "regular"):
    df = fetch_gamelog_by_type(player_id, season=season, season_type=season_type)
    games = parse_games(df)
    return tuple(tuple(sorted(game.items())) for game in games)


def fetch_gamelog_range(player_id: int, start_season: str, end_season: str, season_type: str = "regular"):
    start = int(start_season.split("-")[0])
    end = int(end_season.split("-")[0])

    if end < start:
        raise ValueError("season_end must be the same as or after season_start.")

    current_year = datetime.utcnow().year
    current_month = datetime.utcnow().month
    max_start_year = current_year if current_month >= 10 else current_year - 1

    if start > max_start_year or end > max_start_year:
        raise ValueError("Season range cannot include future seasons.")

    seasons = [f"{y}-{str(y + 1)[-2:]}" for y in range(start, end + 1)]
    frames = []
    errors = []

    with ThreadPoolExecutor(max_workers=10) as executor:
        future_to_season = {
            executor.submit(fetch_gamelog_by_type, player_id, season, season_type): season
            for season in seasons
        }

        for future in as_completed(future_to_season):
            season = future_to_season[future]
            try:
                df = future.result()

                if df is None:
                    errors.append(f"{season}: No data returned")
                else:
                    frames.append(df)
            except Exception as exc:
                errors.append(f"{season}: {exc}")

    if errors:
        raise RuntimeError("Could not fetch all required seasons: " + " | ".join(errors))

    if frames:
        return pd.concat(frames, ignore_index=True)

    return None


def parse_games(gamelog_frame):
    if gamelog_frame is None or gamelog_frame.empty:
        return []

    games = []
    for _, row in gamelog_frame.iterrows():
        matchup = str(row.get("MATCHUP", ""))
        games.append(
            {
                "date": row.get("GAME_DATE", ""),
                "opponent": matchup.split()[-1] if matchup else "",
                "home": "vs." in matchup,
                "pts": to_float(row.get("PTS")),
                "reb": to_float(row.get("REB")),
                "ast": to_float(row.get("AST")),
                "fg_pct": to_percentage(row.get("FG_PCT")),
                "fg3_pct": to_percentage(row.get("FG3_PCT")),
                "ft_pct": to_percentage(row.get("FT_PCT")),
                "stl": to_float(row.get("STL")),
                "blk": to_float(row.get("BLK")),
                "tov": to_float(row.get("TOV")),
                "min": to_float(row.get("MIN")),
                "plus_minus": to_float(row.get("PLUS_MINUS")),
            }
        )
    return games


def filter_games_by_location(games: list[dict], location: str = "both"):
    normalized = location.strip().lower()
    if normalized == "both":
        return games
    if normalized == "home":
        return [g for g in games if g["home"]]
    if normalized == "away":
        return [g for g in games if not g["home"]]
    raise ValueError(f"Invalid location '{location}'. Use home, away, or both.")


def filter_games_by_opponent(games: list[dict], opponent: str | None = None):
    if opponent is None:
        return games
    target = opponent.strip().upper()
    if not target:
        return games
    return [g for g in games if g.get("opponent", "").upper() == target]


def to_season_string(start_year: int) -> str:
    return f"{start_year}-{str(start_year + 1)[-2:]}"


def build_compare_summary(player_name: str, player_id: int, games: list[dict]) -> dict:
    return {
        "player": player_name,
        "games_played": len(games),
        "ppg": average_stat(games, "pts"),
        "apg": average_stat(games, "ast"),
        "rpg": average_stat(games, "reb"),
        "fg_pct": average_stat(games, "fg_pct"),
        "mpg": average_stat(games, "min"),
    }

def fetch_player_vs_opponent(player_name: str, opponent: str):
    player, error = get_player_lookup(player_name)
    if error:
        raise ValueError(error["error"])

    player_id = player["id"]
    canonical_name = player["name"]

    start_season = "2014-15"
    end_season = get_default_season()

    gamelog_data = fetch_gamelog_range(
        player_id,
        start_season=start_season,
        end_season=end_season,
        season_type="regular"
    )

    all_games = parse_games(gamelog_data)
    vs_games = filter_games_by_opponent(all_games, opponent)

    if not vs_games:
        raise ValueError(f"No games found for {canonical_name} vs {opponent}")

    return build_compare_summary(canonical_name, player_id, vs_games)


def limit_last_n_games(games: list[dict], last_n: int | None = None):
    if last_n is None:
        return games
    if last_n <= 0:
        raise ValueError("Invalid last_n value. Use a positive integer.")

    if len(games) <= last_n:
        return games

    def game_date_key(game: dict):
        try:
            return datetime.strptime(game["date"], "%b %d, %Y")
        except ValueError:
            return datetime.min

    sorted_games = sorted(games, key=game_date_key, reverse=True)
    return sorted_games[:last_n]


def normalize_stat(stat: str = "points") -> str:
    normalized = stat.strip().lower()
    mapped = STAT_MAP.get(normalized)
    if not mapped:
        raise ValueError("Invalid stat selection.")
    return mapped


def build_summary(player_name: str, player_id: int, games: list[dict], stat: str = "points"):
    selected_stat = normalize_stat(stat)

    values = [float(g[selected_stat]) for g in games]
    avg = average_stat(games, selected_stat)

    high_low = get_high_low_games(games, stat)

    return {
        "player": player_name,
        "player_image": player_image_url(player_id),
        "stat": selected_stat,
        "games_played": len(games),
        "average": avg,
        "high": max(values) if values else 0.0,
        "low": min(values) if values else 0.0,
        "high_game": high_low["high_game"],
        "low_game": high_low["low_game"],
    }


def get_high_low_games(games: list[dict], stat: str = "points") -> dict:
    selected_stat = normalize_stat(stat)

    if not games:
        return {"high_game": None, "low_game": None}

    high_game = max(games, key=lambda g: float(g[selected_stat]))
    low_game = min(games, key=lambda g: float(g[selected_stat]))

    return {
        "high_game": {
            "date": high_game["date"],
            "opponent": high_game["opponent"],
            "home": high_game["home"],
            "value": float(high_game[selected_stat]),
        },
        "low_game": {
            "date": low_game["date"],
            "opponent": low_game["opponent"],
            "home": low_game["home"],
            "value": float(low_game[selected_stat]),
        },
    }


def to_float(value) -> float:
    if value is None:
        return 0.0
    if isinstance(value, (int, float)):
        return float(value)

    text = str(value).strip()
    if not text or text == "--":
        return 0.0
    if ":" in text:
        parts = text.split(":")
        if len(parts) == 2:
            minutes = float(parts[0]) if parts[0] else 0.0
            seconds = float(parts[1]) if parts[1] else 0.0
            return minutes + (seconds / 60.0)
    return float(text)


def to_percentage(value) -> float:
    raw = to_float(value)
    if raw <= 1.0:
        return raw * 100.0
    return raw


def restore_games(serialized_games):
    return [dict(items) for items in serialized_games]


def average_stat(games: list[dict], stat_key: str) -> float:
    if not games:
        return 0.0
    try:
        values = []
        for g in games:
            val = g.get(stat_key)
            if val is not None:
                values.append(float(val))
        
        if not values:
            return 0.0
        
        avg = sum(values) / len(values)
        # Handle NaN values
        import math
        if math.isnan(avg):
            return 0.0
        return round(avg, 1)
    except (ValueError, TypeError):
        return 0.0