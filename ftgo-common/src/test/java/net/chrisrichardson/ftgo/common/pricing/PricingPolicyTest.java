package net.chrisrichardson.ftgo.common.pricing;

import net.chrisrichardson.ftgo.common.Money;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PricingPolicyTest {

  @Test
  public void appliesFirstOrderDiscount() {
    PricingPolicy policy = policyWith(
            PricingPolicy.firstOrderDiscount(10, 10)
    );
    PricingContext context = PricingContext.builder(new Money("40.00"))
            .deliveryCharge(new Money("5.00"))
            .priorOrderCount(0)
            .build();

    PricingResult result = policy.calculate(context);

    assertEquals(new Money("41.00"), result.getTotal());
    assertEquals(new Money("4.00"), result.totalDiscounts());
    assertEquals(1, result.getAdjustments().size());
    assertEquals("FIRST_ORDER", result.getAdjustments().get(0).getCode());
  }

  @Test
  public void doesNotApplyFirstOrderDiscountToReturningCustomer() {
    PricingPolicy policy = policyWith(
            PricingPolicy.firstOrderDiscount(10, 10)
    );
    PricingContext context = PricingContext.builder(new Money("40.00"))
            .priorOrderCount(3)
            .build();

    PricingResult result = policy.calculate(context);

    assertEquals(new Money("40.00"), result.getTotal());
    assertFalse(result.hasAdjustments());
  }

  @Test
  public void appliesTaggedCustomerDiscount() {
    PricingPolicy policy = policyWith(
            PricingPolicy.taggedCustomerDiscount(10, "vip", 15)
    );
    PricingContext context = PricingContext.builder(new Money("80.00"))
            .priorOrderCount(5)
            .addTag("vip")
            .build();

    PricingResult result = policy.calculate(context);

    assertEquals(new Money("68.00"), result.getTotal());
    assertEquals(new Money("12.00"), result.totalDiscounts());
  }

  @Test
  public void appliesMinimumOrderDiscountAtThreshold() {
    Money threshold = new Money("50.00");
    PricingPolicy policy = policyWith(
            PricingPolicy.minimumOrderDiscount(10, threshold, 20)
    );
    PricingContext context = PricingContext.builder(threshold)
            .priorOrderCount(2)
            .build();

    PricingResult result = policy.calculate(context);

    assertEquals(new Money("40.00"), result.getTotal());
  }

  @Test
  public void appliesMinimumOrderDiscountAboveThreshold() {
    PricingPolicy policy = policyWith(
            PricingPolicy.minimumOrderDiscount(10, new Money("50.00"), 20)
    );
    PricingContext context = PricingContext.builder(new Money("75.00"))
            .priorOrderCount(2)
            .build();

    PricingResult result = policy.calculate(context);

    assertEquals(new Money("60.00"), result.getTotal());
  }

  @Test
  public void skipsMinimumOrderDiscountBelowThreshold() {
    PricingPolicy policy = policyWith(
            PricingPolicy.minimumOrderDiscount(10, new Money("50.00"), 20)
    );
    PricingContext context = PricingContext.builder(new Money("49.99"))
            .priorOrderCount(2)
            .build();

    PricingResult result = policy.calculate(context);

    assertEquals(new Money("49.99"), result.getTotal());
    assertFalse(result.hasAdjustments());
  }

  @Test
  public void appliesWeekendSurcharge() {
    PricingPolicy policy = policyWith(
            PricingPolicy.weekendSurcharge(10, 5)
    );
    PricingContext context = PricingContext.builder(new Money("20.00"))
            .orderDate(LocalDate.of(2026, 10, 18))
            .build();

    PricingResult result = policy.calculate(context);

    assertEquals(new Money("21.00"), result.getTotal());
    assertEquals(new Money("1.00"), result.totalSurcharges());
  }

  @Test
  public void skipsWeekendSurchargeOnWeekday() {
    PricingPolicy policy = policyWith(
            PricingPolicy.weekendSurcharge(10, 5)
    );
    PricingContext context = PricingContext.builder(new Money("20.00"))
            .orderDate(LocalDate.of(2026, 10, 19))
            .build();

    PricingResult result = policy.calculate(context);

    assertEquals(new Money("20.00"), result.getTotal());
    assertFalse(result.hasAdjustments());
  }

  @Test
  public void combinesRulesInPriorityOrder() {
    PricingPolicy policy = new PricingPolicy(Arrays.asList(
            PricingPolicy.weekendSurcharge(30, 5),
            PricingPolicy.taggedCustomerDiscount(20, "vip", 10),
            PricingPolicy.firstOrderDiscount(10, 10)
    ));
    PricingContext context = PricingContext.builder(new Money("100.00"))
            .orderDate(LocalDate.of(2026, 10, 18))
            .addTag("vip")
            .build();

    PricingResult result = policy.calculate(context);

    assertEquals(new Money("85.00"), result.getTotal());
    assertEquals(3, result.getAdjustments().size());
    assertEquals("FIRST_ORDER", result.getAdjustments().get(0).getCode());
    assertEquals("TAG_VIP", result.getAdjustments().get(1).getCode());
    assertEquals("WEEKEND_SURCHARGE", result.getAdjustments().get(2).getCode());
  }

  @Test
  public void appliesCustomerCreditAfterPricingRules() {
    PricingPolicy policy = policyWith(
            PricingPolicy.firstOrderDiscount(10, 10)
    );
    PricingContext context = PricingContext.builder(new Money("50.00"))
            .customerCredit(new Money("12.00"))
            .build();

    PricingResult result = policy.calculate(context);

    assertEquals(new Money("33.00"), result.getTotal());
    assertEquals(new Money("17.00"), result.totalDiscounts());
    assertEquals("CUSTOMER_CREDIT", result.getAdjustments().get(1).getCode());
  }

  @Test
  public void capsCustomerCreditAtRunningTotal() {
    PricingPolicy policy = new PricingPolicy(Collections.emptyList());
    PricingContext context = PricingContext.builder(new Money("10.00"))
            .customerCredit(new Money("25.00"))
            .build();

    PricingResult result = policy.calculate(context);

    assertEquals(Money.ZERO, result.getTotal());
    assertTrue(result.hasAdjustments());
  }

  @Test(expected = IllegalArgumentException.class)
  public void rejectsNegativePriorOrderCount() {
    PricingContext.builder(new Money("10.00"))
            .priorOrderCount(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void rejectsInvalidDiscountPercentage() {
    PricingPolicy.firstOrderDiscount(10, 101);
  }

  private PricingPolicy policyWith(PricingPolicy.Rule rule) {
    return new PricingPolicy(Collections.singletonList(rule));
  }
}
