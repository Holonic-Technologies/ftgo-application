package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

public class UndoBeginCancelCommand extends OrderCommand {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("UndoBeginCancelCommand");
  }
  public UndoBeginCancelCommand(long orderId) {
    super(orderId);
  }
}
