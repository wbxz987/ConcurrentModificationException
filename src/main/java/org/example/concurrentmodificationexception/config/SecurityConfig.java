package org.example.concurrentmodificationexception.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.security.authorization.method.AuthorizationAdvisor;
import org.springframework.security.authorization.method.AuthorizationAdvisorProxyFactory;
import org.springframework.security.authorization.method.AuthorizationManagerAfterMethodInterceptor;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.authorization.method.AuthorizeReturnObjectMethodInterceptor;
import org.springframework.security.authorization.method.PostFilterAuthorizationMethodInterceptor;
import org.springframework.security.authorization.method.PreFilterAuthorizationMethodInterceptor;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  @Primary
  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  static AuthorizationAdvisorProxyFactory proxyFactory() {
    AuthorizationAdvisorProxyFactory proxyFactory =
        new AuthorizationAdvisorProxyFactory(new ArrayList<>());
    List<AuthorizationAdvisor> advisors = new ArrayList<>();

    advisors.add(AuthorizationManagerBeforeMethodInterceptor.preAuthorize());
    advisors.add(AuthorizationManagerAfterMethodInterceptor.postAuthorize());
    advisors.add(new PreFilterAuthorizationMethodInterceptor());
    advisors.add(new PostFilterAuthorizationMethodInterceptor());
    advisors.add(new AuthorizeReturnObjectMethodInterceptor(proxyFactory));

    AnnotationAwareOrderComparator.sort(advisors);

    for (AuthorizationAdvisor advisor : advisors) {
      proxyFactory.addAdvisor(advisor);
    }

    return proxyFactory;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.httpBasic(Customizer.withDefaults())
        .authorizeHttpRequests(requests -> requests.anyRequest().authenticated())
        .csrf(AbstractHttpConfigurer::disable)
        .build();
  }

  @Bean
  UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails user =
        User.builder()
            .username("user")
            .password(passwordEncoder.encode("password"))
            .authorities("READ")
            .build();

    return new InMemoryUserDetailsManager(user);
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
