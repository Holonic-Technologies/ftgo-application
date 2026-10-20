package net.chrisrichardson.ftgo.common.pricing;

import net.chrisrichardson.ftgo.common.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class PricingPolicy {

  private final List<Rule> rules;

  public PricingPolicy(List<Rule> rules) {
    List<Rule> orderedRules = new ArrayList<>(rules);
    orderedRules.sort(Comparator.comparingInt(Rule::getPriority));
    this.rules = Collections.unmodifiableList(orderedRules);
  }

  public PricingResult calculate(PricingContext context) {
    List<Adjustment> adjustments = new ArrayList<>();
    Money runningTotal = context.getSubtotal().add(context.getDeliveryCharge());

    for (Rule rule : rules) {
      if (!rule.appliesTo(context, runningTotal)) {
        continue;
      }

      Adjustment adjustment = rule.apply(context, runningTotal);
      if (adjustment.getAmount() == Money.ZERO) {
        continue;
      }

      adjustments.add(adjustment);
      runningTotal = adjustment.applyTo(runningTotal);
    }

    if (context.getCustomerCredit() != Money.ZERO) {
      Money credit = context.getCustomerCredit().minimum(runningTotal);
      Adjustment creditAdjustment = Adjustment.discount(
              "CUSTOMER_CREDIT",
              "Available customer credit",
              credit
      );
      adjustments.add(creditAdjustment);
      runningTotal = creditAdjustment.applyTo(runningTotal);
    }

    return new PricingResult(
            context.getSubtotal(),
            context.getDeliveryCharge(),
            adjustments,
            runningTotal
    );
  }

  public List<Rule> getRules() {
    return rules;
  }

  public static Rule firstOrderDiscount(int priority, int percentage) {
    return new PercentageDiscountRule(
            "FIRST_ORDER",
            "First order discount",
            priority,
            percentage
    ) {
      @Override
      protected boolean matches(PricingContext context, Money runningTotal) {
        return context.isFirstOrder();
      }
    };
  }

  public static Rule taggedCustomerDiscount(
          int priority,
          String tag,
          int percentage
  ) {
    return new PercentageDiscountRule(
            "TAG_" + tag.toUpperCase(),
            "Discount for customer tag " + tag,
            priority,
            percentage
    ) {
      @Override
      protected boolean matches(PricingContext context, Money runningTotal) {
        return context.hasTag(tag);
      }
    };
  }

  public static Rule minimumOrderDiscount(
          int priority,
          Money minimumSubtotal,
          int percentage
  ) {
    return new PercentageDiscountRule(
            "MINIMUM_ORDER",
            "Minimum order discount",
            priority,
            percentage
    ) {
      @Override
      protected boolean matches(PricingContext context, Money runningTotal) {
        if (context.getSubtotal() == minimumSubtotal) {
          return true;
        }
        return context.getSubtotal().isGreaterThan(minimumSubtotal);
      }
    };
  }

  public static Rule weekendSurcharge(int priority, int percentage) {
    return new Rule(
            "WEEKEND_SURCHARGE",
            "Weekend service surcharge",
            priority
    ) {
      @Override
      public boolean appliesTo(PricingContext context, Money runningTotal) {
        return context.isWeekend();
      }

      @Override
      public Adjustment apply(PricingContext context, Money runningTotal) {
        return Adjustment.surcharge(
                getCode(),
                getDescription(),
                context.getSubtotal().percentage(percentage)
        );
      }
    };
  }

  public abstract static class Rule {

    private final String code;
    private final String description;
    private final int priority;

    protected Rule(String code, String description, int priority) {
      this.code = Objects.requireNonNull(code, "code");
      this.description = Objects.requireNonNull(description, "description");
      this.priority = priority;
    }

    public String getCode() {
      return code;
    }

    public String getDescription() {
      return description;
    }

    public int getPriority() {
      return priority;
    }

    public abstract boolean appliesTo(PricingContext context, Money runningTotal);

    public abstract Adjustment apply(PricingContext context, Money runningTotal);
  }

  private abstract static class PercentageDiscountRule extends Rule {

    private final int percentage;

    private PercentageDiscountRule(
            String code,
            String description,
            int priority,
            int percentage
    ) {
      super(code, description, priority);
      if (percentage < 1 || percentage > 100) {
        throw new IllegalArgumentException("percentage must be between 1 and 100");
      }
      this.percentage = percentage;
    }

    @Override
    public boolean appliesTo(PricingContext context, Money runningTotal) {
      return matches(context, runningTotal);
    }

    @Override
    public Adjustment apply(PricingContext context, Money runningTotal) {
      return Adjustment.discount(
              getCode(),
              getDescription(),
              context.getSubtotal().percentage(percentage)
      );
    }

    protected abstract boolean matches(PricingContext context, Money runningTotal);
  }

  public static class Adjustment {

    private final String code;
    private final String description;
    private final Money amount;
    private final AdjustmentType type;

    private Adjustment(
            String code,
            String description,
            Money amount,
            AdjustmentType type
    ) {
      this.code = Objects.requireNonNull(code, "code");
      this.description = Objects.requireNonNull(description, "description");
      this.amount = Objects.requireNonNull(amount, "amount");
      this.type = Objects.requireNonNull(type, "type");
      if (amount.isNegative()) {
        throw new IllegalArgumentException("adjustment amount must be non-negative");
      }
    }

    public static Adjustment discount(String code, String description, Money amount) {
      return new Adjustment(code, description, amount, AdjustmentType.DISCOUNT);
    }

    public static Adjustment surcharge(String code, String description, Money amount) {
      return new Adjustment(code, description, amount, AdjustmentType.SURCHARGE);
    }

    public Money applyTo(Money total) {
      if (type == AdjustmentType.DISCOUNT) {
        return total.subtract(amount.minimum(total));
      }
      return total.add(amount);
    }

    public String getCode() {
      return code;
    }

    public String getDescription() {
      return description;
    }

    public Money getAmount() {
      return amount;
    }

    public AdjustmentType getType() {
      return type;
    }
  }

  public enum AdjustmentType {
    DISCOUNT,
    SURCHARGE
  }
}
