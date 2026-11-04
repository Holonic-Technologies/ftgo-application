package net.chrisrichardson.ftgo.orderservice.domain;

public class OrderLookupException extends RuntimeException {
  public OrderLookupException(Long orderId) {
    super("Order not found: " + orderId);
  }
}
