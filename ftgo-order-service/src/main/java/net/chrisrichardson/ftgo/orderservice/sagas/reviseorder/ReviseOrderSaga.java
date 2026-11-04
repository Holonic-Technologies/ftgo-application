package net.chrisrichardson.ftgo.orderservice.sagas.reviseorder;

import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.BeginReviseOrderReply;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;

import javax.annotation.PostConstruct;

public class ReviseOrderSaga implements SimpleSaga<ReviseOrderSagaData> {

  private final ReviseOrderSagaActions actions = new ReviseOrderSagaActions();

  private SagaDefinition<ReviseOrderSagaData> sagaDefinition;

  @PostConstruct
  public void initializeSagaDefinition() {
    sagaDefinition = step()
            .invokeParticipant(actions::initiateOrderRevision)
            .onReply(BeginReviseOrderReply.class, actions::handleBeginReviseOrderReply)
            .withCompensation(actions::cancelOrderRevisionInitiation)
            .step()
            .invokeParticipant(actions::initiateTicketRevision)
            .withCompensation(actions::cancelTicketRevisionInitiation)
            .step()
            .invokeParticipant(actions::reviseAccountAuthorization)
            .step()
            .invokeParticipant(actions::confirmKitchenTicketRevision)
            .step()
            .invokeParticipant(actions::finalizeOrderRevision)
            .build();
  }

  @Override
  public SagaDefinition<ReviseOrderSagaData> getSagaDefinition() {
    return sagaDefinition;
  }

}
