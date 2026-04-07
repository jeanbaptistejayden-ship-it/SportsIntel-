from fastapi import APIRouter

from API.services.player_service import (
    build_summary,
    fetch_gamelog,
    get_default_season,
    get_player_lookup,
    parse_games,
)

router = APIRouter()


@router.get("/player/{name}")
def get_player_stats(name: str, season: str | None = None, season_type: str = "Regular Season"):
    player, error = get_player_lookup(name)
    if error:
        return error

    player_id = player["id"]
    player_name = player["name"]

    chosen_season = season or get_default_season()
    gamelog_data = fetch_gamelog(player_id, season=chosen_season, season_type=season_type)
    games = parse_games(gamelog_data)

    if not games:
        return {"error": f"No games found for {player_name} ({chosen_season}, {season_type})."}

    summary = build_summary(player_name, games)
    return {
        "summary": summary,
        "meta": {"season": chosen_season, "season_type": season_type},
        "games": games,
    }
