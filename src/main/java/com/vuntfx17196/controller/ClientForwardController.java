package com.vuntfx17196.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class ClientForwardController {

  /**
   * Forwards any unmapped paths (except those containing a period) to the client
   * {@code index.html}.
   *
   * @return forward to client {@code index.html}.
   */
  @GetMapping(value = "/**/{path:[^\\.]*}")
  public String forward() {
    return "forward:/";
  }
}
