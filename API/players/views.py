from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
import sys
import os
import pandas as pd

# Add parent directory to path to import services
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from services.player_service import (
    build_summary,
    fetch_gamelog_range,
    fetch_player_vs_opponent,
    filter_games_by_location,
    filter_games_by_opponent,
    get_default_season,
    get_player_lookup,
    limit_last_n_games,
    parse_games,
    average_stat,
)


def sort_games_desc(games: list[dict]) -> list[dict]:
    from datetime import datetime

    def game_date_key(game: dict):
        try:
            return datetime.strptime(game["date"], "%b %d, %Y")
        except Exception:
            return datetime.min

    return sorted(games, key=game_date_key, reverse=True)


def parse_season_range(season_string: str) -> tuple[str, str]:
    """Converts 2025-26 format to ('2025', '2026')"""
    parts = season_string.split('-')
    if len(parts) == 2:
        start_year = parts[0]
        end_year_short = parts[1]
        century = start_year[:2]  # Get '20' from '2025'
        end_year = century + end_year_short
        return start_year, end_year
    return parts[0], parts[1]


def format_summary_for_java(summary: dict, include_all_stats: bool = False, games: list = None) -> dict:
    """Converts service summary to Java compatible format with ppg, rpg, apg fields
    
    Args:
        summary: The summary dict from build_summary
        include_all_stats: If True, compute all major stats (ppg, rpg, apg, mpg, etc.) regardless of requested stat
        games: List of game dicts, required if include_all_stats is True
    """
    stat_key = summary.get('stat', 'pts')
    average = summary.get('average', 0.0)
    
    formatted = {
        "player": summary.get("player"),
        "player_image": summary.get("player_image"),
        "stat": stat_key,
        "games_played": summary.get("games_played", 0),
        "average": average,
        "high": summary.get("high", 0.0),
        "low": summary.get("low", 0.0),
        "high_game": summary.get("high_game"),
        "low_game": summary.get("low_game"),
    }
    
    # Add stat specific fields for Java
    if stat_key == "pts":
        formatted["ppg"] = average
    elif stat_key == "reb":
        formatted["rpg"] = average
    elif stat_key == "ast":
        formatted["apg"] = average
    elif stat_key == "min":
        formatted["mpg"] = average
    elif stat_key == "fg_pct":
        formatted["fg_pct"] = average
    elif stat_key == "fg3_pct":
        formatted["fg3_pct"] = average
    elif stat_key == "ft_pct":
        formatted["ft_pct"] = average
    elif stat_key == "tov":
        formatted["tov"] = average
    elif stat_key == "blk":
        formatted["blk"] = average
    elif stat_key == "stl":
        formatted["stl"] = average
    
    # If requested, also add all other major stats for career summaries
    if include_all_stats and games is not None:
        # Always add the main stats that frontend expects
        formatted["ppg"] = average_stat(games, "pts")
        formatted["rpg"] = average_stat(games, "reb")
        formatted["apg"] = average_stat(games, "ast")
        formatted["mpg"] = average_stat(games, "min")
        formatted["spg"] = average_stat(games, "stl")
        formatted["bpg"] = average_stat(games, "blk")
        formatted["topg"] = average_stat(games, "tov")
        formatted["fg_pct"] = average_stat(games, "fg_pct")
        formatted["fg3_pct"] = average_stat(games, "fg3_pct")
        formatted["ft_pct"] = average_stat(games, "ft_pct")
    
    return formatted


