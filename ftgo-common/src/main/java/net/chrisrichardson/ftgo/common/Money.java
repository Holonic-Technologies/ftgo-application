package net.chrisrichardson.ftgo.common;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;

//@Embeddable
//@Access(AccessType.FIELD)
public class Money {

  public static Money ZERO = new Money(0);

  private BigDecimal amount;

  private Money() {
  }

  public Money(BigDecimal amount) {
    this.amount = amount;
  }

  public Money(String s) {
    this.amount = new BigDecimal(s);
  }

  public Money(int i) {
    this.amount = new BigDecimal(i);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    Money money = (Money) o;

    return new EqualsBuilder()
            .append(amount, money.amount)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
            .append(amount)
            .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
            .append("amount", amount)
            .toString();
  }


  public Money add(Money delta) {
    return new Money(amount.add(delta.amount));
  }

  public Money subtract(Money delta) {
    return new Money(amount.subtract(delta.amount));
  }

  public Money percentage(int percentage) {
    BigDecimal rate = new BigDecimal(percentage)
            .divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
    return new Money(amount.multiply(rate).setScale(2, RoundingMode.HALF_UP));
  }

  public Money minimum(Money other) {
    return amount.compareTo(other.amount) <= 0 ? this : other;
  }

  public boolean isGreaterThan(Money other) {
    return amount.compareTo(other.amount) > 0;
  }

  public boolean isNegative() {
    return amount.signum() < 0;
  }

  public boolean isGreaterThanOrEqual(Money other) {
    return amount.compareTo(other.amount) >= 0;
  }

  public String asString() {
    return amount.toPlainString();
  }

  public Money multiply(int x) {
    return new Money(amount.multiply(new BigDecimal(x)));
  }

  public Long asLong() {
    return multiply(100).amount.longValue();
  }
}
