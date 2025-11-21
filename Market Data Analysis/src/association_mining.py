import pandas as pd
from mlxtend.frequent_patterns import apriori, association_rules


def mine_frequent_itemsets(basket: pd.DataFrame, min_support: float = 0.02) -> pd.DataFrame:
    """
    Run the Apriori algorithm on the basket matrix to find frequent itemsets.
    """
    if basket.empty:
        return pd.DataFrame(columns=["support", "itemsets"])

    frequent_itemsets = apriori(
        basket.astype(bool),
        min_support=min_support,
        use_colnames=True,
    )
    frequent_itemsets = frequent_itemsets.sort_values("support", ascending=False)
    frequent_itemsets = frequent_itemsets.reset_index(drop=True)

    return frequent_itemsets


def generate_association_rules(
    frequent_itemsets: pd.DataFrame,
    metric: str = "lift",
    min_threshold: float = 1.0,
) -> pd.DataFrame:
    """
    Generate association rules from frequent itemsets and sort them by lift
    and confidence in descending order.
    """
    if frequent_itemsets.empty:
        return pd.DataFrame(
            columns=["antecedents", "consequents", "support", "confidence", "lift"]
        )

    rules = association_rules(
        frequent_itemsets,
        metric=metric,
        min_threshold=min_threshold,
    )
    if rules.empty:
        return pd.DataFrame(
            columns=["antecedents", "consequents", "support", "confidence", "lift"]
        )

    rules = rules.sort_values(["lift", "confidence"], ascending=False)
    rules = rules.reset_index(drop=True)
    preferred_cols = ["antecedents", "consequents", "support", "confidence", "lift"]
    extra_cols = [col for col in rules.columns if col not in preferred_cols]

    return rules[preferred_cols + extra_cols]
