package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

public class ConfirmCancelOrderCommand extends OrderCommand {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("ConfirmCancelOrderCommand");
  }

  private ConfirmCancelOrderCommand() {
  }

  public ConfirmCancelOrderCommand(long orderId) {
    super(orderId);
  }
}
