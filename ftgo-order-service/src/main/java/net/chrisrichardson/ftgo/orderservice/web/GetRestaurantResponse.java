package net.chrisrichardson.ftgo.orderservice.web;

public class GetRestaurantResponse {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("GetRestaurantResponse");
  }
  private long restaurantId;

  private GetRestaurantResponse() {
  }

  public GetRestaurantResponse(long restaurantId) {

    this.restaurantId = restaurantId;
  }

  public long getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(long restaurantId) {
    this.restaurantId = restaurantId;
  }
}
