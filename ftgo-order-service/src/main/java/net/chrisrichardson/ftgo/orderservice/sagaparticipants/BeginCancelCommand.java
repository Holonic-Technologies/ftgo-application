package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

public class BeginCancelCommand extends OrderCommand {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("BeginCancelCommand");
  }

  private BeginCancelCommand() {
  }

  public BeginCancelCommand(long orderId) {
    super(orderId);
  }
}
