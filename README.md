# SportsIntel

A desktop NBA player statistics application that allows users to search for any NBA player and instantly retrieve filtered game log data, performance insights, and head-to-head player comparisons - all in one place.

> "Where Sports Data Becomes Intelligence"

## Tech Stack

| Layer | Technology |
|-------|------------|
| Frontend | JavaFX (Java 17+) |
| Backend | Python 3.12, Django REST Framework |
| Data Source | nba_api |
| Auth | Firebase |
| Design | Figma |

## Architecture

SportsIntel uses a client-server architecture. The JavaFX desktop frontend communicates with a local Django REST Framework backend via HTTP. The backend fetches and filters real NBA game log data using the nba_api library and returns structured JSON responses. Player headshots are loaded dynamically from the official NBA CDN. Firebase handles user authentication and session management.

## Project Structure 

```
SportsIntel/
├── .run/
│   └── SportsIntel [javafx_run].run.xml
├── API/                          # Django REST Backend
│   ├── sportsintel_backend/      # Django project config
│   │   ├── settings.py           # ⚠️  SENSITIVE - use settings.example.py as template
│   │   ├── settings.example.py   # Template for settings configuration
│   │   ├── urls.py
│   │   ├── wsgi.py
│   │   └── asgi.py
│   ├── players/                  # Django app for player endpoints
│   │   ├── views.py
│   │   ├── urls.py
│   │   ├── serializers.py
│   │   ├── models.py
│   │   └── migrations/
│   ├── services/
│   │   ├── __init__.py
│   │   └── player_service.py     # NBA API integration logic
│   ├── data/
│   │   ├── __init__.py
│   │   └── players.py            # Player lookup data
│   ├── manage.py
│   └── requirements.txt
├── archived_fastapi/             # Previous FastAPI implementation (preserved)
│   ├── app.py
│   ├── main.py
│   └── routers/
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
- Python 3.12+
- Maven
- Virtual environment (venv)

### Backend Setup (Django)

1. **Create environment file:**
   ```bash
   cd API
   cp sportsintel_backend/settings.example.py sportsintel_backend/settings.py
   ```

2. **Generate a secure SECRET_KEY:**
   ```bash
   python -c "from django.core.management.utils import get_random_secret_key; print(get_random_secret_key())"
   ```
   Update the `SECRET_KEY` in `API/sportsintel_backend/settings.py` with this value.

3. **Install dependencies:**
   ```bash
   pip install -r requirements.txt
   ```

4. **Run the Django server:**
   ```bash
   python manage.py runserver 0.0.0.0:8000
   ```
   The backend will be available at `http://127.0.0.1:8000`

### Frontend Setup (JavaFX)

1. Open the project in IntelliJ IDEA
2. Ensure the Django backend is running on port 8000
3. Run `Main.java` to launch the JavaFX application

### Running Both Services

**Terminal 1 - Backend:**
```bash
cd /path/to/SportsIntel-/SportsIntel-
source venv/bin/activate
cd API
python manage.py runserver 0.0.0.0:8000
```

**Terminal 2 - Frontend:**
```bash
# In IntelliJ IDEA, run Main.java
```

## Environment Configuration

The project uses a `.env`-style configuration. Edit `API/sportsintel_backend/settings.py` to customize:

```python
SECRET_KEY = 'your-secure-secret-key-here'  # Generate with Django's get_random_secret_key()
DEBUG = True                                  # Set to False in production
ALLOWED_HOSTS = ['localhost', '127.0.0.1']  # Add your domain in production
```

⚠️ **IMPORTANT:** Never commit `settings.py` to version control. Use `settings.example.py` as a template.

## API Endpoints (Django REST)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/players/search/` | Fetch filtered player game log and summary |
| GET | `/api/players/compare/` | Compare two players' stats |
| GET | `/api/players/gamelog/` | Get player game log data |
| GET | `/api/players/vs-opponent/` | Get player stats vs specific opponent |

### Query Parameters for `/api/players/search/`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `player` | string | required | Player name (e.g., `LeBron James`) |
| `season_start` | string | 2010 | Start season year (e.g., `2024`) |
| `season_end` | string | 2025 | End season year (e.g., `2025`) |
| `season_type` | string | regular | `regular`, `playoffs`, or `both` |
| `location` | string | both | `home`, `away`, or `both` |
| `opponent` | string | null | NBA team abbreviation (e.g., `LAL`, `BOS`) |
| `last_n` | int | null | Limit results to last N games |
| `stat` | string | pts | Stat to summarize |

### Query Parameters for `/api/players/compare/`

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `player1` | string | required | First player name |
| `player2` | string | required | Second player name |
| `opponent` | string | null | Filter by opponent team |
| `season_start` | string | 2014 | Start season year |
| `season_end` | string | 2025 | End season year |

### Example Requests

```
GET /api/players/search/?player=LeBron%20James&opponent=BOS&season_start=2024&season_end=2025

GET /api/players/compare/?player1=LeBron%20James&player2=Kevin%20Durant&opponent=CHI
```

### Example Response

```json
{
  "summary": {
    "ppg": 27.1,
    "rpg": 7.6,
    "apg": 7.4,
    "games_played": 28,
    "high": 61,
    "low": 12
  },
  "meta": {
    "season_range": "2014-2025",
    "player": "LeBron James"
  },
  "games": [...],
  "career_vs_opponent_summary": {...},
  "career_overview_summary": {...}
}
```

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

## Migration from FastAPI to Django

This project was originally built with **FastAPI** but has been migrated to **Django REST Framework**. The previous FastAPI implementation is in the `archived_fastapi/` folder for reference.


To use the archived FastAPI version:
```bash
cd archived_fastapi
```

**Current Implementation:** Uses Django REST Framework for all API endpoints (see [API Endpoints](#api-endpoints-django-rest) section above).

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
