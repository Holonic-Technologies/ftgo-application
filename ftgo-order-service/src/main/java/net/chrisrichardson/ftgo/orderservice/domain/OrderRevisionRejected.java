package net.chrisrichardson.ftgo.orderservice.domain;


import io.eventuate.tram.events.common.DomainEvent;

public class OrderRevisionRejected implements DomainEvent {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("OrderRevisionRejected");
  }

  public OrderRevisionRejected(OrderRevision orderRevision) {
    throw new UnsupportedOperationException();
  }

}
