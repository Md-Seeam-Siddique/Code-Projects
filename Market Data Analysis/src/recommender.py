from typing import List

import pandas as pd


def get_recommendations(
    bought_items: List[str],
    rules: pd.DataFrame,
    top_n: int = 5,
) -> List[str]:
    """
    Recommend up to top_n product descriptions using association rules whose
    antecedents are subsets of the purchased items.
    """
    if top_n <= 0 or rules.empty:
        return []

    bought_set = {item.strip() for item in bought_items if item.strip()}
    if not bought_set:
        return []

    def antecedent_matches(antecedent: frozenset) -> bool:
        return antecedent.issubset(bought_set)

    # Keep only rules where all antecedents are present in the customer's basket.
    candidate_rules = rules[rules["antecedents"].apply(antecedent_matches)]
    if candidate_rules.empty:
        return []

    candidate_rules = candidate_rules.sort_values(["lift", "confidence"], ascending=False)
    recommendations: List[str] = []
    seen = set()

    for _, rule in candidate_rules.iterrows():
        for product in sorted(rule["consequents"]):
            if product in bought_set or product in seen:
                continue
            recommendations.append(product)
            seen.add(product)
            if len(recommendations) >= top_n:
                return recommendations

    return recommendations
