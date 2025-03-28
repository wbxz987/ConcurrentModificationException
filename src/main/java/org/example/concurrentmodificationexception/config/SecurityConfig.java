package org.example.concurrentmodificationexception.config;

import java.util.ArrayList;

import org.springframework.aop.Advisor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.authorization.method.AuthorizationAdvisor;
import org.springframework.security.authorization.method.AuthorizationAdvisorProxyFactory;
import org.springframework.security.authorization.method.AuthorizeReturnObjectMethodInterceptor;
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

  @Bean
  @Role(2)
  static Advisor authorizeReturnObjectAdvisor(ObjectProvider<AuthorizationAdvisor> provider, ThreadsafeAuthorizationProxyFactory proxyFactory) {
    AuthorizationAdvisorProxyFactory delegate = new AuthorizationAdvisorProxyFactory(new ArrayList<>());
    provider.forEach(delegate::addAdvisor);
    AuthorizeReturnObjectMethodInterceptor interceptor = new AuthorizeReturnObjectMethodInterceptor(proxyFactory);
    delegate.addAdvisor(interceptor);
    proxyFactory.setDelegate(delegate);
    return interceptor;
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
