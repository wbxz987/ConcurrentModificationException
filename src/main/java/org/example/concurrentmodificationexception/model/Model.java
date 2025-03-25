package org.example.concurrentmodificationexception.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.access.prepost.PreAuthorize;

@JsonSerialize(as = Model.class)
public class Model {
  private final InnerModel innerModel;

  public Model(InnerModel innerModel) {
    this.innerModel = innerModel;
  }

  @PreAuthorize("hasAuthority('READ')")
  public InnerModel getInnerModel() {
    return innerModel;
  }
}
