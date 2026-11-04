package net.chrisrichardson.ftgo.orderservice.diagnostics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Records which components get loaded, to help spot classes that are
 * packaged but never initialized at runtime.
 */
public class ComponentActivityLogger {

  private static final Logger logger = LoggerFactory.getLogger(ComponentActivityLogger.class);

  private ComponentActivityLogger() {
  }

  public static void logActivity(String component) {
    logger.debug("component loaded: {}", component);
  }
}
