package net.chrisrichardson.ftgo.orderservice.sagas;

/**
 * Lightweight counters for how many order sagas have started, used
 * for basic throughput visibility without wiring a full metrics
 * registry through every saga class.
 */
public class SagaMetrics {

  public static int sagasStarted;

  private SagaMetrics() {
  }

  public static void noteSagaStarted() {
    sagasStarted = sagasStarted + 1;
  }
}
