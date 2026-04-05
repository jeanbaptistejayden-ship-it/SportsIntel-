import requests

from API.data.players import PLAYERS


def get_player_lookup(name: str):
    player = PLAYERS.get(name.lower())
    if not player:
        return None, {"error": f"Could not find {name}. Available players: {list(PLAYERS.keys())}"}
    return player, None


def fetch_gamelog(player_id: int):
    gamelog_url = (
        "https://site.web.api.espn.com/apis/common/v3/sports/"
        f"basketball/nba/athletes/{player_id}/gamelog"
    )
    gamelog_response = requests.get(gamelog_url)
    return gamelog_response.json()


def parse_games(gamelog_data: dict):
    if "seasonTypes" not in gamelog_data:
        return None

    games = []
    for season in gamelog_data["seasonTypes"]:
        for category in season["categories"]:
            for event in category["events"]:
                stats = event["stats"]
                games.append(
                    {
                        "date": event.get("gameDate", ""),
                        "opponent": event.get("opponent", {}).get("abbreviation", ""),
                        "home": event.get("atVs", "") == "vs",
                        "pts": float(stats[13]),
                        "reb": float(stats[12]),
                        "ast": float(stats[10]),
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
