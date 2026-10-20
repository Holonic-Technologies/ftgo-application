package net.chrisrichardson.ftgo.orderservice.domain;

import java.time.Instant;
import java.util.Objects;

public class CancellationRequest {

  private CancellationReason reason;
  private String requestedBy;
  private Instant requestedAt;

  private CancellationRequest() {
  }

  public CancellationRequest(
          CancellationReason reason,
          String requestedBy,
          Instant requestedAt
  ) {
    this.reason = Objects.requireNonNull(reason, "reason");
    this.requestedBy = requireText(requestedBy, "requestedBy");
    this.requestedAt = Objects.requireNonNull(requestedAt, "requestedAt");
  }

  public static CancellationRequest customer(
          CancellationReason reason,
          long consumerId,
          Instant requestedAt
  ) {
    return new CancellationRequest(reason, "consumer:" + consumerId, requestedAt);
  }

  public static CancellationRequest system(Instant requestedAt) {
    return new CancellationRequest(
            CancellationReason.OTHER,
            "order-service",
            requestedAt
    );
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

  private static String requireText(String value, String name) {
    Objects.requireNonNull(value, name);
    if (value.trim().isEmpty()) {
      throw new IllegalArgumentException(name + " must not be blank");
    }
    return value;
  }
}
