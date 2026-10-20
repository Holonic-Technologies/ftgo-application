package net.chrisrichardson.ftgo.common.governance;

import net.chrisrichardson.ftgo.common.Money;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MenuPriceLookup {

  private final Map<Long, Money> prices = new HashMap<>();

  public void recordPrice(long menuItemId, Money price) {
    prices.put(menuItemId, price);
  }

  public Money price(long menuItemId) {
    return findPrice(menuItemId).get();
  }

  private Optional<Money> findPrice(long menuItemId) {
    return Optional.ofNullable(prices.get(menuItemId));
  }
}
