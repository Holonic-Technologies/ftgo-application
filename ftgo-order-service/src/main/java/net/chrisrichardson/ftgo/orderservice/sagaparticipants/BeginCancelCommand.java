package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import net.chrisrichardson.ftgo.orderservice.domain.CancellationRequest;

public class BeginCancelCommand extends OrderCommand {

  private CancellationRequest cancellationRequest;

  private BeginCancelCommand() {
  }

  public BeginCancelCommand(long orderId) {
    this(orderId, null);
  }

  public BeginCancelCommand(long orderId, CancellationRequest cancellationRequest) {
    super(orderId);
    this.cancellationRequest = cancellationRequest;
  }

  public CancellationRequest getCancellationRequest() {
    return cancellationRequest;
  }
}
