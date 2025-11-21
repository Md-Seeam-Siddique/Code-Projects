
#author Md Seeam Siddique
#version 1.0
#date 12/10/2025
 
from pathlib import Path
from typing import Iterable, List

import pandas as pd

from src.association_mining import generate_association_rules, mine_frequent_itemsets
from src.basket_builder import create_basket
from src.data_loading import load_and_clean_data
from src.recommender import get_recommendations

DATA_PATH = Path(__file__).resolve().parent / "data" / "data.csv"


def format_itemset(items: Iterable[str]) -> str:
    """Return a predictable, comma-separated representation of an itemset."""
    return ", ".join(sorted(items)) if items else "(empty)"


def show_top_rules(rules: pd.DataFrame, limit: int = 10) -> None:
    """Pretty-print the strongest association rules."""
    if rules.empty:
        print("No association rules are available yet. Try adjusting support thresholds.")
        return

    to_display = rules.head(limit)
    print(f"\nTop {len(to_display)} rules (sorted by lift and confidence):")
    for idx, (_, row) in enumerate(to_display.iterrows(), start=1):
        antecedents = format_itemset(row["antecedents"])
        consequents = format_itemset(row["consequents"])
        stats = (
            f"support: {row['support']:.3f}, "
            f"confidence: {row['confidence']:.3f}, "
            f"lift: {row['lift']:.3f}"
        )
        print(f"  {idx}. {antecedents} -> {consequents} | {stats}")
    print()


def prompt_bought_items() -> List[str]:
    """Ask the user for a comma-separated list of products."""
    raw_input = input(
        "Enter one or more product names separated by commas (as in the Description column): "
    )
    return [item.strip() for item in raw_input.split(",") if item.strip()]


def menu_loop(rules: pd.DataFrame) -> None:
    """Simple CLI loop for inspecting rules and fetching recommendations."""
    while True:
        print("Options:")
        print("  [1] Show top 10 association rules")
        print("  [2] Get product recommendations")
        print("  [3] Exit")
        choice = input("Select an option (1-3): ").strip()

        if choice == "1":
            show_top_rules(rules)
        elif choice == "2":
            if rules.empty:
                print("No rules are available to power recommendations yet.\n")
                continue
            items = prompt_bought_items()
            if not items:
                print("No items provided. Please try again.\n")
                continue
            recommendations = get_recommendations(items, rules, top_n=5)
            if recommendations:
                print("\nRecommended products:")
                for idx, product in enumerate(recommendations, start=1):
                    print(f"  {idx}. {product}")
                print()
            else:
                print("No recommendations were found for the supplied items.\n")
        elif choice == "3":
            print("Goodbye!")
            break
        else:
            print("Invalid option. Please choose 1, 2, or 3.\n")


def main() -> None:
    """Entry point that orchestrates loading, mining, and the CLI loop."""
    banner = "=" * 54
    print(banner)
    print("Market Basket Analysis & Recommender")
    print(banner)

    try:
        transactions = load_and_clean_data(str(DATA_PATH))
    except FileNotFoundError as exc:
        print(f"[Error] {exc}")
        return
    except ValueError as exc:
        print(f"[Error] {exc}")
        return

    print(f"Loaded {len(transactions):,} rows after cleaning.")
    basket = create_basket(transactions)
    if basket.empty:
        print("The basket matrix is empty. Please verify the dataset and filters.")
        return
    print(
        f"Created basket with {basket.shape[0]:,} invoices and {basket.shape[1]:,} products."
    )

    frequent_itemsets = mine_frequent_itemsets(basket, min_support=0.02)
    print(f"Found {len(frequent_itemsets):,} frequent itemsets (support >= 0.02).")
    rules = generate_association_rules(frequent_itemsets, metric="lift", min_threshold=1.0)
    print(f"Generated {len(rules):,} association rules.\n")

    menu_loop(rules)


if __name__ == "__main__":
    main()
