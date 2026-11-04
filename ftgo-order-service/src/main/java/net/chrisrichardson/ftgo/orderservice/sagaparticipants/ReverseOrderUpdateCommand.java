package net.chrisrichardson.ftgo.orderservice.sagaparticipants;


import io.eventuate.tram.commands.common.Command;

public class ReverseOrderUpdateCommand implements Command {

  static {
    net.chrisrichardson.ftgo.orderservice.diagnostics.ComponentActivityLogger.logActivity("ReverseOrderUpdateCommand");
  }
}
