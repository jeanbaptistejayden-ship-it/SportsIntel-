from datetime import datetime

from nba_api.stats.endpoints import playergamelog
from nba_api.stats.static import players as nba_players


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
    endpoint = playergamelog.PlayerGameLog(
        player_id=player_id,
        season=season,
        season_type_all_star=season_type,
        timeout=30,
    )
    data_frames = endpoint.get_data_frames()
    return data_frames[0] if data_frames else None


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
                "pts": float(row.get("PTS", 0)),
                "reb": float(row.get("REB", 0)),
                "ast": float(row.get("AST", 0)),
            }
        )
    return games


def build_summary(player_name: str, games: list[dict]):
    points = [g["pts"] for g in games]
    return {
        "player": player_name,
        "games_played": len(games),
        "avg_pts": round(sum(points) / len(points), 1),
        "high": max(points),
        "low": min(points),
    }
