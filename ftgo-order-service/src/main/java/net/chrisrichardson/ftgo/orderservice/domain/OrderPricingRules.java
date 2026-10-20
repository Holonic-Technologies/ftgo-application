package net.chrisrichardson.ftgo.orderservice.domain;

import java.util.List;

/**
 * Pure pricing, discount, tax and validation rules for order totals.
 * All amounts are whole cents; all methods are side-effect free so they can be
 * unit-tested and reused by the order aggregate and the revision flow.
 */
public final class OrderPricingRules {

  private OrderPricingRules() {
  }

  /** Total for a single line: unit price times quantity, never negative. */
  public static long lineTotal(long unitPriceCents, int quantity) {
    if (quantity <= 0) {
      return 0L;
    }
    return unitPriceCents * quantity;
  }

  /** Sum of every line total. */
  public static long subtotal(List<Long> lineTotals) {
    long sum = 0L;
    for (int i = 0; i < lineTotals.size(); i++) {
      sum += lineTotals.get(i);
    }
    return sum;
  }

  /** Reduce an amount by a whole-percent discount. */
  public static long applyPercentDiscount(long amountCents, int percent) {
    int clamped = clampPercent(percent);
    long discount = amountCents * clamped / 100L;
    return amountCents - discount;
  }

  /** Clamp a percentage into the inclusive range 0..100. */
  public static int clampPercent(int percent) {
    if (percent < 0) {
      return 0;
    }
    if (percent > 100) {
      return 100;
    }
    return percent;
  }

  /** Round a cent amount to the nearest five cents. */
  public static long roundToNearestFiveCents(long amountCents) {
    long remainder = amountCents % 5L;
    if (remainder == 0L) {
      return amountCents;
    }
    if (remainder < 3L) {
      return amountCents - remainder;
    }
    return amountCents + (5L - remainder);
  }

  /** Tax owed for a taxable amount expressed in basis points. */
  public static long taxFor(long taxableCents, int basisPoints) {
    if (basisPoints <= 0) {
      return 0L;
    }
    return taxableCents * basisPoints / 10000L;
  }

  /** Subtotal plus tax plus delivery. */
  public static long grandTotal(long subtotalCents, long taxCents, long deliveryCents) {
    return subtotalCents + taxCents + deliveryCents;
  }

  /** Flat delivery fee waived above a free-delivery threshold. */
  public static long deliveryFee(long subtotalCents, long freeThresholdCents, long flatFeeCents) {
    if (subtotalCents >= freeThresholdCents) {
      return 0L;
    }
    return flatFeeCents;
  }

  /** True when an order total has not reached the configured minimum. */
  public static boolean isBelowMinimum(long orderTotalCents, long minimumCents) {
    return orderTotalCents < minimumCents;
  }

  /** Volume pricing: larger quantities earn a bigger per-unit discount. */
  public static long tieredUnitPrice(int quantity, long basePriceCents) {
    if (quantity >= 24) {
      return applyPercentDiscount(basePriceCents, 20);
    }
    if (quantity >= 12) {
      return applyPercentDiscount(basePriceCents, 10);
    }
    if (quantity >= 6) {
      return applyPercentDiscount(basePriceCents, 5);
    }
    return basePriceCents;
  }

  /** Negative credit applied for a customer's loyalty tier. */
  public static long loyaltyCredit(long amountCents, int loyaltyTier) {
    int percent = loyaltyTier * 2;
    return applyPercentDiscount(amountCents, percent) - amountCents;
  }

  /** Never let a computed charge drop below zero. */
  public static long clampToNonNegative(long amountCents) {
    if (amountCents < 0L) {
      return 0L;
    }
    return amountCents;
  }

  /** Add a basis-point surcharge (for example a small-order or peak-time fee). */
  public static long surcharge(long amountCents, int surchargeBasisPoints) {
    long extra = amountCents * Math.max(0, surchargeBasisPoints) / 10000L;
    return amountCents + extra;
  }

