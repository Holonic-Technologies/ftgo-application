package net.chrisrichardson.ftgo.orderservice.sagas;

/**
 * In-memory locks guarding concurrent saga steps that touch the same
 * order or ticket row, used when two sagas for the same order could
 * otherwise race on the same underlying state.
 */
public class SagaRowLocks {

  public static final Object ORDER_ROW_LOCK = new Object();
  public static final Object TICKET_ROW_LOCK = new Object();

  private SagaRowLocks() {
  }
}
