from fastapi import APIRouter

from API.services.player_service import (
    build_summary,
    fetch_gamelog,
    get_player_lookup,
    parse_games,
)

router = APIRouter()


@router.get("/player/{name}")
def get_player_stats(name: str):
    player, error = get_player_lookup(name)
    if error:
        return error

    player_id = player["id"]
    player_name = player["name"]

    gamelog_data = fetch_gamelog(player_id)
    games = parse_games(gamelog_data)

    if games is None:
        return {"error": f"ESPN did not return game data for {player_name}"}

    if not games:
        return {"error": f"No games found for {player_name}"}

    summary = build_summary(player_name, games)
    return {"summary": summary, "games": games}
