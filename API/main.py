from fastapi import FastAPI
import requests

app = FastAPI()

PLAYERS = {
    "lebron james": {"id": 1966, "name": "LeBron James"},
    "stephen curry": {"id": 3975, "name": "Stephen Curry"},
    "kevin durant": {"id": 3202, "name": "Kevin Durant"},
    "giannis antetokounmpo": {"id": 3032977, "name": "Giannis Antetokounmpo"},
    "luka doncic": {"id": 3945274, "name": "Luka Doncic"},
    "jayson tatum": {"id": 4065648, "name": "Jayson Tatum"},
    "nikola jokic": {"id": 3112335, "name": "Nikola Jokic"},
    "joel embiid": {"id": 3059318, "name": "Joel Embiid"},
    "james harden": {"id": 3992, "name": "James Harden"},
    "anthony davis": {"id": 6583, "name": "Anthony Davis"},
    "devin booker": {"id": 3136193, "name": "Devin Booker"},
    "zion williamson": {"id": 4395628, "name": "Zion Williamson"},
    "donovan mitchell": {"id": 3908809, "name": "Donovan Mitchell"},
    "bam adebayo": {"id": 3907387, "name": "Bam Adebayo"},
    "draymond green": {"id": 473168, "name": "Draymond Green"},
    "klay thompson": {"id": 6475, "name": "Klay Thompson"},
    "jimmy butler": {"id": 6430, "name": "Jimmy Butler"},
    "kyrie irving": {"id": 6442, "name": "Kyrie Irving"},
    "chris paul": {"id": 2779, "name": "Chris Paul"},
    "karl-anthony towns": {"id": 3136776, "name": "Karl-Anthony Towns"},
    "shai gilgeous-alexander": {"id": 4278129, "name": "Shai Gilgeous-Alexander"},
    "de'aaron fox": {"id": 4066261, "name": "De'Aaron Fox"},
    "brandon ingram": {"id": 4066421, "name": "Brandon Ingram"},
    "zach lavine": {"id": 3064514, "name": "Zach LaVine"},
    "pascal siakam": {"id": 3149673, "name": "Pascal Siakam"},
    "julius randle": {"id": 2990984, "name": "Julius Randle"},
    "domantas sabonis": {"id": 3136779, "name": "Domantas Sabonis"},
    "andrew wiggins": {"id": 3059319, "name": "Andrew Wiggins"},
    "anthony edwards": {"id": 4594268, "name": "Anthony Edwards"},
    "evan mobley": {"id": 4433670, "name": "Evan Mobley"},
    "tyrese haliburton": {"id": 4433671, "name": "Tyrese Haliburton"},
    "cade cunningham": {"id": 4432172, "name": "Cade Cunningham"},
    "tyler herro": {"id": 4395651, "name": "Tyler Herro"},
    "franz wagner": {"id": 4433141, "name": "Franz Wagner"},
    "scottie barnes": {"id": 4433675, "name": "Scottie Barnes"},
    "og anunoby": {"id": 3934719, "name": "OG Anunoby"},
    "rudy gobert": {"id": 3032976, "name": "Rudy Gobert"},
    "paul george": {"id": 4251, "name": "Paul George"},
    "cj mccollum": {"id": 2490149, "name": "CJ McCollum"},
    "jaren jackson jr": {"id": 4065660, "name": "Jaren Jackson Jr"},
    "paolo banchero": {"id": 4432167, "name": "Paolo Banchero"},
    "lamelo ball": {"id": 4432174, "name": "LaMelo Ball"},
    "jalen williams": {"id": 4433670, "name": "Jalen Williams"},
    "alperen sengun": {"id": 4874304, "name": "Alperen Sengun"},
    "jaylen brown": {"id": 3917376, "name": "Jaylen Brown"},
    "josh giddey": {"id": 4874898, "name": "Josh Giddey"},
    "rj barrett": {"id": 4432166, "name": "RJ Barrett"},
    "khris middleton": {"id": 2528210, "name": "Khris Middleton"},
    "fred vanvleet": {"id": 3064440, "name": "Fred VanVleet"},
    "jrue holiday": {"id": 4563, "name": "Jrue Holiday"},
}

@app.get("/player/{name}")
def get_player_stats(name: str):

    # step 1 - look up the player in our dictionary
    player = PLAYERS.get(name.lower())

    if not player:
        return {"error": f"Could not find {name}. Available players: {list(PLAYERS.keys())}"}

    player_id = player["id"]
    player_name = player["name"]

    # step 2 - grab their game log from ESPN
    gamelog_url = f"https://site.web.api.espn.com/apis/common/v3/sports/basketball/nba/athletes/{player_id}/gamelog"
    gamelog_response = requests.get(gamelog_url)
    gamelog_data = gamelog_response.json()

    # step 3 - check if ESPN returned valid data
    if "seasonTypes" not in gamelog_data:
        return {"error": f"ESPN did not return game data for {player_name}"}

    # step 4 - loop through and collect stats
    games = []
    for season in gamelog_data["seasonTypes"]:
        for category in season["categories"]:
            for event in category["events"]:
                stats = event["stats"]
                games.append({
                    "date": event.get("gameDate", ""),
                    "opponent": event.get("opponent", {}).get("abbreviation", ""),
                    "home": event.get("atVs", "") == "vs",
                    "pts": float(stats[13]),
                    "reb": float(stats[12]),
                    "ast": float(stats[10]),
                })

    if not games:
        return {"error": f"No games found for {player_name}"}

    # step 5 - calculate the summary
    points = [g["pts"] for g in games]
    summary = {
        "player": player_name,
        "games_played": len(games),
        "avg_pts": round(sum(points) / len(points), 1),
        "high": max(points),
        "low": min(points),
    }

    return {
        "summary": summary,
        "games": games,
    }