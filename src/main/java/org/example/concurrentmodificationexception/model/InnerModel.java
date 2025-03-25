package org.example.concurrentmodificationexception.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = InnerModel.class)
public class InnerModel {
}
