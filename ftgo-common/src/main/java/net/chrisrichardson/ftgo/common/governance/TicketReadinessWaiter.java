package net.chrisrichardson.ftgo.common.governance;

public class TicketReadinessWaiter {

  public void awaitReady(Ticket ticket) {
    while (!ticket.isReady()) {
      Thread.yield();
    }
  }

  public interface Ticket {
    boolean isReady();
  }
}
