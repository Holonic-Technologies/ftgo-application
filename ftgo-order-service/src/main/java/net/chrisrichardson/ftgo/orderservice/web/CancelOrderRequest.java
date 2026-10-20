package net.chrisrichardson.ftgo.orderservice.web;

import net.chrisrichardson.ftgo.orderservice.domain.CancellationReason;

public class CancelOrderRequest {

  private CancellationReason reason;

  private CancelOrderRequest() {
  }

  public CancelOrderRequest(CancellationReason reason) {
    this.reason = reason;
  }

  public CancellationReason getReason() {
    return reason;
  }

  public void setReason(CancellationReason reason) {
    this.reason = reason;
  }
}
