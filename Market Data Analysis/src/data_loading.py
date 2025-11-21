from typing import Set

import pandas as pd

REQUIRED_COLUMNS: Set[str] = {
    "InvoiceNo",
    "StockCode",
    "Description",
    "Quantity",
    "InvoiceDate",
    "UnitPrice",
    "CustomerID",
    "Country",
}


def load_and_clean_data(csv_path: str) -> pd.DataFrame:
    """
    Load the retail transactions CSV and return a cleaned DataFrame suitable
    for market basket analysis.
    """
    try:
        df = pd.read_csv(csv_path)
    except FileNotFoundError as exc:
        raise FileNotFoundError(
            f"Could not find the retail dataset at '{csv_path}'."
        ) from exc

    missing_columns = REQUIRED_COLUMNS.difference(df.columns)
    if missing_columns:
        missing = ", ".join(sorted(missing_columns))
        raise ValueError(
            "Dataset is missing expected columns: "
            f"{missing}"
        )

    df["InvoiceNo"] = df["InvoiceNo"].astype(str)

    # Drop rows without a description because they cannot be matched later.
    cleaned_df = df.dropna(subset=["Description"]).copy()
    cleaned_df["Description"] = cleaned_df["Description"].str.strip()
    # Remove cancelled invoices (InvoiceNo starting with 'C').
    cleaned_df = cleaned_df[~cleaned_df["InvoiceNo"].str.startswith("C")]
    # Keep only transactions with positive quantity and price.
    cleaned_df = cleaned_df[cleaned_df["Quantity"] > 0]
    cleaned_df = cleaned_df[cleaned_df["UnitPrice"] > 0]
    # Drop exact duplicates to avoid double-counting the same line item.
    cleaned_df = cleaned_df.drop_duplicates()

    return cleaned_df
