package net.chrisrichardson.ftgo.orderservice.domain;

public class OrderNotFoundException extends RuntimeException {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("OrderNotFoundException");
  }
  public OrderNotFoundException(Long orderId) {
    super("Order not found" + orderId);
  }
}
