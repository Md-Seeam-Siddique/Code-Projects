"""SQLite helpers for the Weather History application."""

from __future__ import annotations

import sqlite3
from pathlib import Path
from typing import Iterable, List, Optional

import config


def get_connection(db_path: str | Path) -> sqlite3.Connection:
    """Return a SQLite connection with row access as dictionaries."""
    db_file = Path(db_path)
    if db_file.parent and not db_file.parent.exists():
        db_file.parent.mkdir(parents=True, exist_ok=True)
    conn = sqlite3.connect(db_file)
    conn.row_factory = sqlite3.Row
    return conn


def init_db(db_path: str | Path) -> sqlite3.Connection:
    """Initialize the SQLite database schema and seed default cities."""
    conn = get_connection(db_path)
    with conn:
        conn.execute(
            """
            CREATE TABLE IF NOT EXISTS cities (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                country TEXT NOT NULL,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                elevation REAL,
                UNIQUE(name, country)
            );
            """
        )
        conn.execute(
            """
            CREATE TABLE IF NOT EXISTS daily_weather (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                city_id INTEGER NOT NULL,
                date TEXT NOT NULL,
                tavg REAL,
                tmin REAL,
                tmax REAL,
                prcp REAL,
                wspd REAL,
                snow REAL,
                pres REAL,
                rhum REAL,
                created_at TEXT DEFAULT (datetime('now')),
                updated_at TEXT DEFAULT (datetime('now')),
                UNIQUE(city_id, date),
                FOREIGN KEY (city_id) REFERENCES cities(id)
            );
            """
        )
        conn.execute(
            """
            CREATE INDEX IF NOT EXISTS idx_daily_weather_city_date
            ON daily_weather(city_id, date);
            """
        )
    ensure_default_cities(conn)
    return conn


def ensure_default_cities(conn: sqlite3.Connection) -> None:
    """Insert the default city set when the table is empty."""
    cur = conn.execute("SELECT COUNT(*) AS count FROM cities;")
    row = cur.fetchone()
    if row and row["count"]:
        return

    with conn:
        for city in config.DEFAULT_CITIES:
            insert_city(conn, **city)


def insert_city(
    conn: sqlite3.Connection,
    name: str,
    country: str,
    latitude: float,
    longitude: float,
    elevation: Optional[float] = None,
) -> int:
    """Insert a city row and return its ID."""
    cur = conn.execute(
        """
        INSERT OR IGNORE INTO cities (name, country, latitude, longitude, elevation)
        VALUES (?, ?, ?, ?, ?);
        """,
        (name, country, latitude, longitude, elevation),
    )
    if cur.lastrowid:
        return int(cur.lastrowid)
    # Fetch existing city ID if the record already existed.
    cur = conn.execute(
        """
        SELECT id FROM cities WHERE LOWER(name) = LOWER(?) AND LOWER(country) = LOWER(?);
        """,
        (name, country),
    )
    row = cur.fetchone()
    if row is None:
        raise sqlite3.IntegrityError("Failed to insert or locate city record.")
    return int(row["id"])


def get_city_by_name(conn: sqlite3.Connection, city_name: str) -> Optional[sqlite3.Row]:
    """Return a city row by name (case-insensitive)."""
    cur = conn.execute(
        """
        SELECT * FROM cities
        WHERE LOWER(name) = LOWER(?)
        LIMIT 1;
        """,
        (city_name,),
    )
    return cur.fetchone()


def list_cities(conn: sqlite3.Connection) -> List[sqlite3.Row]:
    """Return all city rows ordered alphabetically."""
    cur = conn.execute(
        """
        SELECT * FROM cities
        ORDER BY country, name;
        """
    )
    return cur.fetchall()


def upsert_daily_weather_rows(
    conn: sqlite3.Connection, city_id: int, rows: Iterable[dict]
) -> int:
    """Insert or update the provided daily weather rows.

    Returns the number of rows processed.
    """
    rows = list(rows)
    if not rows:
        return 0

    query = """
        INSERT INTO daily_weather (
            city_id, date, tavg, tmin, tmax, prcp, wspd, snow, pres, rhum
        ) VALUES (
            :city_id, :date, :tavg, :tmin, :tmax, :prcp, :wspd, :snow, :pres, :rhum
        )
        ON CONFLICT(city_id, date) DO UPDATE SET
            tavg = excluded.tavg,
            tmin = excluded.tmin,
            tmax = excluded.tmax,
            prcp = excluded.prcp,
            wspd = excluded.wspd,
            snow = excluded.snow,
            pres = excluded.pres,
            rhum = excluded.rhum,
            updated_at = CURRENT_TIMESTAMP;
    """
    with conn:
        conn.executemany(query, rows)
    return len(rows)
