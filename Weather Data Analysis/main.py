#author Md Seeam Siddique
#version 1.0
#date 15/11/2025

from __future__ import annotations

import argparse
import sys
import sqlite3
from typing import Callable, Optional

import analysis
import config
import data_import
import db


def _with_connection(db_path: str, action: Callable) -> Optional[int]:
    """Open a DB connection, run the callable, and ensure cleanup."""
    conn = db.get_connection(db_path)
    try:
        result = action(conn)
        conn.commit()
        return result
    finally:
        conn.close()


def handle_init_db(args: argparse.Namespace) -> None:
    """Create the database schema and seed default cities."""
    conn = db.init_db(args.db)
    conn.close()
    print(f"Database initialized at {args.db}")


def handle_list_cities(args: argparse.Namespace) -> None:
    """List available cities stored in the SQLite database."""
    def action(conn):
        db.ensure_default_cities(conn)
        cities = db.list_cities(conn)
        if not cities:
            print("No cities found. Use 'init-db' to seed defaults.")
            return
        print("Cities available in the database:")
        for city in cities:
            elevation = (
                f"{city['elevation']} m" if city["elevation"] is not None else "N/A"
            )
            print(
                f"- {city['name']} ({city['country']}) "
                f"[lat {city['latitude']}, lon {city['longitude']}, elev {elevation}]"
            )

    _with_connection(args.db, action)


def handle_import_data(args: argparse.Namespace) -> None:
    """Download Meteostat data for a city and persist it."""
    def action(conn):
        return data_import.import_daily_weather(
            conn, args.city, args.start, args.end
        )

    try:
        _with_connection(args.db, action)
    except ValueError as exc:
        print(f"Error: {exc}")


def handle_monthly_summary(args: argparse.Namespace) -> None:
    """Print average temperature and precipitation grouped by month."""
    def action(conn):
        analysis.print_monthly_summary(conn, args.city)

    try:
        _with_connection(args.db, action)
    except ValueError as exc:
        print(f"Error: {exc}")


def handle_extremes(args: argparse.Namespace) -> None:
    """Show the hottest and coldest recorded days for the city."""
    def action(conn):
        analysis.print_extreme_days(conn, args.city)

    try:
        _with_connection(args.db, action)
    except ValueError as exc:
        print(f"Error: {exc}")


def handle_rainy_days(args: argparse.Namespace) -> None:
    """Count rainy days per year above the configured precipitation threshold."""
    def action(conn):
        analysis.print_rainy_days_per_year(conn, args.city, args.threshold)

    try:
        _with_connection(args.db, action)
    except ValueError as exc:
        print(f"Error: {exc}")


def handle_compare_cities(args: argparse.Namespace) -> None:
    """Compare average annual temperatures between two cities."""
    def action(conn):
        analysis.print_city_comparison(conn, args.city1, args.city2)

    try:
        _with_connection(args.db, action)
    except ValueError as exc:
        print(f"Error: {exc}")

def handle_add_city(args: argparse.Namespace) -> None:
    """Insert a new city record into the database."""

    def action(conn):
        elevation = args.elevation
        return db.insert_city(
            conn,
            name=args.name,
            country=args.country,
            latitude=args.latitude,
            longitude=args.longitude,
            elevation=elevation,
        )

    try:
        new_id = _with_connection(args.db, action)
        print(f"City '{args.name}' ({args.country}) stored with id {new_id}.")
    except sqlite3.IntegrityError as exc:
        print(f"City already exists: {exc}")
    except ValueError as exc:
        print(f"Error: {exc}")


def build_parser() -> argparse.ArgumentParser:
    """Construct and return the CLI argument parser."""
    parser = argparse.ArgumentParser(
        description="City Weather History & Climate Explorer CLI"
    )
    parser.add_argument(
        "--db",
        default=config.DEFAULT_DB_PATH,
        help=f"Path to SQLite database file (default: {config.DEFAULT_DB_PATH})",
    )

    subparsers = parser.add_subparsers(dest="command", required=True)

    subparsers.add_parser("init-db", help="Create the database and seed default cities.").set_defaults(
        func=handle_init_db
    )

    subparsers.add_parser("list-cities", help="List all cities in the database.").set_defaults(
        func=handle_list_cities
    )

    add_city_parser = subparsers.add_parser(
        "add-city", help="Insert a new city with coordinates into the database."
    )
    add_city_parser.add_argument("--name", required=True, help="City name.")
    add_city_parser.add_argument("--country", required=True, help="Country name.")
    add_city_parser.add_argument(
        "--latitude", required=True, type=float, help="Latitude in decimal degrees."
    )
    add_city_parser.add_argument(
        "--longitude", required=True, type=float, help="Longitude in decimal degrees."
    )
    add_city_parser.add_argument(
        "--elevation",
        type=float,
        default=None,
        help="Elevation in meters (optional).",
    )
    add_city_parser.set_defaults(func=handle_add_city)

    import_parser = subparsers.add_parser(
        "import-data",
        help="Download Meteostat daily data for a city and store it in the database.",
    )
    import_parser.add_argument("--city", required=True, help="City name as stored in the database.")
    import_parser.add_argument("--start", required=True, help="Start date (YYYY-MM-DD).")
    import_parser.add_argument("--end", required=True, help="End date (YYYY-MM-DD).")
    import_parser.set_defaults(func=handle_import_data)

    monthly_parser = subparsers.add_parser(
        "monthly-summary", help="Show average temperature and precipitation by month."
    )
    monthly_parser.add_argument("--city", required=True, help="City name.")
    monthly_parser.set_defaults(func=handle_monthly_summary)

    extremes_parser = subparsers.add_parser(
        "extremes", help="Show hottest and coldest recorded days for a city."
    )
    extremes_parser.add_argument("--city", required=True, help="City name.")
    extremes_parser.set_defaults(func=handle_extremes)

    rainy_parser = subparsers.add_parser(
        "rainy-days", help="Count rainy days per year for a city."
    )
    rainy_parser.add_argument("--city", required=True, help="City name.")
    rainy_parser.add_argument(
        "--threshold",
        type=float,
        default=0.1,
        help="Rainfall threshold in mm to count as a rainy day (default: 0.1).",
    )
    rainy_parser.set_defaults(func=handle_rainy_days)

    compare_parser = subparsers.add_parser(
        "compare-cities", help="Compare yearly average temperatures for two cities."
    )
    compare_parser.add_argument("--city1", required=True, help="First city name.")
    compare_parser.add_argument("--city2", required=True, help="Second city name.")
    compare_parser.set_defaults(func=handle_compare_cities)

    return parser


def main(argv: Optional[list[str]] = None) -> None:
    """CLI entrypoint used by both scripts and tests."""
    parser = build_parser()
    args = parser.parse_args(argv)
    args.func(args)


if __name__ == "__main__":
    main(sys.argv[1:])