@api_view(['GET'])
def player_search(request):
    """Search for a player and return their summary with stats"""
    try:
        name = request.query_params.get('name', '')
        season_start = request.query_params.get('season_start')
        season_end = request.query_params.get('season_end')
        season_type = request.query_params.get('season_type', 'regular')
        location = request.query_params.get('location', 'both')
        opponent = request.query_params.get('opponent')
        last_n = request.query_params.get('last_n')
        stat = request.query_params.get('stat', 'points')
        
        if not name:
            return Response(
                {"error": "Name parameter required"},
                status=status.HTTP_400_BAD_REQUEST
            )
        
        player_info, error = get_player_lookup(name)
        if error:
            return Response(error, status=status.HTTP_404_NOT_FOUND)
        
        player_id = player_info.get('id')
        player_name = player_info.get('name')
        print("PLAYER INFO:", player_info)

        # Determine what to fetch
        if not season_start or not season_end:
            # No filter entered: show career regular season stats only
            career_start = f"{player_info.get('from_year', 2003)}-{str(player_info.get('from_year', 2003) + 1)[-2:]}"
            career_end = get_default_season()

            games_df = fetch_gamelog_range(
                player_id,
                career_start,
                career_end,
                "regular"
            )

            display_season = "Career"
            season_type = "regular"

        else:
            # User selected a specific filter/range
            games_df = fetch_gamelog_range(
                player_id,
                season_start,
                season_end,
                season_type
            )

            display_season = f"{season_start}-{season_end}"


        games = parse_games(games_df)

        # Apply filters
        games = filter_games_by_location(games, location)
        games = filter_games_by_opponent(games, opponent)

        if last_n:
            games = limit_last_n_games(games, int(last_n))

        games_sorted = sort_games_desc(games)

        # Main summary
        summary = build_summary(player_name, player_id, games, stat)
        summary = format_summary_for_java(summary)

# For career summaries, use the same fetched data.
# Do not fetch both regular season and playoffs.
        games_career_parsed = parse_games(games_df)

        games_career_filtered = games_career_parsed.copy()
        games_career_filtered = filter_games_by_location(games_career_filtered, location)
        games_career_filtered = filter_games_by_opponent(games_career_filtered, opponent)

        career_vs_opponent_summary = build_summary(
            player_name,
            player_id,
            games_career_filtered,
            stat
        )
        career_vs_opponent_summary = format_summary_for_java(
            career_vs_opponent_summary,
            include_all_stats=True,
            games=games_career_filtered
        )

        career_overview_summary = build_summary(
            player_name,
            player_id,
            games_career_parsed,
            stat
        )
        career_overview_summary = format_summary_for_java(
            career_overview_summary,
            include_all_stats=True,
            games=games_career_parsed
        )

        recent_vs_opponent_games = games_sorted[:5] if games_sorted else []
        
        return Response({
            "summary": summary,
            "meta": {
                "season_range": display_season,
                "season": display_season,
                "player": player_name,
                "season_type": season_type,
                "location": location,
                "opponent": opponent if opponent else "all",
                "stat": stat
            },
            "games": games_sorted,
            "career_vs_opponent_summary": career_vs_opponent_summary,
            "career_overview_summary": career_overview_summary,
            "recent_vs_opponent_games": recent_vs_opponent_games
        })
    except Exception as e:
        return Response(
            {"error": str(e)},
            status=status.HTTP_500_INTERNAL_SERVER_ERROR
        )



@api_view(['GET'])
def player_summary(request):
    """Get player summary information"""
    try:
        name = request.query_params.get('name', '')
        if not name:
            return Response(
                {"error": "Name parameter required"},
                status=status.HTTP_400_BAD_REQUEST
            )
        
        summary = build_summary(name)
        return Response(summary)
    except Exception as e:
        return Response(
            {"error": str(e)},
            status=status.HTTP_500_INTERNAL_SERVER_ERROR
        )


