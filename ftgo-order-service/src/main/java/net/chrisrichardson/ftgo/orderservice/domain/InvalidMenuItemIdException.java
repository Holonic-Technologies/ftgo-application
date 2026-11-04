package net.chrisrichardson.ftgo.orderservice.domain;

public class InvalidMenuItemIdException extends RuntimeException {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("InvalidMenuItemIdException");
  }
  public InvalidMenuItemIdException(String menuItemId) {
    super("Invalid menu item id " + menuItemId);
  }
}
