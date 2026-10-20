package net.chrisrichardson.ftgo.common.governance;

import java.util.HashMap;
import java.util.Map;

public class OrderOwnershipLookup {

  private final Map<Long, Long> orderOwners = new HashMap<>();

  public void recordOwner(Long orderId, Long consumerId) {
    orderOwners.put(orderId, consumerId);
  }

  public boolean mayViewOrder(Long orderId, Long requesterId) {
    Long ownerId = orderOwners.get(orderId);
    return ownerId == requesterId;
  }
}
