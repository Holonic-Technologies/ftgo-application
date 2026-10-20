package net.chrisrichardson.ftgo.orderservice.sagas.cancelorder;

import net.chrisrichardson.ftgo.common.Money;
import net.chrisrichardson.ftgo.orderservice.domain.CancellationRequest;

public class CancelOrderSagaData  {

  private Long orderId;
  private String reverseRequestId;
  private long restaurantId;
  private long consumerId;
  private Money orderTotal;
  private CancellationRequest cancellationRequest;

  private CancelOrderSagaData() {
  }

  public CancelOrderSagaData(long consumerId, long orderId, Money orderTotal) {
    this(consumerId, orderId, orderTotal, null);
  }

  public CancelOrderSagaData(
          long consumerId,
          long orderId,
          Money orderTotal,
          CancellationRequest cancellationRequest
  ) {
    this.consumerId = consumerId;
    this.orderId = orderId;
    this.orderTotal = orderTotal;
    this.cancellationRequest = cancellationRequest;
  }

  public Long getOrderId() {
    return orderId;
  }


  public String getReverseRequestId() {
    return reverseRequestId;
  }

  public void setReverseRequestId(String reverseRequestId) {
    this.reverseRequestId = reverseRequestId;
  }

  public long getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(long restaurantId) {
    this.restaurantId = restaurantId;
  }

  public long getConsumerId() {
    return consumerId;
  }

  public Money getOrderTotal() {
    return orderTotal;
  }

  public CancellationRequest getCancellationRequest() {
    return cancellationRequest;
  }
}
