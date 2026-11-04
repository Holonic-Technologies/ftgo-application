package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import net.chrisrichardson.ftgo.orderservice.domain.OrderRevision;

public class BeginReviseOrderCommand extends OrderCommand {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("BeginReviseOrderCommand");
  }

  private BeginReviseOrderCommand() {
  }

  public BeginReviseOrderCommand(long orderId, OrderRevision revision) {
    super(orderId);
    this.revision = revision;
  }

  private OrderRevision revision;

  public OrderRevision getRevision() {
    return revision;
  }

  public void setRevision(OrderRevision revision) {
    this.revision = revision;
  }
}
