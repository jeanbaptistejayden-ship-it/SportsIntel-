from datetime import datetime
from functools import lru_cache
import time

import pandas as pd
import requests
from nba_api.stats.endpoints import commonplayerinfo, playergamelog
from nba_api.stats.static import players as nba_players

SEASON_TYPE_MAP = {
    "regular": "Regular Season",
    "playoffs": "Playoffs",
    "both": None
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

def get_player_lookup(name: str):
    matches = nba_players.find_players_by_full_name(name)
    if not matches:
        return None, {"error": f"Could not find player '{name}' in nba_api player index."}

    active_match = next((p for p in matches if p.get("is_active")), matches[0])
    return {"id": int(active_match["id"]), "name": active_match["full_name"]}, None


def get_default_season() -> str:
    now = datetime.utcnow()
    if now.month >= 10:
        start_year = now.year
    else:
        start_year = now.year - 1
    return f"{start_year}-{str(start_year + 1)[-2:]}"


def fetch_gamelog(player_id: int, season: str, season_type: str = "Regular Season"):
    last_exc = None
    for attempt in range(3):
        try:
            endpoint = playergamelog.PlayerGameLog(
                player_id=player_id,
                season=season,
                season_type_all_star=season_type,
                timeout=30,
            )
            data_frames = endpoint.get_data_frames()
            return data_frames[0] if data_frames else None
        except requests.exceptions.RequestException as exc:
            last_exc = exc
            if attempt < 2:
                time.sleep(1.5 * (attempt + 1))
            continue
    raise ValueError("Could not reach stats.nba.com. Please check your internet connection and try again.") from last_exc


@lru_cache(maxsize=4096)
def _fetch_gamelog_by_type_cached(player_id: int, season: str, normalized_season_type: str):
    if normalized_season_type == "both":
        reg = fetch_gamelog(player_id, season, "Regular Season")
        post = fetch_gamelog(player_id, season, "Playoffs")
        frames = [f for f in [reg, post] if f is not None and not f.empty]
        return pd.concat(frames, ignore_index=True) if frames else None

    mapped = SEASON_TYPE_MAP.get(normalized_season_type)
    if not mapped:
        raise ValueError(f"Invalid season_type '{normalized_season_type}'. Use regular, playoffs, or both.")
    return fetch_gamelog(player_id, season, mapped)

def normalize_season_type(season_type: str) -> str:
    normalized = season_type.strip().lower()
    if normalized in {"regular", "regular season"}:
        return "regular"
    if normalized == "playoffs":
        return "playoffs"
    if normalized == "both":
        return "both"
    raise ValueError(f"Invalid season_type '{season_type}'. Use regular, playoffs, or both.")


def fetch_gamelog_by_type(player_id: int, season: str, season_type: str = "regular"):
    normalized = normalize_season_type(season_type)
    cached = _fetch_gamelog_by_type_cached(player_id, season, normalized)
    return None if cached is None else cached.copy(deep=True)

def fetch_gamelog_range(player_id, start_season, end_season, season_type="Regular Season"):
    import pandas as pd

    # pull the starting year; for example "2021-22" -> 2021
    start = int(start_season.split("-")[0])
    end = int(end_season.split("-")[0])

    frames = []
    for y in range(start, end + 1):
        # rebuild the season string for each year in the range
        season = f"{y}-{str(y + 1)[-2:]}"
        df = fetch_gamelog(player_id, season, season_type)
        # skip empty seasons so we don't break the concat
        if df is not None and not df.empty:
            frames.append(df)

    # stack all seasons into one dataframe, or return None if nothing came back
    return pd.concat(frames, ignore_index=True) if frames else None


def fetch_gamelog_range_by_type(player_id: int, start_year: int, end_year: int, season_type: str = "regular"):
    if start_year > end_year:
        raise ValueError("Season start year cannot be greater than season end year.")

    frames = []
    for year in range(start_year, end_year + 1):
        season = f"{year}-{str(year + 1)[-2:]}"
        frame = fetch_gamelog_by_type(player_id, season=season, season_type=season_type)
        if frame is not None and not frame.empty:
            frames.append(frame)

    return pd.concat(frames, ignore_index=True) if frames else None


def get_player_career_years(player_id: int) -> tuple[int, int]:
    try:
        endpoint = commonplayerinfo.CommonPlayerInfo(player_id=player_id, timeout=30)
        data_frames = endpoint.get_data_frames()
    except requests.exceptions.RequestException as exc:
        raise ValueError("Could not reach stats.nba.com. Please check your internet connection and try again.") from exc
    if not data_frames or data_frames[0].empty:
        raise ValueError("Could not determine player career years.")

    info = data_frames[0].iloc[0]
    from_year_raw = info.get("FROM_YEAR")
    to_year_raw = info.get("TO_YEAR")
    if from_year_raw is None or to_year_raw is None:
        raise ValueError("Could not determine player career years.")

    start_year = int(from_year_raw)
    end_year = int(to_year_raw)
    if end_year < start_year:
        raise ValueError("Invalid player career year range.")
    return start_year, end_year

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
                "result": str(row.get("WL", "")).strip(),
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

def limit_last_n_games(games: list[dict], last_n: int | None = None):
    if last_n is None:
        return games
    if last_n <= 0:
        raise ValueError("Invalid last_n value. Use a positive integer.")

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

    # grab the full game context for the best and worst games
    high_low = get_high_low_games(games, stat)

    # summary the frontend actually uses: averages plus the standout games
    return {
        "player": player_name,
        "player_image": player_image_url(player_id),
        "stat": selected_stat,
        "games_played": len(games),
        "average": round(sum(values) / len(values), 1),
        "high": max(values),
        "low": min(values),
        "high_game": high_low["high_game"],
        "low_game": high_low["low_game"],
    }


def build_stat_averages(games: list[dict]) -> dict:
    if not games:
        return {
            "ast_average": 0.0,
            "reb_average": 0.0,
            "fg_pct_average": 0.0,
            "min_average": 0.0,
        }

    def avg(stat_key: str) -> float:
        values = [float(g.get(stat_key, 0.0)) for g in games]
        return round(sum(values) / len(values), 1) if values else 0.0

    return {
        "ast_average": avg("ast"),
        "reb_average": avg("reb"),
        "fg_pct_average": avg("fg_pct"),
        "min_average": avg("min"),
    }

def get_high_low_games(games: list[dict], stat: str = "points") -> dict:
    # normalize whatever the user passed in (points, ppg, etc.) to the actual key
    selected_stat = normalize_stat(stat)

    # nothing to work with, bail early
    if not games:
        return {"high_game": None, "low_game": None}

    # find the game where that stat was highest/lowest
    high_game = max(games, key=lambda g: float(g[selected_stat]))
    low_game = min(games, key=lambda g: float(g[selected_stat]))

    # return just what the results page needs: date, opponent, location, value
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
