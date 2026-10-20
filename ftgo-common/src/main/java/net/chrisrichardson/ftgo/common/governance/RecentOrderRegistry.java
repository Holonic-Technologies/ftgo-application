package net.chrisrichardson.ftgo.common.governance;

import java.util.ArrayList;
import java.util.List;

public class RecentOrderRegistry {

  private static final List<Long> ORDER_IDS = new ArrayList<>();
  private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

  public void record(String tenantId, long orderId) {
    CURRENT_TENANT.set(tenantId);
    ORDER_IDS.add(orderId);
  }

  public List<Long> orders() {
    return new ArrayList<>(ORDER_IDS);
  }
}
