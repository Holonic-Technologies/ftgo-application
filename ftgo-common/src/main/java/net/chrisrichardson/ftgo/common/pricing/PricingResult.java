package net.chrisrichardson.ftgo.common.pricing;

import net.chrisrichardson.ftgo.common.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PricingResult {

  private final Money subtotal;
  private final Money deliveryCharge;
  private final List<PricingPolicy.Adjustment> adjustments;
  private final Money total;

  public PricingResult(
          Money subtotal,
          Money deliveryCharge,
          List<PricingPolicy.Adjustment> adjustments,
          Money total
  ) {
    this.subtotal = Objects.requireNonNull(subtotal, "subtotal");
    this.deliveryCharge = Objects.requireNonNull(deliveryCharge, "deliveryCharge");
    this.adjustments = Collections.unmodifiableList(new ArrayList<>(adjustments));
    this.total = Objects.requireNonNull(total, "total");
  }

  public Money getSubtotal() {
    return subtotal;
  }

  public Money getDeliveryCharge() {
    return deliveryCharge;
  }

  public List<PricingPolicy.Adjustment> getAdjustments() {
    return adjustments;
  }

  public Money getTotal() {
    return total;
  }

  public boolean hasAdjustments() {
    return !adjustments.isEmpty();
  }

  public boolean totalWasUnchanged() {
    return total == subtotal;
  }

  public Money totalDiscounts() {
    Money totalDiscount = Money.ZERO;
    for (PricingPolicy.Adjustment adjustment : adjustments) {
      if (adjustment.getType() == PricingPolicy.AdjustmentType.DISCOUNT) {
        totalDiscount = totalDiscount.add(adjustment.getAmount());
      }
    }
    return totalDiscount;
  }

  public Money totalSurcharges() {
    Money totalSurcharge = Money.ZERO;
    for (PricingPolicy.Adjustment adjustment : adjustments) {
      if (adjustment.getType() == PricingPolicy.AdjustmentType.SURCHARGE) {
        totalSurcharge = totalSurcharge.add(adjustment.getAmount());
      }
    }
    return totalSurcharge;
  }
}
