package net.chrisrichardson.ftgo.orderservice.domain;

import net.chrisrichardson.ftgo.orderservice.api.events.OrderDomainEvent;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderState;

import java.time.Instant;

public class OrderCancelRequested implements OrderDomainEvent {
  private OrderState previousState;
  private CancellationReason reason;
  private String requestedBy;
  private Instant requestedAt;

  private OrderCancelRequested() {
  }

  public OrderCancelRequested(OrderState state) {
    this(state, CancellationReason.OTHER, "order-service", Instant.EPOCH);
  }

  public OrderCancelRequested(
          OrderState previousState,
          CancellationReason reason,
          String requestedBy,
          Instant requestedAt
  ) {
    this.previousState = previousState;
    this.reason = reason;
    this.requestedBy = requestedBy;
    this.requestedAt = requestedAt;
  }

  public OrderState getState() {
    return previousState;
  }

  public OrderState getPreviousState() {
    return previousState;
  }

  public CancellationReason getReason() {
    return reason;
  }

  public String getRequestedBy() {
    return requestedBy;
  }

  public Instant getRequestedAt() {
    return requestedAt;
  }
}
