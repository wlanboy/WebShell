package com.wlanboy.webshell;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class BasicWebSecurityConfigurerAdapter {

  @Value("${userpassword}")
  String userpassword;
  @Value("${testpassword}")
  String testpassword;
  String role = "SHELL";

  @Bean
  SecurityFilterChain configure(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
.authorizeHttpRequests(requests -> requests
            .requestMatchers("/login.html", "/style.css", "/login.js", "/shell.js").permitAll()
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/login.html")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/", true)
            .failureUrl("/login.html?error")
            .permitAll());
    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  InMemoryUserDetailsManager userDetailsService() {
    UserDetails user = User.withUsername("user")
        .password(userpassword)
        .roles(role)
        .build();
    UserDetails test = User.withUsername("test")
        .password(testpassword)
        .roles(role)
        .build();
    return new InMemoryUserDetailsManager(user, test);
  }
}
