package net.chrisrichardson.ftgo.common.pricing;

import net.chrisrichardson.ftgo.common.Money;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PricingContext {

  private final Money subtotal;
  private final Money deliveryCharge;
  private final Money customerCredit;
  private final String customerId;
  private final String restaurantId;
  private final LocalDate orderDate;
  private final int priorOrderCount;
  private final Set<String> tags;

  private PricingContext(Builder builder) {
    subtotal = Objects.requireNonNull(builder.subtotal, "subtotal");
    deliveryCharge = Objects.requireNonNull(builder.deliveryCharge, "deliveryCharge");
    customerCredit = Objects.requireNonNull(builder.customerCredit, "customerCredit");
    customerId = Objects.requireNonNull(builder.customerId, "customerId");
    restaurantId = Objects.requireNonNull(builder.restaurantId, "restaurantId");
    orderDate = Objects.requireNonNull(builder.orderDate, "orderDate");
    priorOrderCount = builder.priorOrderCount;
    tags = Collections.unmodifiableSet(new HashSet<>(builder.tags));
  }

  public Money getSubtotal() {
    return subtotal;
  }

  public Money getDeliveryCharge() {
    return deliveryCharge;
  }

  public Money getCustomerCredit() {
    return customerCredit;
  }

  public String getCustomerId() {
    return customerId;
  }

  public String getRestaurantId() {
    return restaurantId;
  }

  public LocalDate getOrderDate() {
    return orderDate;
  }

  public int getPriorOrderCount() {
    return priorOrderCount;
  }

  public Set<String> getTags() {
    return tags;
  }

  public boolean isWeekend() {
    DayOfWeek day = orderDate.getDayOfWeek();
    return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
  }

  public boolean hasTag(String tag) {
    return tags.contains(tag);
  }

  public boolean isFirstOrder() {
    return priorOrderCount == 0;
  }

  public static Builder builder(Money subtotal) {
    return new Builder(subtotal);
  }

  public static class Builder {

    private final Money subtotal;
    private Money deliveryCharge = Money.ZERO;
    private Money customerCredit = Money.ZERO;
    private String customerId = "anonymous";
    private String restaurantId = "unknown";
    private LocalDate orderDate = LocalDate.now();
    private int priorOrderCount;
    private Set<String> tags = new HashSet<>();

    private Builder(Money subtotal) {
      this.subtotal = subtotal;
    }

    public Builder deliveryCharge(Money deliveryCharge) {
      this.deliveryCharge = deliveryCharge;
      return this;
    }

    public Builder customerCredit(Money customerCredit) {
      this.customerCredit = customerCredit;
      return this;
    }

    public Builder customerId(String customerId) {
      this.customerId = customerId;
      return this;
    }

    public Builder restaurantId(String restaurantId) {
      this.restaurantId = restaurantId;
      return this;
    }

    public Builder orderDate(LocalDate orderDate) {
      this.orderDate = orderDate;
      return this;
    }

    public Builder priorOrderCount(int priorOrderCount) {
      if (priorOrderCount < 0) {
        throw new IllegalArgumentException("priorOrderCount must be non-negative");
      }
      this.priorOrderCount = priorOrderCount;
      return this;
    }

    public Builder addTag(String tag) {
      tags.add(tag);
      return this;
    }

    public PricingContext build() {
      return new PricingContext(this);
    }
  }
}
