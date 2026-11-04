package net.chrisrichardson.ftgo.orderservice.domain;

public class RestaurantNotFoundException extends RuntimeException {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("RestaurantNotFoundException");
  }
  public RestaurantNotFoundException(long restaurantId) {
    super("Restaurant not found with id " + restaurantId);
  }
}
