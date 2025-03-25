package org.example.concurrentmodificationexception;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ConcurrentModificationExceptionApplication {
  public static void main(String[] args) {
    new SpringApplicationBuilder(ConcurrentModificationExceptionApplication.class)
        .web(WebApplicationType.SERVLET)
        .run(args);
  }
}
