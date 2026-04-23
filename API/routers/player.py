from fastapi import APIRouter

from API.services.player_service import (
    build_summary,
    fetch_gamelog_by_type,
    fetch_gamelog_range,
    filter_games_by_location,
    filter_games_by_opponent,
    get_default_season,
    get_player_lookup,
    limit_last_n_games,
    parse_games,
    average_stat,
)

router = APIRouter()


def sort_games_desc(games: list[dict]) -> list[dict]:
    from datetime import datetime

    def game_date_key(game: dict):
        try:
            return datetime.strptime(game["date"], "%b %d, %Y")
        except Exception:
            return datetime.min

    return sorted(games, key=game_date_key, reverse=True)


def build_career_vs_opponent_summary(player_name: str, opponent: str | None, games: list[dict]) -> dict:
    return {
        "player": player_name,
        "opponent": opponent,
        "games_played": len(games),
        "ppg": average_stat(games, "pts"),
        "apg": average_stat(games, "ast"),
        "rpg": average_stat(games, "reb"),
        "mpg": average_stat(games, "min"),
        "bpg": average_stat(games, "blk"),
        "spg": average_stat(games, "stl"),
        "tov": average_stat(games, "tov"),
    }


def build_career_overview_summary(games: list[dict]) -> dict:
    return {
        "games_played": len(games),
        "ppg": average_stat(games, "pts"),
        "apg": average_stat(games, "ast"),
        "rpg": average_stat(games, "reb"),
        "mpg": average_stat(games, "min"),
        "fg_pct": average_stat(games, "fg_pct"),
        "fg3_pct": average_stat(games, "fg3_pct"),
        "tov": average_stat(games, "tov"),
        "blk": average_stat(games, "blk"),
        "stl": average_stat(games, "stl"),
    }


@router.get("/player/{name}")
def get_player_stats(
        name: str,
        season_start: str | None = None,
        season_end: str | None = None,
        season_type: str = "regular",
        location: str = "both",
        opponent: str | None = None,
        last_n: int | None = None,
        stat: str = "points",
):
    player, error = get_player_lookup(name)
    if error:
        return error

    player_id = player["id"]
    player_name = player["name"]

    has_season_filter = bool(season_start or season_end)

    try:
        if has_season_filter:
            chosen_start = season_start or season_end
            chosen_end = season_end or season_start

            if chosen_start == chosen_end:
                gamelog_data = fetch_gamelog_by_type(
                    player_id,
                    season=chosen_start,
                    season_type=season_type
                )
            else:
                gamelog_data = fetch_gamelog_range(
                    player_id,
                    start_season=chosen_start,
                    end_season=chosen_end,
                    season_type=season_type
                )
        else:
            chosen_start = "2015-16"
            chosen_end = get_default_season()
            gamelog_data = fetch_gamelog_range(
                player_id,
                start_season=chosen_start,
                end_season=chosen_end,
                season_type=season_type
            )
    except ValueError as exc:
        return {"error": str(exc)}
    except Exception as exc:
        return {"error": f"Backend fetch failed: {str(exc)}"}

    all_games = parse_games(gamelog_data)

    try:
        scoped_games = filter_games_by_location(all_games, location=location)
    except ValueError as exc:
        return {"error": str(exc)}

    career_overview_games = scoped_games
    career_vs_opponent_games = filter_games_by_opponent(scoped_games, opponent=opponent)

    filtered_games = career_vs_opponent_games if opponent else scoped_games

    try:
        filtered_games = limit_last_n_games(filtered_games, last_n=last_n)
    except ValueError as exc:
        return {"error": str(exc)}

    if not filtered_games:
        return {
            "error": (
                f"No games found for {player_name} "
                f"({chosen_start} to {chosen_end}, {season_type}, {location}, {opponent}, {last_n})."
            )
        }

    try:
        summary = build_summary(player_name, player_id, filtered_games, stat=stat)
    except ValueError as exc:
        return {"error": str(exc)}

    recent_vs_opponent_games = sort_games_desc(career_vs_opponent_games)[:5]

    return {
        "summary": summary,
        "meta": {
            "season_start": chosen_start,
            "season_end": chosen_end,
            "season_range": f"{chosen_start} to {chosen_end}",
            "season_type": season_type,
            "location": location,
            "opponent": opponent,
            "last_n": last_n,
            "stat": stat,
        },
        "games": filtered_games,
        "career_vs_opponent_summary": build_career_vs_opponent_summary(
            player_name=player_name,
            opponent=opponent,
            games=career_vs_opponent_games,
        ),
        "career_overview_summary": build_career_overview_summary(career_overview_games),
        "recent_vs_opponent_games": recent_vs_opponent_games,
    }


@router.get("/player/filters/reset")
def reset_filters():
    default_season = get_default_season()
    return {
        "season_start": default_season,
        "season_end": default_season,
        "season_type": "regular",
        "location": "both",
        "opponent": None,
        "last_n": None,
        "stat": "points",
    }


@router.get("/teams")
def get_teams():
    return {
        "teams": [
            "ATL", "BOS", "BKN", "CHA", "CHI", "CLE", "DAL", "DEN", "DET", "GSW",
            "HOU", "IND", "LAC", "LAL", "MEM", "MIA", "MIL", "MIN", "NOP", "NYK",
            "OKC", "ORL", "PHI", "PHX", "POR", "SAC", "SAS", "TOR", "UTA", "WAS"
        ]
    }