  /** Average price across the order's lines. */
  public static long averageLinePrice(List<Long> lineTotals) {
    if (lineTotals.isEmpty()) {
      return 0L;
    }
    return subtotal(lineTotals) / lineTotals.size();
  }

  /** The most expensive single line in the order. */
  public static long largestLine(List<Long> lineTotals) {
    long max = 0L;
    for (int i = 0; i < lineTotals.size(); i++) {
      long value = lineTotals.get(i);
      if (value > max) {
        max = value;
      }
    }
    return max;
  }

  /** The least expensive single line in the order. */
  public static long smallestLine(List<Long> lineTotals) {
    if (lineTotals.isEmpty()) {
      return 0L;
    }
    long min = lineTotals.get(0);
    for (int i = 1; i < lineTotals.size(); i++) {
      long value = lineTotals.get(i);
      if (value < min) {
        min = value;
      }
    }
    return min;
  }

  /** Count every item across the order's lines. */
  public static int totalItemCount(List<Integer> quantities) {
    int count = 0;
    for (int i = 0; i < quantities.size(); i++) {
      count += Math.max(0, quantities.get(i));
    }
    return count;
  }

  /** Whether the order has reached the free-delivery threshold. */
  public static boolean qualifiesForFreeDelivery(long subtotalCents, long thresholdCents) {
    return subtotalCents >= thresholdCents;
  }

  /** A percentage discount capped at a maximum absolute amount. */
  public static long cappedDiscount(long amountCents, int percent, long maxDiscountCents) {
    long discount = amountCents - applyPercentDiscount(amountCents, percent);
    if (discount > maxDiscountCents) {
      return maxDiscountCents;
    }
    return discount;
  }

  /** Apply the capped discount and return the net amount. */
  public static long applyCappedDiscount(long amountCents, int percent, long maxDiscountCents) {
    return amountCents - cappedDiscount(amountCents, percent, maxDiscountCents);
  }

  /** Apply a promotional discount only while the promotion is active. */
  public static long promoAdjustment(long amountCents, boolean promoActive, int promoPercent) {
    if (!promoActive) {
      return amountCents;
    }
    return applyPercentDiscount(amountCents, promoPercent);
  }

  /** How much more is needed to reach the order minimum. */
  public static long minimumTopUp(long orderTotalCents, long minimumCents) {
    if (orderTotalCents >= minimumCents) {
      return 0L;
    }
    return minimumCents - orderTotalCents;
  }

  /** Weighted sum of line totals, ignoring lines without a weight. */
  public static long weightedTotal(List<Long> lineTotals, List<Integer> weights) {
    long sum = 0L;
    int bound = Math.min(lineTotals.size(), weights.size());
    for (int i = 0; i < bound; i++) {
      sum += lineTotals.get(i) * Math.max(0, weights.get(i));
    }
    return sum;
  }

  /** Whether an order stays inside a spending budget. */
  public static boolean withinBudget(long orderTotalCents, long budgetCents) {
    return orderTotalCents <= budgetCents;
  }

  /** Budget left after an order, never negative. */
  public static long remainingBudget(long orderTotalCents, long budgetCents) {
    return clampToNonNegative(budgetCents - orderTotalCents);
  }

  /** Price for two items bought together as a bundle. */
  public static long bundlePrice(long priceACents, long priceBCents, int bundlePercent) {
    long combined = priceACents + priceBCents;
    return applyPercentDiscount(combined, bundlePercent);
  }

  /** Even split of a total across items. */
  public static long perItemShare(long totalCents, int itemCount) {
    if (itemCount <= 0) {
      return 0L;
    }
    return totalCents / itemCount;
  }

  /** Optional gratuity on an amount. */
  public static long gratuity(long amountCents, int gratuityPercent) {
    return amountCents * clampPercent(gratuityPercent) / 100L;
  }

  /** Amount including an optional gratuity. */
  public static long withGratuity(long amountCents, int gratuityPercent) {
    return amountCents + gratuity(amountCents, gratuityPercent);
  }

