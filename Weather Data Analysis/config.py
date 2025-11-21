"""Application-wide configuration helpers."""

from __future__ import annotations

from pathlib import Path

# Default SQLite database location.
DEFAULT_DB_PATH = str(Path("weather_history.db"))

# Default set of cities to seed the database with on first run.
DEFAULT_CITIES = [
    {
        "name": "St. John's",
        "country": "Canada",
        "latitude": 47.5615,
        "longitude": -52.7126,
        "elevation": 140.0,
    },
    {
        "name": "Toronto",
        "country": "Canada",
        "latitude": 43.6510,
        "longitude": -79.3470,
        "elevation": None,
    },
    {
        "name": "Vancouver",
        "country": "Canada",
        "latitude": 49.2827,
        "longitude": -123.1207,
        "elevation": None,
    },
    {
        "name": "London",
        "country": "United Kingdom",
        "latitude": 51.5074,
        "longitude": -0.1278,
        "elevation": None,
    },
    {
        "name": "Tokyo",
        "country": "Japan",
        "latitude": 35.6762,
        "longitude": 139.6503,
        "elevation": None,
    },
]
