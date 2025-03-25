package org.example.concurrentmodificationexception;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ConcurrentModificationExceptionTest {
  @Autowired private MockMvc mockMvc;

  @Test
  void test() {
    List<CompletableFuture<MockHttpServletResponse>> futures = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      CompletableFuture<MockHttpServletResponse> future =
          CompletableFuture.supplyAsync(
              () -> {
                try {
                  return mockMvc
                      .perform(
                          get("/model")
                              .accept(MediaType.APPLICATION_JSON)
                              .with(user("user").authorities(new SimpleGrantedAuthority("READ"))))
                      .andExpect(status().isOk())
                      .andReturn()
                      .getResponse();
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              });

      futures.add(future);
    }

    futures.forEach(CompletableFuture::join);
  }
}
