from fastapi import APIRouter

from API.services.player_service import (
    build_summary,
    fetch_gamelog_by_type,
    filter_games_by_location,
    filter_games_by_opponent,
    get_default_season,
    get_player_lookup,
    limit_last_n_games,
    parse_games,
)

router = APIRouter()


@router.get("/player/{name}")
def get_player_stats(
    name: str,
    season: str | None = None,
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

    chosen_season = season or get_default_season()
    try:
        gamelog_data = fetch_gamelog_by_type(player_id, season=chosen_season, season_type=season_type)
    except ValueError as exc:
        return {"error": str(exc)}

    games = parse_games(gamelog_data)
    try:
        games = filter_games_by_location(games, location=location)
        games = filter_games_by_opponent(games, opponent=opponent)
        games = limit_last_n_games(games, last_n=last_n)
    except ValueError as exc:
        return {"error": str(exc)}

    if not games:
        return {
            "error": (
                f"No games found for {player_name} "
                f"({chosen_season}, {season_type}, {location}, {opponent}, {last_n})."
            )
        }

    try:
        summary = build_summary(player_name, games, stat=stat)
    except ValueError as exc:
        return {"error": str(exc)}

    return {
        "summary": summary,
        "meta": {
            "season": chosen_season,
            "season_type": season_type,
            "location": location,
            "opponent": opponent,
            "last_n": last_n,
            "stat": stat,
        },
        "games": games,
    }
