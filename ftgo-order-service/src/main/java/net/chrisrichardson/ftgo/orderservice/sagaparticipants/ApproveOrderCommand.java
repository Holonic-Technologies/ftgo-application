package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

public class ApproveOrderCommand extends OrderCommand {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("ApproveOrderCommand");
  }

  private ApproveOrderCommand() {
  }

  public ApproveOrderCommand(long orderId) {
    super(orderId);
  }
}
