"""Data import helpers built on Meteostat."""

from __future__ import annotations

from datetime import datetime
from typing import Iterable

import pandas as pd
from meteostat import Daily, Point

import db


def fetch_daily_weather_for_city(
    city_row, start_date: datetime, end_date: datetime
) -> pd.DataFrame:
    """Fetch daily weather data for the city using Meteostat."""
    elevation = city_row["elevation"] if city_row["elevation"] is not None else 0.0
    point = Point(
        city_row["latitude"],
        city_row["longitude"],
        elevation,
    )
    daily = Daily(point, start_date, end_date)
    df = daily.fetch()
    return df


def import_daily_weather(
    conn,
    city_name: str,
    start_date_str: str,
    end_date_str: str,
) -> int:
    """Download and store Meteostat daily data for the requested city."""
    city = db.get_city_by_name(conn, city_name)
    if city is None:
        raise ValueError(f"City '{city_name}' not found in database.")

    try:
        start_date = datetime.strptime(start_date_str, "%Y-%m-%d")
        end_date = datetime.strptime(end_date_str, "%Y-%m-%d")
    except ValueError as exc:
        raise ValueError("Dates must be formatted as YYYY-MM-DD.") from exc

    if end_date < start_date:
        raise ValueError("End date must be on or after the start date.")

    df = fetch_daily_weather_for_city(city, start_date, end_date)
    if df.empty:
        print(
            f"No weather data returned for {city['name']} "
            f"between {start_date_str} and {end_date_str}."
        )
        return 0

    df = df.reset_index().rename(columns={"time": "date"})

    field_names = ["tavg", "tmin", "tmax", "prcp", "wspd", "snow", "pres", "rhum"]

    rows: list[dict] = []
    for _, row in df.iterrows():
        date_value = row["date"]
        if isinstance(date_value, pd.Timestamp):
            date_str = date_value.strftime("%Y-%m-%d")
        else:
            date_str = str(date_value)[:10]
        entry = {"city_id": city["id"], "date": date_str}
        for field in field_names:
            entry[field] = None if pd.isna(row.get(field)) else row.get(field)
        rows.append(entry)

    inserted = db.upsert_daily_weather_rows(conn, city["id"], rows)
    print(
        f"Stored {inserted} daily rows for {city['name']} "
        f"from {start_date_str} to {end_date_str}."
    )
    return inserted
