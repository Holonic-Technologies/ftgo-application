package net.chrisrichardson.ftgo.orderservice.domain;

public class OrderMinimumNotMetException extends RuntimeException {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("OrderMinimumNotMetException");
  }
}
