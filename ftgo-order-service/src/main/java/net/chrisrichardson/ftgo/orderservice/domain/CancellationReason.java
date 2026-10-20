package net.chrisrichardson.ftgo.orderservice.domain;

public enum CancellationReason {
  CUSTOMER_CHANGED_MIND,
  DUPLICATE_ORDER,
  DELIVERY_DELAY,
  RESTAURANT_UNAVAILABLE,
  PAYMENT_FAILURE,
  OTHER
}
