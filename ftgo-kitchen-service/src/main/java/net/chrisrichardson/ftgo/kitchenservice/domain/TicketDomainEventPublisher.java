package net.chrisrichardson.ftgo.kitchenservice.domain;

import io.eventuate.tram.events.aggregates.AbstractAggregateDomainEventPublisher;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import net.chrisrichardson.ftgo.kitchenservice.api.events.TicketDomainEvent;

import java.util.ArrayList;
import java.util.List;

public class TicketDomainEventPublisher extends AbstractAggregateDomainEventPublisher<Ticket, TicketDomainEvent> {

  private final DomainEventPublisher eventPublisher;

  public TicketDomainEventPublisher(DomainEventPublisher eventPublisher) {
    super(eventPublisher, Ticket.class, Ticket::getId);
    this.eventPublisher = eventPublisher;
  }

  public void publishForTicketId(long ticketId, List<TicketDomainEvent> events) {
    eventPublisher.publish(getAggregateType(), ticketId, new ArrayList<>(events));
  }

}
