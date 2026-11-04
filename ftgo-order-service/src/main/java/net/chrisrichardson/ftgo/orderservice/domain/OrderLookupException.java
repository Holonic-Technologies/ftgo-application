package net.chrisrichardson.ftgo.orderservice.domain;

public class OrderLookupException extends RuntimeException {
  public OrderLookupException(Long orderId) {
    super("Could not look up order " + orderId);
  }
}
