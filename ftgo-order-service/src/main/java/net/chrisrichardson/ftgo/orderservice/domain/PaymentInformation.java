package net.chrisrichardson.ftgo.orderservice.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;

@Access(AccessType.FIELD)
public class PaymentInformation {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("PaymentInformation");
  }

  private String paymentToken;
}
