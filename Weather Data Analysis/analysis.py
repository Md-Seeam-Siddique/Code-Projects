"""Reporting and analysis helpers."""

from __future__ import annotations

import sqlite3
from typing import Dict, List, Optional

import db


def _get_city_or_raise(conn: sqlite3.Connection, city_name: str) -> sqlite3.Row:
    """Return the matching city row or raise a ValueError if missing."""
    city = db.get_city_by_name(conn, city_name)
    if city is None:
        raise ValueError(f"City '{city_name}' not found in database.")
    return city


def get_monthly_summary(
    conn: sqlite3.Connection, city_name: str
) -> List[Dict[str, Optional[float]]]:
    """Return average temperature and precipitation grouped by month."""
    city = _get_city_or_raise(conn, city_name)
    rows = conn.execute(
        """
        SELECT
            strftime('%Y-%m', date) AS year_month,
            AVG(tavg) AS avg_temp,
            SUM(prcp) AS total_precip
        FROM daily_weather
        WHERE city_id = ?
        GROUP BY year_month
        ORDER BY year_month;
        """,
        (city["id"],),
    ).fetchall()
    return [dict(row) for row in rows]


def print_monthly_summary(conn: sqlite3.Connection, city_name: str) -> None:
    """Print average temperature and precipitation grouped by month."""
    rows = get_monthly_summary(conn, city_name)
    if not rows:
        print(f"No weather data found for {city_name}.")
        return

    print(f"Monthly summary for {city_name}:")
    print("Year-Month | Avg Temp (°C) | Total Precip (mm)")
    for row in rows:
        avg_temp = f"{row['avg_temp']:.1f}" if row["avg_temp"] is not None else "N/A"
        total_precip = (
            f"{row['total_precip']:.1f}" if row["total_precip"] is not None else "N/A"
        )
        print(f"{row['year_month']} | {avg_temp:>13} | {total_precip:>17}")


def get_extreme_days(conn: sqlite3.Connection, city_name: str) -> Dict[str, Optional[dict]]:
    """Return the hottest and coldest recorded days for a city."""
    city = _get_city_or_raise(conn, city_name)
    hottest = conn.execute(
        """
        SELECT date, tmax FROM daily_weather
        WHERE city_id = ?
        ORDER BY tmax DESC
        LIMIT 1;
        """,
        (city["id"],),
    ).fetchone()
    coldest = conn.execute(
        """
        SELECT date, tmin FROM daily_weather
        WHERE city_id = ?
        ORDER BY tmin ASC
        LIMIT 1;
        """,
        (city["id"],),
    ).fetchone()
    return {
        "hottest": dict(hottest) if hottest else None,
        "coldest": dict(coldest) if coldest else None,
    }


def print_extreme_days(conn: sqlite3.Connection, city_name: str) -> None:
    """Print the hottest and coldest recorded day for a city."""
    extremes = get_extreme_days(conn, city_name)
    if not extremes["hottest"] and not extremes["coldest"]:
        print(f"No weather data available for {city_name}.")
        return

    print(f"Extreme temperature days for {city_name}:")
    if extremes["hottest"]:
        tmax = (
            f"{extremes['hottest']['tmax']:.1f}"
            if extremes["hottest"]["tmax"] is not None
            else "N/A"
        )
        print(
            f"Hottest : {extremes['hottest']['date']} "
            f"with Tmax {tmax} °C"
        )
    else:
        print("Hottest : No records available.")
    if extremes["coldest"]:
        tmin = (
            f"{extremes['coldest']['tmin']:.1f}"
            if extremes["coldest"]["tmin"] is not None
            else "N/A"
        )
        print(
            f"Coldest : {extremes['coldest']['date']} "
            f"with Tmin {tmin} °C"
        )
    else:
        print("Coldest : No records available.")


def get_rainy_days_per_year(
    conn: sqlite3.Connection, city_name: str, threshold_mm: float = 0.1
) -> List[Dict[str, int]]:
    """Return the count of rainy days per year for a city above the threshold."""
    city = _get_city_or_raise(conn, city_name)
    rows = conn.execute(
        """
        SELECT
            strftime('%Y', date) AS year,
            COUNT(*) AS rainy_days
        FROM daily_weather
        WHERE city_id = ?
          AND prcp IS NOT NULL
          AND prcp > ?
        GROUP BY year
        ORDER BY year;
        """,
        (city["id"], threshold_mm),
    ).fetchall()
    return [dict(row) for row in rows]


def print_rainy_days_per_year(
    conn: sqlite3.Connection, city_name: str, threshold_mm: float = 0.1
) -> None:
    """Print the count of rainy days per year for a city."""
    rows = get_rainy_days_per_year(conn, city_name, threshold_mm)
    if not rows:
        print(
            f"No rainy day data found for {city_name} using threshold {threshold_mm} mm."
        )
        return

    print(f"Rainy days per year for {city_name} (threshold {threshold_mm} mm):")
    print("Year | Rainy Days")
    for row in rows:
        print(f"{row['year']} | {row['rainy_days']}")


def get_city_comparison(
    conn: sqlite3.Connection, city1_name: str, city2_name: str
) -> List[Dict[str, Optional[float]]]:
    """Return yearly average temperatures for two cities side-by-side."""
    city1 = _get_city_or_raise(conn, city1_name)
    city2 = _get_city_or_raise(conn, city2_name)

    def _fetch_yearly_avg(city_id: int) -> Dict[str, Optional[float]]:
        data = conn.execute(
            """
            SELECT
                strftime('%Y', date) AS year,
                AVG(tavg) AS avg_temp
            FROM daily_weather
            WHERE city_id = ?
            GROUP BY year
            ORDER BY year;
            """,
            (city_id,),
        ).fetchall()
        return {row["year"]: row["avg_temp"] for row in data}

    city1_data = _fetch_yearly_avg(city1["id"])
    city2_data = _fetch_yearly_avg(city2["id"])
    years = sorted(set(city1_data.keys()) | set(city2_data.keys()))
    return [
        {
            "year": year,
            "city1_avg": city1_data.get(year),
            "city2_avg": city2_data.get(year),
        }
        for year in years
    ]


def print_city_comparison(
    conn: sqlite3.Connection, city1_name: str, city2_name: str
) -> None:
    """Compare annual average temperatures for two cities."""
    rows = get_city_comparison(conn, city1_name, city2_name)
    if not rows:
        print("No comparison data available for either city.")
        return

    print(f"Average annual temperature comparison: {city1_name} vs {city2_name}")
    print("Year | {:>18} | {:>18}".format(city1_name, city2_name))
    for row in rows:
        city1_val = (
            f"{row['city1_avg']:.1f}" if row["city1_avg"] is not None else "N/A"
        )
        city2_val = (
            f"{row['city2_avg']:.1f}" if row["city2_avg"] is not None else "N/A"
        )
        print(f"{row['year']} | {city1_val:>18} | {city2_val:>18}")
