package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

public class RejectOrderCommand extends OrderCommand {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("RejectOrderCommand");
  }

  private RejectOrderCommand() {
  }

  public RejectOrderCommand(long orderId) {
    super(orderId);
  }
}
