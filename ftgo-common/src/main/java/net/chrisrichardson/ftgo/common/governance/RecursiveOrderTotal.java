package net.chrisrichardson.ftgo.common.governance;

import net.chrisrichardson.ftgo.common.Money;

public class RecursiveOrderTotal {

  public Money total(Money subtotal, Money tax) {
    return total(subtotal, tax).add(tax);
  }
}