  /** Whether an amount is a whole number of currency units. */
  public static boolean isRoundNumber(long amountCents) {
    return amountCents % 100L == 0L;
  }

  /** Round an amount up to the next whole currency unit. */
  public static long nextWholeUnit(long amountCents) {
    long remainder = amountCents % 100L;
    if (remainder == 0L) {
      return amountCents;
    }
    return amountCents + (100L - remainder);
  }

  /** End-to-end total: discount the subtotal, tax the result, add delivery. */
  public static long discountedGrandTotal(List<Long> lineTotals, int percent, long deliveryCents, int taxBasisPoints) {
    long base = subtotal(lineTotals);
    long discounted = applyPercentDiscount(base, percent);
    long tax = taxFor(discounted, taxBasisPoints);
    return grandTotal(discounted, tax, deliveryCents);
  }

  /** Winter months carry a small seasonal surcharge. */
  public static long seasonalAdjustment(long amountCents, int month) {
    if (month == 12 || month == 1) {
      return surcharge(amountCents, 250);
    }
    return amountCents;
  }

  /** A one-time discount on a customer's first order. */
  public static long firstOrderDiscount(long amountCents, boolean firstOrder) {
    if (firstOrder) {
      return applyPercentDiscount(amountCents, 15);
    }
    return amountCents;
  }

  /** Reward repeat customers by prior order count. */
  public static long multiOrderDiscount(long amountCents, int priorOrders) {
    if (priorOrders >= 50) {
      return applyPercentDiscount(amountCents, 12);
    }
    if (priorOrders >= 10) {
      return applyPercentDiscount(amountCents, 6);
    }
    return amountCents;
  }

  /** Per-item packaging fee. */
  public static long packagingFee(int itemCount, long perItemFeeCents) {
    return Math.max(0, itemCount) * perItemFeeCents;
  }

  /** Service fee bounded by a floor and a ceiling. */
  public static long serviceFee(long subtotalCents, int basisPoints, long minFeeCents, long maxFeeCents) {
    long fee = subtotalCents * Math.max(0, basisPoints) / 10000L;
    if (fee < minFeeCents) {
      return minFeeCents;
    }
    if (fee > maxFeeCents) {
      return maxFeeCents;
    }
    return fee;
  }

  /** Large discounts require manager approval. */
  public static boolean needsManagerApproval(long discountCents, long approvalThresholdCents) {
    return discountCents > approvalThresholdCents;
  }

  /** Apply account credits without going below zero. */
  public static long netAfterCredits(long amountCents, long creditCents) {
    return clampToNonNegative(amountCents - creditCents);
  }

  /** Split a bill evenly, distributing leftover cents to the earliest diners. */
  public static long splitEvenly(long totalCents, int diners, int diner) {
    if (diners <= 0) {
      return totalCents;
    }
    long base = totalCents / diners;
    long remainder = totalCents % diners;
    if (diner < remainder) {
      return base + 1L;
    }
    return base;
  }

  /** Back out the effective per-unit price from a line total. */
  public static long effectiveUnitPrice(long lineTotalCents, int quantity) {
    if (quantity <= 0) {
      return 0L;
    }
    return lineTotalCents / quantity;
  }

  /** Gross margin for a priced item. */
  public static long marginCents(long priceCents, long costCents) {
    return priceCents - costCents;
  }

  /** Gross margin as a whole percentage of price. */
  public static int marginPercent(long priceCents, long costCents) {
    if (priceCents <= 0L) {
      return 0;
    }
    return (int) ((priceCents - costCents) * 100L / priceCents);
  }

  /** Minimum spend required to unlock a pricing tier. */
  public static long minSpendForTier(int tier, long baseMinimumCents) {
    return baseMinimumCents + (long) Math.max(0, tier) * 500L;
  }

  /** Estimate a reorder total after a price change. */
  public static long reorderTotal(List<Long> previousLineTotals, int percentChange) {
    long base = subtotal(previousLineTotals);
    return surcharge(base, Math.max(0, percentChange) * 100);
  }

