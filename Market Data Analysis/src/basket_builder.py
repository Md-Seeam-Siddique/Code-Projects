import pandas as pd

_MIN_ITEM_OCCURRENCE = 3


def create_basket(df: pd.DataFrame) -> pd.DataFrame:
    """
    Build an invoice (rows) x product (columns) binary matrix from the
    cleaned transaction DataFrame.
    """
    if df.empty:
        return pd.DataFrame()

    invoice_product = (
        df.groupby(["InvoiceNo", "Description"])["Quantity"]
        .sum()
        .unstack(fill_value=0)
    )
    # Convert quantities into binary presence flags required by Apriori.
    basket = (invoice_product > 0).astype(int)

    if _MIN_ITEM_OCCURRENCE > 1 and not basket.empty:
        # Optionally drop rarely purchased products to reduce sparsity.
        frequent_cols = basket.columns[basket.sum(axis=0) >= _MIN_ITEM_OCCURRENCE]
        basket = basket[frequent_cols]

    return basket
