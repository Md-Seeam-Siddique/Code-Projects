# City Weather History & Climate Explorer (Personal Project)

Python CLI + Tkinter desktop app that downloads historical daily weather data from Meteostat, stores it in a local SQLite database, and surfaces quick climate analyses per city.

## Features
- Seeded SQLite database with common cities; add more through the CLI or GUI.
- Imports Meteostat daily observations (temperature, precipitation, pressure, humidity, wind, snow) for any date range.
- Analysis commands: monthly averages, hottest/coldest days, rainy-day counts above a threshold, and yearly average temperature comparisons between cities.
- Re-runs are safe: daily rows are upserted, so you can refresh data without duplicates.
- Works offline once data is downloaded; single portable `.db` file.

## Requirements
- Python 3.10+
- Packages: `meteostat`, `pandas` (plus the standard library `sqlite3` and `tkinter`).
- No external database server needed.

## Setup and quick start
1. (Optional) Create and activate a virtual environment.
2. Install dependencies:
   ```bash
   pip install meteostat pandas
   ```
3. Initialize the database (creates tables and seeds cities):
   ```bash
   python main.py init-db
   ```
4. See available cities:
   ```bash
   python main.py list-cities
   ```
5. Import data for a city and date range:
   ```bash
   python main.py import-data --city "St. John's" --start 2015-01-01 --end 2024-12-31
   ```
6. Run analyses, for example:
   ```bash
   python main.py monthly-summary --city "St. John's"
   python main.py extremes --city "Tokyo"
   ```

Use `--db path/to/file.db` with any command to work with a different SQLite file than the default `weather_history.db`.

## CLI command reference
| Command | Example | Description |
| --- | --- | --- |
| `init-db` | `python main.py init-db` | Create the SQLite file, schema, and seed default cities. |
| `list-cities` | `python main.py list-cities` | Show all cities stored in the database. |
| `add-city` | `python main.py add-city --name Oslo --country Norway --latitude 59.9 --longitude 10.8 [--elevation 23]` | Insert a new city and its coordinates. |
| `import-data` | `python main.py import-data --city "London" --start 2010-01-01 --end 2020-12-31` | Download Meteostat daily data for the given range and city. |
| `monthly-summary` | `python main.py monthly-summary --city "Vancouver"` | Print monthly average temperatures and total precipitation. |
| `extremes` | `python main.py extremes --city "Tokyo"` | Display the hottest and coldest recorded days. |
| `rainy-days` | `python main.py rainy-days --city "London" --threshold 0.5` | Count rainy days per year above the precipitation threshold (default 0.1 mm). |
| `compare-cities` | `python main.py compare-cities --city1 "Vancouver" --city2 "Toronto"` | Compare average annual temperatures between two cities. |

## GUI quick start
1. Run the desktop app:
   ```bash
   python gui.py
   ```
2. Click **Initialize Database** on first launch to create the SQLite file and seed cities.
3. Add new cities in the **Add City** section if needed.
4. In **Import Historical Data**, pick a city and date range, then click **Import / Update Data**.
5. In **Analysis**, choose the analysis type (single city or city comparison), adjust the rain threshold if needed, and click **Run Analysis**. Results appear in the lower text area.

## Data and storage
- Default database location: `weather_history.db` in the project root (override with `--db`).
- Tables:
  - `cities`: id, name, country, latitude, longitude, elevation.
  - `daily_weather`: city_id, date, average/min/max temperature, precipitation, wind speed, snow, pressure, relative humidity, plus timestamps.
- Daily rows are stored per city/date; imports use upserts, so re-running for the same range refreshes data.

Default seeded cities: St. John's, Toronto, Vancouver, London, Tokyo. Extend by editing `config.DEFAULT_CITIES`, using `add-city`, or through the GUI.

## Project layout
```
analysis.py      # Reporting helpers for monthly summaries, extremes, rainy days, comparisons
config.py        # Default paths and seed city definitions
data_import.py   # Meteostat integration and ETL into SQLite
db.py            # SQLite schema creation and query utilities
gui.py           # Tkinter desktop front-end for imports and analyses
main.py          # Argparse-driven CLI entry point
README.md
```

## Notes and troubleshooting
- Meteostat enforces rate limits; use narrower date ranges if you hit throttling.
- If an import returns zero rows, double-check the city coordinates and date range; some stations lack full coverage.
- Analyses require imported data; run `import-data` before `monthly-summary`, `extremes`, `rainy-days`, or `compare-cities`.
- Keep separate database files (via `--db`) for experiments vs. production data.

Author: Md Seeam Siddique Date: 15/11/2025


