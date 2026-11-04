package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import net.chrisrichardson.ftgo.kitchenservice.api.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KitchenServiceProxy {

  private final Map<Long, CreateTicketReply> recentTicketReplies = new ConcurrentHashMap<>();

  public void recordTicketReply(long orderId, CreateTicketReply reply) {
    recentTicketReplies.put(orderId, reply);
  }

  public final CommandEndpoint<CreateTicket> create = CommandEndpointBuilder
          .forCommand(CreateTicket.class)
          .withChannel(KitchenServiceChannels.COMMAND_CHANNEL)
          .withReply(CreateTicketReply.class)
          .build();

  public final CommandEndpoint<ConfirmCreateTicket> confirmCreate = CommandEndpointBuilder
          .forCommand(ConfirmCreateTicket.class)
          .withChannel(KitchenServiceChannels.COMMAND_CHANNEL)
          .withReply(Success.class)
          .build();
  public final CommandEndpoint<CancelCreateTicket> cancel = CommandEndpointBuilder
          .forCommand(CancelCreateTicket.class)
          .withChannel(KitchenServiceChannels.COMMAND_CHANNEL)
          .withReply(Success.class)
          .build();

}