@api_view(['GET'])
def player_gamelog(request):
    """Get player game log for a season range"""
    try:
        name = request.query_params.get('name', '')
        season_start = request.query_params.get('season_start')
        season_end = request.query_params.get('season_end')
        game_type = request.query_params.get('game_type', 'regular')
        
        if not name:
            return Response(
                {"error": "Name parameter required"},
                status=status.HTTP_400_BAD_REQUEST
            )
        
        player_info, error = get_player_lookup(name)
        if error:
            return Response(error, status=status.HTTP_404_NOT_FOUND)
        player_id = player_info.get('id')
        
        if not season_start or not season_end:
            default_season = get_default_season()
            season_start, season_end = parse_season_range(default_season)
        
        games_df = fetch_gamelog_range(player_id, season_start, season_end, game_type)
        games = parse_games(games_df)
        games = sort_games_desc(games)
        
        return Response({
            "player": name,
            "season_start": season_start,
            "season_end": season_end,
            "games": games
        })
    except Exception as e:
        return Response(
            {"error": str(e)},
            status=status.HTTP_500_INTERNAL_SERVER_ERROR
        )


@api_view(['GET'])
def player_compare(request):
    """Compare two players"""
    try:
        player1 = request.query_params.get('player1', '')
        player2 = request.query_params.get('player2', '')
        season_start = request.query_params.get('season_start')
        season_end = request.query_params.get('season_end')
        opponent = request.query_params.get('opponent')
        
        if not player1 or not player2:
            return Response(
                {"error": "Both player1 and player2 parameters required"},
                status=status.HTTP_400_BAD_REQUEST
            )
        
        player1_info, error1 = get_player_lookup(player1)
        if error1:
            return Response(error1, status=status.HTTP_404_NOT_FOUND)
        player1_id = player1_info.get('id')
        
        player2_info, error2 = get_player_lookup(player2)
        if error2:
            return Response(error2, status=status.HTTP_404_NOT_FOUND)
        player2_id = player2_info.get('id')
        
        if not season_start or not season_end:
            season_start = "2014"
            season_end = "2025"
        
        games1_df = fetch_gamelog_range(player1_id, season_start, season_end)
        games1 = parse_games(games1_df)
        
        games2_df = fetch_gamelog_range(player2_id, season_start, season_end)
        games2 = parse_games(games2_df)
        
        # Apply opponent filter if provided
        if opponent:
            games1 = filter_games_by_opponent(games1, opponent)
            games2 = filter_games_by_opponent(games2, opponent)
        
        comparison = {
            "player_one": {
                "player": player1,
                "ppg": average_stat(games1, "pts"),
                "rpg": average_stat(games1, "reb"),
                "apg": average_stat(games1, "ast"),
                "mpg": average_stat(games1, "min"),
                "fg_pct": average_stat(games1, "fg_pct"),
                "games_played": len(games1),
            },
            "player_two": {
                "player": player2,
                "ppg": average_stat(games2, "pts"),
                "rpg": average_stat(games2, "reb"),
                "apg": average_stat(games2, "ast"),
                "mpg": average_stat(games2, "min"),
                "fg_pct": average_stat(games2, "fg_pct"),
                "games_played": len(games2),
            }
        }
        
        return Response(comparison)
    except Exception as e:
        return Response(
            {"error": str(e)},
            status=status.HTTP_500_INTERNAL_SERVER_ERROR
        )


@api_view(['GET'])
def player_vs_opponent(request):
    """Get player stats vs opponent"""
    try:
        name = request.query_params.get('name', '')
        opponent = request.query_params.get('opponent', '')
        season_start = request.query_params.get('season_start')
        season_end = request.query_params.get('season_end')
        
        if not name or not opponent:
            return Response(
                {"error": "Name and opponent parameters required"},
                status=status.HTTP_400_BAD_REQUEST
            )
        
        player_info, error = get_player_lookup(name)
        if error:
            return Response(error, status=status.HTTP_404_NOT_FOUND)
        player_id = player_info.get('id')
        
        if not season_start or not season_end:
            default_season = get_default_season()
            season_start, season_end = parse_season_range(default_season)
        
        games_df = fetch_player_vs_opponent(player_id, opponent, season_start, season_end)
        games = parse_games(games_df)
        
        return Response({
            "player": name,
            "opponent": opponent,
            "games": games
        })
    except Exception as e:
        return Response(
            {"error": str(e)},
            status=status.HTTP_500_INTERNAL_SERVER_ERROR
        )

