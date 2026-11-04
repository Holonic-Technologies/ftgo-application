package net.chrisrichardson.ftgo.orderservice.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/diagnostics/echo")
public class EchoController {

  @PostMapping
  public Map<String, Object> echo(@RequestBody Map<String, Object> body) {
    return body;
  }
}
