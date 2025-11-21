# Market Basket Analysis & Product Recommender

A Python project that cleans a retail transactions CSV, builds an invoice by product basket, mines frequent itemsets with Apriori, and suggests complementary products through a CLI and Tkinter GUI.

## Features
- Cleans the dataset: trims descriptions, drops cancellations (InvoiceNo starting with C), removes negative quantity/price rows, and the duplicates.
- Builds an invoice by product binary matrix and prunes products purchased fewer than 3 times.
- Mines frequent itemsets with mlxtend.apriori and sorts association rules by lift then confidence.
- Command line menu to inspect rules and request recommendations.
- Tkinter desktop GUI with adjustable support/lift thresholds and inline recommendations.

## Project structure
- main.py - CLI entry that loads data, builds the basket, mines itemsets, and runs the menu.
- gui_app.py - Tkinter GUI with rule table and recommendation form.
- src/data_loading.py - Validates required columns and cleans the transactions.
- src/basket_builder.py - Converts transactions to the invoice x product basket.
- src/association_mining.py - Runs Apriori and generates association rules.
- src/recommender.py - Filters rules to match the shopper basket and returns top suggestions.
- requirements.txt - Python dependencies.
- data/data.csv - Place your dataset here (example dataset provided but unzip it first).

## Data requirements
Provide a CSV at data/data.csv with columns:
InvoiceNo, StockCode, Description, Quantity, InvoiceDate, UnitPrice, CustomerID, Country.
Notes:
- InvoiceNo is coerced to string; rows starting with C (cancellations/returns) are removed.
- Blank descriptions are dropped; Quantity and UnitPrice must be positive.
- Duplicate rows are removed to avoid double counting.

## Quick start
```bash
python -m venv .venv
# Linux/macOS
source .venv/bin/activate
# Windows PowerShell
.\.venv\Scripts\Activate.ps1

pip install -r requirements.txt

# ensure your dataset exists at data/data.csv
python main.py
```

## CLI usage
- Run python main.py from the project root.
- Default thresholds: min_support=0.02, min_lift=1.0.
- Menu options:
  - [1] Show top 10 association rules (sorted by lift/confidence).
  - [2] Enter comma-separated products to get up to five recommendations.
  - [3] Exit.

Example output (counts will depend on your dataset):
```text
======================================================
Market Basket Analysis & Recommender
======================================================
Loaded 26,278 rows after cleaning.
Created basket with 4,320 invoices and 1,235 products.
Found 218 frequent itemsets (support >= 0.02).
Generated 85 association rules.

Top 10 rules (sorted by lift and confidence):
1. ...
```

## GUI usage
- Run python gui_app.py in the same environment.
- Controls:
  - Min support and Min lift text inputs (float values).
  - Refresh rules re-mines with the chosen thresholds.
  - Recommend accepts comma-separated products and lists up to five suggestions.
- The summary bar shows invoice/product/transaction counts; the table lists association rules.

## How it works
1. load_and_clean_data reads the CSV, validates required columns, cleans, and filters.
2. create_basket aggregates to an invoice-product matrix and binarizes quantities (drops items bought fewer than 3 times by default).
3. mine_frequent_itemsets runs Apriori with the minimum support threshold.
4. generate_association_rules builds rules sorted by lift and confidence.
5. get_recommendations selects rules whose antecedents are subsets of the provided basket and returns unique consequents.

## Configuration tips
- Tweak _MIN_ITEM_OCCURRENCE in src/basket_builder.py to keep rarer products.
- Adjust default support/lift thresholds in main.py and the GUI defaults in gui_app.py.
- If you store the CSV elsewhere, update DATA_PATH in both entry points.

## Troubleshooting
- No rules produced: lower min support/lift or confirm the dataset has enough rows.
- Empty basket matrix: verify your CSV passes cleaning (positive quantity/price, InvoiceNo not starting with C, Description present).
- File not found: confirm data/data.csv exists relative to the project root.

Author: Md Seeam Siddique
Date : 12/10/2025
