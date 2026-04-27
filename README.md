# SportsIntel

A desktop NBA player statistics application that allows users to search for any NBA player and instantly retrieve filtered game log data, performance insights, and head-to-head player comparisons - all in one place.

> "Where Sports Data Becomes Intelligence"

## Tech Stack

| Layer | Technology |
|-------|------------|
| Frontend | JavaFX (Java 17+) |
| Backend | Python 3.12, FastAPI |
| Data Source | nba_api |
| Auth | Firebase |
| Dependency Management | Maven |
| Version Control | GitHub
| Design | Figma |

## Architecture

SportsIntel uses a client-server architecture. The JavaFX desktop frontend communicates with a local FastAPI backend via HTTP. The backend fetches and filters real NBA game log data using the nba_api library and returns structured JSON responses. Player headshots are loaded dynamically from the official NBA CDN. Firebase handles user authentication and session management.

## Project Structure 

```
SportsIntel/
├── .run/
│   └── SportsIntel [javafx_run].run.xml
├── API/
│   ├── data/
│   │   ├── __init__.py
│   │   └── players.py
│   ├── routers/
│   │   ├── __init__.py
│   │   └── player.py
│   ├── services/
│   │   ├── __init__.py
│   │   └── player_service.py
│   ├── app.py
│   └── main.py
├── src/main/
│   ├── java/com/sportsintel/
│   │   ├── CompareController.java
│   │   ├── CompareResultsController.java
│   │   ├── HelpController.java
│   │   ├── HomeController.java
│   │   ├── LoginController.java
│   │   ├── Main.java
│   │   ├── ResultsController.java
│   │   ├── SessionManager.java
│   │   ├── SignUpController.java
│   │   └── SplashController.java
│   └── resources/
│       ├── CompareResultsView.fxml
│       ├── CompareView.fxml
│       ├── HelpView.fxml
│       ├── HomeView.fxml
│       ├── LoginView.fxml
│       ├── ResultsView.fxml
│       ├── SignUpView.fxml
│       ├── SplashView.fxml
│       ├── lebron.png
│       ├── newlogo.png
│       └── styles.css
├── .gitignore
├── README.md
├── pom.xml
└── requirements.txt
```

## Setup

### Prerequisites
- Java 17+
- Python 3.12
- Maven

### Backend
```bash
cd API
pip install -r requirements.txt
uvicorn API.main:app --reload
```

### Frontend
Open the project in IntelliJ IDEA and run 'Main.java'.
Make sure the backend is running on port 8000 before launching the app.

## API Endpoints 

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/player/{name}` | Fetch filtered player game log and summary |
| GET | `/player/filters/reset` | Returns default filter values |
| GET | `/teams` | Returns all 30 NBA team abbreviations |

### Query Parameters for '/player/{name}'

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `season` | string | current | Season in `YYYY-YY` format e.g. `2024-25` |
| `season_type` | string | regular | `regular`, `playoffs`, or `both` |
| `location` | string | both | `home`, `away`, or `both` |
| `opponent` | string | null | NBA team abbreviation e.g. `LAL` |
| `last_n` | int | null | Limit results to last N games |
| `stat` | string | points | Stat to summarize |

### Example Request
GET/player/Lebron James?season=2024-2025&season_type=regular&opponent=NYK&stat=points

## Features

- Animated splash screen on launch
- Player game log retrieval by name
- Player headshots loaded from the NBA CDN
- Season filtering by year
- Season type filter (regular season, playoffs, or both)
- Home/away split filtering
- Opponent filtering across all 30 NBA teams
- Last N games filter
- Filter reset
- High/low game markers
- Season baseline comparison
- Last 5 and last 10 game rolling averages
- Home vs away stat splits
- Best and toughest matchup indicators
- Trend and recent form intelligence summaries
- Head-to-head player comparison (points, assists, rebounds, FG%, minutes)
- Win/lose visual indicators on comparison results
- User authentication via Firebase
- In-app help and support section

## Supported Stats

Points, Assists, Rebounds, Field Goal %, 3-Point %, Free Throw %,
Steals, Blocks, Turnovers, Minutes, Plus/Minus

## Project Management

This project was developed using Scrum methodology. Sprints were planned and
tracked using GitHub Issues with a structured backlog. The team held regular
scrum standups covering completed work, next steps, and blockers.

## Team 

| Name | Role |
|------|------|
| Jayden | GM |
| Daniel | Frontend / JavaFX |
| Brenda | Backend / API Integration |
| Matt | Backend / API |
| Valerie | Firebase |

## Notes

- Only NBA Basketball is currently supported. Additional sports are planned
  for future development.
- The application requires an active internet connection to fetch live NBA data.
- Login and sign-up are required to access profile features
