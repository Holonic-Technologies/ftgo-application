package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

public class UndoBeginReviseOrderCommand extends OrderCommand {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("UndoBeginReviseOrderCommand");
  }

  protected UndoBeginReviseOrderCommand() {
  }

  public UndoBeginReviseOrderCommand(long orderId) {
    super(orderId);
  }
}
