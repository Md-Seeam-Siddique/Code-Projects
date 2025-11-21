#author Md Seeam Siddique
#version 1.0
#date 15/11/2025

from __future__ import annotations

import tkinter as tk
from tkinter import messagebox, ttk

import sqlite3

import analysis
import config
import data_import
import db


class WeatherApp:
    """Simple GUI wrapper around the CLI functionality."""

    ANALYSIS_OPTIONS = [
        "Monthly summary",
        "Extremes",
        "Rainy days per year",
        "Compare cities",
    ]

    def __init__(self, root: tk.Tk) -> None:
        self.root = root
        self.root.title("City Weather History & Climate Explorer")

        self.db_path = config.DEFAULT_DB_PATH
        # Keep a mapping from combobox display strings to full city rows.
        self.city_map: dict[str, dict] = {}
        # Track every combobox that needs its values refreshed when cities change.
        self.city_comboboxes: list[ttk.Combobox] = []
        # Quick lookup from tk variable names to their UI widget (for enabling/disabling).
        self.widget_by_var: dict[str, tk.Widget] = {}

        self.import_city_var = tk.StringVar()
        self.analysis_type_var = tk.StringVar(value=self.ANALYSIS_OPTIONS[0])
        self.analysis_city_var = tk.StringVar()
        self.analysis_city1_var = tk.StringVar()
        self.analysis_city2_var = tk.StringVar()
        self.rain_threshold_var = tk.StringVar(value="0.1")
        self.new_city_name_var = tk.StringVar()
        self.new_city_country_var = tk.StringVar()
        self.new_city_lat_var = tk.StringVar()
        self.new_city_lon_var = tk.StringVar()
        self.new_city_elevation_var = tk.StringVar()

        self.status_var = tk.StringVar(value="Status: Ready")

        main_frame = ttk.Frame(root, padding=10)
        main_frame.pack(fill="both", expand=True)

        self._build_database_frame(main_frame)
        self._build_city_form(main_frame)
        self._build_import_frame(main_frame)
        self._build_analysis_frame(main_frame)

        # Prepare the database and populate the city controls.
        self.initialize_database(show_message=False)

    def _build_database_frame(self, parent: ttk.Frame) -> None:
        frame = ttk.LabelFrame(parent, text="Database")
        frame.pack(fill="x", pady=5)

        ttk.Label(frame, text=f"DB: {self.db_path}").grid(
            row=0, column=0, sticky="w", padx=5, pady=5
        )
        ttk.Button(
            frame, text="Initialize Database", command=self.initialize_database
        ).grid(row=0, column=1, sticky="e", padx=5, pady=5)

        ttk.Label(frame, textvariable=self.status_var).grid(
            row=1, column=0, columnspan=2, sticky="w", padx=5, pady=(0, 5)
        )

    def _build_city_form(self, parent: ttk.Frame) -> None:
        """Create inputs for adding new cities to the database."""
        frame = ttk.LabelFrame(parent, text="Add City")
        frame.pack(fill="x", pady=5)

        labels = ["Name:", "Country:", "Latitude:", "Longitude:", "Elevation (m, optional):"]
        vars_ = [
            self.new_city_name_var,
            self.new_city_country_var,
            self.new_city_lat_var,
            self.new_city_lon_var,
            self.new_city_elevation_var,
        ]
        for idx, (label, var) in enumerate(zip(labels, vars_)):
            ttk.Label(frame, text=label).grid(
                row=idx, column=0, sticky="e", padx=5, pady=2
            )
            entry = ttk.Entry(frame, textvariable=var, width=30)
            entry.grid(row=idx, column=1, sticky="w", padx=5, pady=2)

        ttk.Button(frame, text="Add City", command=self.add_city).grid(
            row=len(labels), column=0, columnspan=2, pady=5
        )

    def _build_import_frame(self, parent: ttk.Frame) -> None:
        frame = ttk.LabelFrame(parent, text="Import Historical Data")
        frame.pack(fill="x", pady=5)

        ttk.Label(frame, text="City:").grid(row=0, column=0, sticky="e", padx=5, pady=5)
        city_combo = ttk.Combobox(
            frame, textvariable=self.import_city_var, state="readonly", width=30
        )
        city_combo.grid(row=0, column=1, sticky="w", padx=5, pady=5)
        self.city_comboboxes.append(city_combo)
        self.widget_by_var[str(self.import_city_var)] = city_combo

        ttk.Label(frame, text="Start date (YYYY-MM-DD):").grid(
            row=1, column=0, sticky="e", padx=5, pady=5
        )
        self.start_entry = ttk.Entry(frame)
        self.start_entry.grid(row=1, column=1, sticky="w", padx=5, pady=5)

        ttk.Label(frame, text="End date (YYYY-MM-DD):").grid(
            row=2, column=0, sticky="e", padx=5, pady=5
        )
        self.end_entry = ttk.Entry(frame)
        self.end_entry.grid(row=2, column=1, sticky="w", padx=5, pady=5)

        ttk.Button(frame, text="Import / Update Data", command=self.import_data).grid(
            row=3, column=0, columnspan=2, pady=5
        )

        for child in frame.winfo_children():
            child.grid_configure(pady=3)

    def _build_analysis_frame(self, parent: ttk.Frame) -> None:
        frame = ttk.LabelFrame(parent, text="Analysis")
        frame.pack(fill="both", expand=True, pady=5)

        controls = ttk.Frame(frame)
        controls.grid(row=0, column=0, sticky="nsew", padx=5, pady=5)

        ttk.Label(controls, text="Analysis type:").grid(
            row=0, column=0, sticky="w", pady=2
        )
        type_combo = ttk.Combobox(
            controls,
            textvariable=self.analysis_type_var,
            values=self.ANALYSIS_OPTIONS,
            state="readonly",
            width=25,
        )
        type_combo.grid(row=1, column=0, sticky="w", pady=2)
        type_combo.current(0)
        type_combo.bind("<<ComboboxSelected>>", lambda _: self._update_analysis_fields())

        ttk.Label(controls, text="City:").grid(row=2, column=0, sticky="w", pady=(10, 2))
        analysis_city_combo = ttk.Combobox(
            controls, textvariable=self.analysis_city_var, state="readonly", width=30
        )
        analysis_city_combo.grid(row=3, column=0, sticky="w", pady=2)
        self.city_comboboxes.append(analysis_city_combo)
        self.widget_by_var[str(self.analysis_city_var)] = analysis_city_combo

        ttk.Label(controls, text="Rain threshold (mm):").grid(
            row=4, column=0, sticky="w", pady=(10, 2)
        )
        self.rain_threshold_entry = ttk.Entry(
            controls, textvariable=self.rain_threshold_var, width=10
        )
        self.rain_threshold_entry.grid(row=5, column=0, sticky="w", pady=2)
        self.widget_by_var[str(self.rain_threshold_var)] = self.rain_threshold_entry

        ttk.Label(controls, text="City 1:").grid(row=6, column=0, sticky="w", pady=(10, 2))
        analysis_city1_combo = ttk.Combobox(
            controls, textvariable=self.analysis_city1_var, state="readonly", width=30
        )
        analysis_city1_combo.grid(row=7, column=0, sticky="w", pady=2)
        self.city_comboboxes.append(analysis_city1_combo)
        self.widget_by_var[str(self.analysis_city1_var)] = analysis_city1_combo

        ttk.Label(controls, text="City 2:").grid(row=8, column=0, sticky="w", pady=(10, 2))
        analysis_city2_combo = ttk.Combobox(
            controls, textvariable=self.analysis_city2_var, state="readonly", width=30
        )
        analysis_city2_combo.grid(row=9, column=0, sticky="w", pady=2)
        self.city_comboboxes.append(analysis_city2_combo)
        self.widget_by_var[str(self.analysis_city2_var)] = analysis_city2_combo

        actions = ttk.Frame(frame)
        actions.grid(row=0, column=1, sticky="ne", padx=5, pady=5)

        ttk.Button(actions, text="Run Analysis", command=self.run_analysis).pack(
            fill="x", pady=2
        )
        ttk.Button(actions, text="Clear Output", command=self.clear_output).pack(
            fill="x", pady=2
        )

        output_frame = ttk.Frame(frame)
        output_frame.grid(row=1, column=0, columnspan=2, sticky="nsew", padx=5, pady=5)
        frame.rowconfigure(1, weight=1)
        frame.columnconfigure(0, weight=1)

        # Multiline text area for showing analysis output with a vertical scrollbar.
        self.output_text = tk.Text(output_frame, wrap="none", height=15)
        self.output_text.pack(side="left", fill="both", expand=True)
        scrollbar = ttk.Scrollbar(
            output_frame, orient="vertical", command=self.output_text.yview
        )
        scrollbar.pack(side="right", fill="y")
        self.output_text.configure(yscrollcommand=scrollbar.set)

        self._update_analysis_fields()
        self.clear_output()

    def initialize_database(self, show_message: bool = True) -> None:
        """Create tables, ensure default cities, and refresh combo boxes."""
        try:
            conn = db.init_db(self.db_path)
            db.ensure_default_cities(conn)
            conn.close()
            self.refresh_city_options()
            message = "Database initialized and default cities inserted."
            self.status_var.set(f"Status: {message}")
            if show_message:
                messagebox.showinfo("Database", message)
        except Exception as exc:  # pragma: no cover - GUI feedback
            messagebox.showerror("Database Error", str(exc))

    def refresh_city_options(self) -> None:
        """Reload city options from the database and update all comboboxes."""
        try:
            conn = db.get_connection(self.db_path)
            cities = db.list_cities(conn)
            conn.close()
        except Exception as exc:  # pragma: no cover - GUI feedback
            messagebox.showerror("Database Error", str(exc))
            return

        self.city_map = {}
        values: list[str] = []
        for city in cities:
            label = f"{city['name']} ({city['country']})"
            self.city_map[label] = city
            values.append(label)

        for combo in self.city_comboboxes:
            combo["values"] = values
            if values:
                combo.set(values[0])
            else:
                combo.set("")

    def add_city(self) -> None:
        """Insert a new city row based on form inputs."""
        name = self.new_city_name_var.get().strip()
        country = self.new_city_country_var.get().strip()
        lat = self.new_city_lat_var.get().strip()
        lon = self.new_city_lon_var.get().strip()
        elevation = self.new_city_elevation_var.get().strip()

        if not name or not country or not lat or not lon:
            messagebox.showwarning(
                "Input Error", "Name, country, latitude, and longitude are required."
            )
            return

        try:
            lat_val = float(lat)
            lon_val = float(lon)
            elevation_val = float(elevation) if elevation else None
        except ValueError:
            messagebox.showwarning(
                "Input Error", "Latitude, longitude, and elevation must be numeric."
            )
            return

        try:
            city_id = self._with_connection(
                lambda conn: db.insert_city(
                    conn,
                    name=name,
                    country=country,
                    latitude=lat_val,
                    longitude=lon_val,
                    elevation=elevation_val,
                )
            )
            self.refresh_city_options()
            for var in [
                self.new_city_name_var,
                self.new_city_country_var,
                self.new_city_lat_var,
                self.new_city_lon_var,
                self.new_city_elevation_var,
            ]:
                var.set("")
            message = f"City '{name}' added (id={city_id})."
            self.status_var.set(f"Status: {message}")
            messagebox.showinfo("Add City", message)
        except sqlite3.IntegrityError:
            messagebox.showinfo(
                "Add City",
                f"City '{name}' in {country} already exists in the database.",
            )
        except Exception as exc:  # pragma: no cover - GUI feedback
            messagebox.showerror("Add City Error", str(exc))

    def import_data(self) -> None:
        """Trigger Meteostat import for the selected city and range."""
        selection = self.import_city_var.get()
        start_date = self.start_entry.get().strip()
        end_date = self.end_entry.get().strip()

        if not selection:
            messagebox.showwarning("Input Error", "Please select a city to import.")
            return
        if not start_date or not end_date:
            messagebox.showwarning(
                "Input Error", "Start and end dates are required (YYYY-MM-DD)."
            )
            return

        city = self.city_map.get(selection)
        if city is None:
            messagebox.showwarning("Input Error", "Invalid city selection.")
            return

        try:
            inserted = self._with_connection(
                lambda conn: data_import.import_daily_weather(
                    conn, city["name"], start_date, end_date
                )
            )
            message = (
                f"Imported {inserted} records for {city['name']} "
                f"between {start_date} and {end_date}."
            )
            self.status_var.set(f"Status: {message}")
            messagebox.showinfo("Import", message)
        except Exception as exc:  # pragma: no cover - GUI feedback
            messagebox.showerror("Import Error", str(exc))

    def run_analysis(self) -> None:
        """Execute the selected analysis and display the results."""
        analysis_type = self.analysis_type_var.get()
        try:
            output = self._with_connection(
                lambda conn: self._execute_analysis(conn, analysis_type)
            )
            self.display_output(output)
        except ValueError as exc:
            messagebox.showwarning("Input Error", str(exc))
        except Exception as exc:  # pragma: no cover - GUI feedback
            messagebox.showerror("Analysis Error", str(exc))

    def _execute_analysis(self, conn, analysis_type: str) -> str:
        """Return formatted text for the chosen analysis type."""
        if analysis_type == "Monthly summary":
            city = self._get_city_from_var(self.analysis_city_var)
            rows = analysis.get_monthly_summary(conn, city["name"])
            if not rows:
                return f"No data available for {city['name']}."
            lines = ["Year-Month\tAvg Temp (°C)\tTotal Precip (mm)"]
            for row in rows:
                avg_temp = (
                    f"{row['avg_temp']:.1f}" if row["avg_temp"] is not None else "N/A"
                )
                total_precip = (
                    f"{row['total_precip']:.1f}"
                    if row["total_precip"] is not None
                    else "N/A"
                )
                lines.append(f"{row['year_month']}\t{avg_temp}\t{total_precip}")
            return "\n".join(lines)

        if analysis_type == "Extremes":
            city = self._get_city_from_var(self.analysis_city_var)
            extremes = analysis.get_extreme_days(conn, city["name"])
            if not extremes["hottest"] and not extremes["coldest"]:
                return f"No data available for {city['name']}."
            lines = [f"Extremes for {city['name']}:"]
            if extremes["hottest"]:
                tmax = (
                    f"{extremes['hottest']['tmax']:.1f}"
                    if extremes["hottest"]["tmax"] is not None
                    else "N/A"
                )
                lines.append(
                    f"Hottest: {extremes['hottest']['date']} "
                    f"(Tmax {tmax} °C)"
                )
            else:
                lines.append("Hottest: No record.")
            if extremes["coldest"]:
                tmin = (
                    f"{extremes['coldest']['tmin']:.1f}"
                    if extremes["coldest"]["tmin"] is not None
                    else "N/A"
                )
                lines.append(
                    f"Coldest: {extremes['coldest']['date']} "
                    f"(Tmin {tmin} °C)"
                )
            else:
                lines.append("Coldest: No record.")
            return "\n".join(lines)

        if analysis_type == "Rainy days per year":
            city = self._get_city_from_var(self.analysis_city_var)
            threshold = self._parse_threshold()
            rows = analysis.get_rainy_days_per_year(conn, city["name"], threshold)
            if not rows:
                return (
                    f"No rainy day data found for {city['name']} "
                    f"with threshold {threshold} mm."
                )
            lines = [f"Year\tRainy Days (> {threshold} mm)"]
            for row in rows:
                lines.append(f"{row['year']}\t{row['rainy_days']}")
            return "\n".join(lines)

        if analysis_type == "Compare cities":
            city1 = self._get_city_from_var(self.analysis_city1_var, label="City 1")
            city2 = self._get_city_from_var(self.analysis_city2_var, label="City 2")
            rows = analysis.get_city_comparison(conn, city1["name"], city2["name"])
            if not rows:
                return "No comparison data available."
            lines = [
                f"Year\t{city1['name']} Avg (°C)\t{city2['name']} Avg (°C)"
            ]
            for row in rows:
                city1_val = (
                    f"{row['city1_avg']:.1f}" if row["city1_avg"] is not None else "N/A"
                )
                city2_val = (
                    f"{row['city2_avg']:.1f}" if row["city2_avg"] is not None else "N/A"
                )
                lines.append(f"{row['year']}\t{city1_val}\t{city2_val}")
            return "\n".join(lines)

        raise ValueError("Unknown analysis type selected.")

    def _parse_threshold(self) -> float:
        """Parse the rain threshold entry into a float."""
        value = self.rain_threshold_var.get().strip()
        if not value:
            raise ValueError("Please enter a rain threshold value.")
        return float(value)

    def _get_city_from_var(self, var: tk.StringVar, label: str = "City") -> dict:
        """Look up the city row associated with the combobox selection."""
        selection = var.get()
        if not selection:
            raise ValueError(f"Please select {label.lower()}.")
        city = self.city_map.get(selection)
        if city is None:
            raise ValueError(f"Invalid selection for {label.lower()}.")
        return city

    def _with_connection(self, func):
        """Helper to open a temporary DB connection for an operation."""
        conn = db.get_connection(self.db_path)
        try:
            result = func(conn)
            conn.commit()
            return result
        finally:
            conn.close()

    def _update_analysis_fields(self) -> None:
        """Toggle the controls needed for the selected analysis."""
        analysis_type = self.analysis_type_var.get()
        single_city = analysis_type in {
            "Monthly summary",
            "Extremes",
            "Rainy days per year",
        }
        rain = analysis_type == "Rainy days per year"
        compare = analysis_type == "Compare cities"

        self._set_widget_state(self.analysis_city_var, single_city)
        self._set_widget_state(self.rain_threshold_var, rain)
        self._set_widget_state(self.analysis_city1_var, compare)
        self._set_widget_state(self.analysis_city2_var, compare)

    def _set_widget_state(self, var: tk.StringVar, enabled: bool) -> None:
        """Enable or disable widgets tied to the provided variable."""
        widget = self.widget_by_var.get(str(var))
        if widget is None:
            return

        if isinstance(widget, ttk.Combobox):
            widget.configure(state="readonly" if enabled else "disabled")
            if enabled and widget["values"] and not var.get():
                var.set(widget["values"][0])
        else:
            widget.configure(state="normal" if enabled else "disabled")

        if not enabled:
            var.set("")
        elif var is self.rain_threshold_var and not var.get():
            var.set("0.1")

    def display_output(self, text: str) -> None:
        """Render the supplied text in the output widget."""
        self.output_text.configure(state="normal")
        self.output_text.delete("1.0", "end")
        self.output_text.insert("1.0", text)
        self.output_text.configure(state="disabled")

    def clear_output(self) -> None:
        """Clear the analysis output area."""
        self.output_text.configure(state="normal")
        self.output_text.delete("1.0", "end")
        self.output_text.configure(state="disabled")


def main() -> None:
    """Launch the Tkinter event loop."""
    root = tk.Tk()
    WeatherApp(root)
    root.mainloop()


if __name__ == "__main__":
    main()
