package net.chrisrichardson.ftgo.orderservice.domain;

public class OptimisticOfflineLockException extends RuntimeException {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("OptimisticOfflineLockException");
  }

}
