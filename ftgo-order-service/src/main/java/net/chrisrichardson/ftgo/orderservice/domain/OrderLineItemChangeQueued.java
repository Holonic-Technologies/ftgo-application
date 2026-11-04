package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.common.DomainEvent;

public class OrderLineItemChangeQueued implements DomainEvent {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("OrderLineItemChangeQueued");
  }
  public OrderLineItemChangeQueued(String lineItemId, int newQuantity) {

  }
}
