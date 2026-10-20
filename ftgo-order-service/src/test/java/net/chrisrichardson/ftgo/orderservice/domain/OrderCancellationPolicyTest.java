package net.chrisrichardson.ftgo.orderservice.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import net.chrisrichardson.ftgo.common.RevisedOrderLineItem;
import net.chrisrichardson.ftgo.common.UnsupportedStateTransitionException;
import net.chrisrichardson.ftgo.orderservice.OrderDetailsMother;
import net.chrisrichardson.ftgo.orderservice.RestaurantMother;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderDomainEvent;
import net.chrisrichardson.ftgo.orderservice.api.events.OrderState;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.chrisrichardson.ftgo.orderservice.OrderDetailsMother.CONSUMER_ID;
import static net.chrisrichardson.ftgo.orderservice.OrderDetailsMother.chickenVindalooLineItems;
import static net.chrisrichardson.ftgo.orderservice.RestaurantMother.AJANTA_RESTAURANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OrderCancellationPolicyTest {

  private static final Instant REQUESTED_AT = Instant.parse("2026-10-20T18:30:00Z");
  private static final String REQUESTED_BY = "consumer:" + CONSUMER_ID;

  private Order order;

  @Before
  public void setUp() {
    ResultWithDomainEvents<Order, OrderDomainEvent> result = Order.createOrder(
            CONSUMER_ID,
            AJANTA_RESTAURANT,
            OrderDetailsMother.DELIVERY_INFORMATION,
            chickenVindalooLineItems()
    );
    order = result.result;
  }

  @Test
  public void requestsCancellationWhileApprovalIsPending() {
    List<OrderDomainEvent> events = order.cancel(request(
            CancellationReason.CUSTOMER_CHANGED_MIND
    ));

    assertEquals(OrderState.CANCEL_PENDING, order.getState());
    assertEquals(OrderState.APPROVAL_PENDING, order.getStateBeforeCancellation());
    assertEquals(CancellationReason.CUSTOMER_CHANGED_MIND, order.getCancellationReason());
    assertEquals(REQUESTED_BY, order.getCancellationRequestedBy());
    assertEquals(REQUESTED_AT, order.getCancellationRequestedAt());
    assertCancellationEvent(
            events,
            OrderState.APPROVAL_PENDING,
            CancellationReason.CUSTOMER_CHANGED_MIND
    );
  }

  @Test
  public void requestsCancellationAfterApproval() {
    order.noteApproved();

    List<OrderDomainEvent> events = order.cancel(request(
            CancellationReason.DELIVERY_DELAY
    ));

    assertEquals(OrderState.CANCEL_PENDING, order.getState());
    assertEquals(OrderState.APPROVED, order.getStateBeforeCancellation());
    assertCancellationEvent(
            events,
            OrderState.APPROVED,
            CancellationReason.DELIVERY_DELAY
    );
  }

  @Test
  public void requestsCancellationWhileRevisionIsPending() {
    moveToRevisionPending();

    List<OrderDomainEvent> events = order.cancel(request(
            CancellationReason.DUPLICATE_ORDER
    ));

    assertEquals(OrderState.CANCEL_PENDING, order.getState());
    assertEquals(OrderState.REVISION_PENDING, order.getStateBeforeCancellation());
    assertCancellationEvent(
            events,
            OrderState.REVISION_PENDING,
            CancellationReason.DUPLICATE_ORDER
    );
  }

  @Test
  public void undoRestoresApprovalPendingState() {
    order.cancel(request(CancellationReason.CUSTOMER_CHANGED_MIND));

    order.undoPendingCancel();

    assertEquals(OrderState.APPROVAL_PENDING, order.getState());
    assertCancellationMetadataCleared();
  }

  @Test
  public void undoRestoresApprovedState() {
    order.noteApproved();
    order.cancel(request(CancellationReason.DELIVERY_DELAY));

    order.undoPendingCancel();

    assertEquals(OrderState.APPROVED, order.getState());
    assertCancellationMetadataCleared();
  }

  @Test
  public void undoRestoresRevisionPendingState() {
    moveToRevisionPending();
    order.cancel(request(CancellationReason.DUPLICATE_ORDER));

    order.undoPendingCancel();

    assertEquals(OrderState.REVISION_PENDING, order.getState());
    assertCancellationMetadataCleared();
  }

  @Test
  public void confirmsCancellationAndRetainsAuditMetadata() {
    order.noteApproved();
    order.cancel(request(CancellationReason.RESTAURANT_UNAVAILABLE));

    order.noteCancelled();

    assertEquals(OrderState.CANCELLED, order.getState());
    assertEquals(OrderState.APPROVED, order.getStateBeforeCancellation());
    assertEquals(CancellationReason.RESTAURANT_UNAVAILABLE, order.getCancellationReason());
    assertEquals(REQUESTED_BY, order.getCancellationRequestedBy());
    assertEquals(REQUESTED_AT, order.getCancellationRequestedAt());
  }

  @Test(expected = UnsupportedStateTransitionException.class)
  public void rejectsSecondCancellationWhileOneIsPending() {
    order.cancel(request(CancellationReason.CUSTOMER_CHANGED_MIND));

    order.cancel(request(CancellationReason.DUPLICATE_ORDER));
  }

  @Test(expected = UnsupportedStateTransitionException.class)
  public void rejectsCancellationAfterOrderWasCancelled() {
    order.noteApproved();
    order.cancel(request(CancellationReason.CUSTOMER_CHANGED_MIND));
    order.noteCancelled();

    order.cancel(request(CancellationReason.DUPLICATE_ORDER));
  }

  private CancellationRequest request(CancellationReason reason) {
    return new CancellationRequest(reason, REQUESTED_BY, REQUESTED_AT);
  }

  private void moveToRevisionPending() {
    order.noteApproved();
    OrderRevision revision = new OrderRevision(
            Optional.empty(),
            Collections.singletonList(new RevisedOrderLineItem(3, "1"))
    );
    order.revise(revision);
  }

  private void assertCancellationEvent(
          List<OrderDomainEvent> events,
          OrderState previousState,
          CancellationReason reason
  ) {
    assertEquals(1, events.size());
    OrderCancelRequested event = (OrderCancelRequested) events.get(0);
    assertEquals(previousState, event.getPreviousState());
    assertEquals(reason, event.getReason());
    assertEquals(REQUESTED_BY, event.getRequestedBy());
    assertEquals(REQUESTED_AT, event.getRequestedAt());
  }

  private void assertCancellationMetadataCleared() {
    assertNull(order.getStateBeforeCancellation());
    assertNull(order.getCancellationReason());
    assertNull(order.getCancellationRequestedBy());
    assertNull(order.getCancellationRequestedAt());
  }
}
