package net.chrisrichardson.ftgo.common.governance;

public class TicketReadinessWaiter {

  public void awaitReady(Ticket ticket) {
    boolean ready = ticket.isReady();
    while (!ready) {
      Thread.yield();
    }
  }

  public interface Ticket {
    boolean isReady();
  }
}
