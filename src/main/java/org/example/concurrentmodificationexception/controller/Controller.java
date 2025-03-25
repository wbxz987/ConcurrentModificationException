package org.example.concurrentmodificationexception.controller;

import org.example.concurrentmodificationexception.model.InnerModel;
import org.example.concurrentmodificationexception.model.Model;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
  @AuthorizeReturnObject
  @GetMapping("/model")
  public Model getModel() {
    return new Model(new InnerModel());
  }
}