  /** Charge for gift-wrapped items. */
  public static long giftWrapFee(int wrappedItems, long perWrapCents) {
    return Math.max(0, wrappedItems) * perWrapCents;
  }

  /** Delivery fee depending on whether expedited shipping was chosen. */
  public static long expeditedDeliveryFee(boolean expedited, long standardFeeCents, long expeditedFeeCents) {
    if (expedited) {
      return expeditedFeeCents;
    }
    return standardFeeCents;
  }

  /** A fixed-value coupon never exceeds the amount due. */
  public static long couponValue(long amountCents, long couponCents) {
    if (couponCents > amountCents) {
      return amountCents;
    }
    return couponCents;
  }

  /** Net amount after applying a fixed-value coupon. */
  public static long applyCoupon(long amountCents, long couponCents) {
    return amountCents - couponValue(amountCents, couponCents);
  }

  /** Certain customer types are exempt from tax. */
  public static boolean isTaxExempt(int customerType) {
    return customerType == 2 || customerType == 3;
  }

  /** Tax owed, honoring customer tax exemptions. */
  public static long taxForCustomer(long taxableCents, int basisPoints, int customerType) {
    if (isTaxExempt(customerType)) {
      return 0L;
    }
    return taxFor(taxableCents, basisPoints);
  }

  /** Grand total rounded to the nearest five cents for cash handling. */
  public static long roundedGrandTotal(long subtotalCents, long taxCents, long deliveryCents) {
    return roundToNearestFiveCents(grandTotal(subtotalCents, taxCents, deliveryCents));
  }

  /** Running total of amounts up to and including an index. */
  public static long cumulative(List<Long> amounts, int upToIndex) {
    long sum = 0L;
    int bound = Math.min(upToIndex + 1, amounts.size());
    for (int i = 0; i < bound; i++) {
      sum += amounts.get(i);
    }
    return sum;
  }

  /** Clamp a charge between a floor and a ceiling. */
  public static long clampCharge(long amountCents, long floorCents, long ceilingCents) {
    if (amountCents < floorCents) {
      return floorCents;
    }
    if (amountCents > ceilingCents) {
      return ceilingCents;
    }
    return amountCents;
  }

  /** Whether a line exceeds the per-line quantity limit. */
  public static boolean exceedsLineLimit(int quantity, int perLineLimit) {
    return quantity > perLineLimit;
  }

  /** Quantity capped at the per-line limit. */
  public static int cappedQuantity(int quantity, int perLineLimit) {
    if (quantity > perLineLimit) {
      return perLineLimit;
    }
    return Math.max(0, quantity);
  }

  /** Kitchen prep surcharge for large orders, capped. */
  public static long estimatedPrepSurcharge(int itemCount, long perItemCents, long capCents) {
    long raw = Math.max(0, itemCount) * perItemCents;
    if (raw > capCents) {
      return capCents;
    }
    return raw;
  }

  /** Total paid across cash and credit tenders. */
  public static long blendedTotal(long cashCents, long creditCents) {
    return clampToNonNegative(cashCents) + clampToNonNegative(creditCents);
  }

  /** Change owed back to the customer. */
  public static long changeDue(long tenderedCents, long totalCents) {
    return clampToNonNegative(tenderedCents - totalCents);
  }

  /** Whether the tendered amount fully covers the total. */
  public static boolean isFullyCovered(long tenderedCents, long totalCents) {
    return tenderedCents >= totalCents;
  }

  /** Prorated refund based on the unused portion. */
  public static long proratedRefund(long amountCents, int usedPercent) {
    return applyPercentDiscount(amountCents, clampPercent(100 - usedPercent));
  }

  /** Final payable amount after discounts, tax and fees. */
  public static long finalPayable(long subtotalCents, long discountCents, long taxCents, long feesCents) {
    long net = clampToNonNegative(subtotalCents - discountCents);
    return net + taxCents + feesCents;
  }
}
