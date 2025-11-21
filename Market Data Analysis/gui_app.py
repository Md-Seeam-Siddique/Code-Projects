#author Md Seeam Siddique
#version 1.0
#date 12/10/2025

from pathlib import Path
from typing import Iterable

import pandas as pd
import tkinter as tk
from tkinter import ttk, messagebox

from src.association_mining import generate_association_rules, mine_frequent_itemsets
from src.basket_builder import create_basket
from src.data_loading import load_and_clean_data
from src.recommender import get_recommendations

DATA_PATH = Path(__file__).resolve().parent / "data" / "data.csv"


def format_itemset(items: Iterable[str]) -> str:
    """Return a sorted, comma-separated string for nicer display."""
    return ", ".join(sorted(items)) if items else "(empty)"


class MarketBasketGUI:
    def __init__(self, master: tk.Tk) -> None:
        self.master = master
        self.master.title("Market Basket Analysis & Recommender")
        self.master.geometry("900x600")

        self.transactions: pd.DataFrame = pd.DataFrame()
        self.basket: pd.DataFrame = pd.DataFrame()
        self.rules: pd.DataFrame = pd.DataFrame()

        self.support_var = tk.StringVar(value="0.02")
        self.lift_var = tk.StringVar(value="1.0")
        self.status_var = tk.StringVar(value="Loading dataset…")
        self.summary_var = tk.StringVar(value="")

        self._build_widgets()
        self._load_data()

    def _build_widgets(self) -> None:
        summary_frame = ttk.Frame(self.master, padding=10)
        summary_frame.pack(fill="x")
        ttk.Label(summary_frame, textvariable=self.summary_var, font=("Segoe UI", 11, "bold")).pack(
            anchor="w"
        )
        ttk.Label(summary_frame, textvariable=self.status_var).pack(anchor="w")

        controls = ttk.Frame(self.master, padding=10)
        controls.pack(fill="x")

        ttk.Label(controls, text="Min support:").grid(row=0, column=0, sticky="w")
        ttk.Entry(controls, width=8, textvariable=self.support_var).grid(row=0, column=1, padx=5)

        ttk.Label(controls, text="Min lift:").grid(row=0, column=2, sticky="w")
        ttk.Entry(controls, width=8, textvariable=self.lift_var).grid(row=0, column=3, padx=5)

        ttk.Button(controls, text="Refresh rules", command=self.refresh_rules).grid(
            row=0, column=4, padx=10
        )

        rules_frame = ttk.LabelFrame(self.master, text="Association rules", padding=10)
        rules_frame.pack(fill="both", expand=True, padx=10, pady=5)

        columns = ("antecedents", "consequents", "support", "confidence", "lift")
        self.rules_tree = ttk.Treeview(rules_frame, columns=columns, show="headings", height=12)
        for col in columns:
            self.rules_tree.heading(col, text=col.title())
            width = 200 if col in {"antecedents", "consequents"} else 90
            self.rules_tree.column(col, width=width, anchor="center")
        self.rules_tree.pack(fill="both", expand=True)
        ttk.Scrollbar(
            rules_frame, orient="vertical", command=self.rules_tree.yview
        ).pack(side="right", fill="y")
        self.rules_tree.configure(yscrollcommand=lambda *args: self.rules_tree.yview(*args))

        recomm_frame = ttk.LabelFrame(self.master, text="Product recommender", padding=10)
        recomm_frame.pack(fill="x", padx=10, pady=5)

        ttk.Label(recomm_frame, text="Products (comma-separated):").grid(row=0, column=0, sticky="w")
        self.items_entry = ttk.Entry(recomm_frame, width=80)
        self.items_entry.grid(row=1, column=0, columnspan=2, pady=5, sticky="we")
        self.items_entry.insert(
            0,
            "WHITE HANGING HEART T-LIGHT HOLDER, REGENCY CAKESTAND 3 TIER",
        )

        ttk.Button(recomm_frame, text="Recommend", command=self.show_recommendations).grid(
            row=1, column=2, padx=10
        )

        self.recommendations_list = tk.Listbox(recomm_frame, height=5, width=80)
        self.recommendations_list.grid(row=2, column=0, columnspan=2, pady=(5, 0), sticky="we")
        recomm_frame.grid_columnconfigure(0, weight=1)

    def _load_data(self) -> None:
        try:
            self.transactions = load_and_clean_data(str(DATA_PATH))
        except FileNotFoundError:
            messagebox.showerror("Dataset missing", f"Could not find {DATA_PATH}.")
            self.status_var.set("Dataset not found.")
            return
        except ValueError as exc:
            messagebox.showerror("Invalid dataset", str(exc))
            self.status_var.set("Dataset schema issue.")
            return

        if self.transactions.empty:
            self.status_var.set("No rows remain after cleaning. Check the CSV.")
            return

        self.basket = create_basket(self.transactions)
        if self.basket.empty:
            self.status_var.set("Basket matrix is empty. Adjust cleaning filters.")
            return

        summary = (
            f"Invoices: {self.basket.shape[0]:,}  |  Products: {self.basket.shape[1]:,}  |  "
            f"Transactions: {len(self.transactions):,}"
        )
        self.summary_var.set(summary)
        self.status_var.set("Loaded data successfully.")
        self.refresh_rules()

    def refresh_rules(self) -> None:
        if self.basket.empty:
            return
        try:
            min_support = float(self.support_var.get())
            min_lift = float(self.lift_var.get())
        except ValueError:
            messagebox.showwarning("Invalid input", "Please enter numeric values for support/lift.")
            return

        self.status_var.set("Mining rules… please wait.")
        self.master.update_idletasks()

        frequent_itemsets = mine_frequent_itemsets(self.basket, min_support=min_support)
        self.rules = generate_association_rules(
            frequent_itemsets,
            metric="lift",
            min_threshold=min_lift,
        )

        if self.rules.empty:
            self.status_var.set("No rules matched the current thresholds.")
        else:
            self.status_var.set(f"Generated {len(self.rules):,} rules.")
        self._populate_rules_tree()

    def _populate_rules_tree(self) -> None:
        for item in self.rules_tree.get_children():
            self.rules_tree.delete(item)

        if self.rules.empty:
            return

        for row in self.rules.itertuples(index=False):
            self.rules_tree.insert(
                "",
                "end",
                values=(
                    format_itemset(row.antecedents),
                    format_itemset(row.consequents),
                    f"{row.support:.3f}",
                    f"{row.confidence:.3f}",
                    f"{row.lift:.3f}",
                ),
            )

    def show_recommendations(self) -> None:
        if self.rules.empty:
            messagebox.showinfo("No rules", "Generate rules first (lower the thresholds if needed).")
            return

        items = [item.strip() for item in self.items_entry.get().split(",") if item.strip()]
        if not items:
            messagebox.showwarning("Missing input", "Enter at least one product name.")
            return

        recommendations = get_recommendations(items, self.rules, top_n=5)
        self.recommendations_list.delete(0, tk.END)

        if recommendations:
            for idx, product in enumerate(recommendations, start=1):
                self.recommendations_list.insert(tk.END, f"{idx}. {product}")
        else:
            self.recommendations_list.insert(tk.END, "No recommendations found for that basket.")


def main() -> None:
    root = tk.Tk()
    MarketBasketGUI(root)
    root.mainloop()


if __name__ == "__main__":
    main()
