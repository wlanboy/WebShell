package com.wlanboy.webshell;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class BasicWebSecurityConfigurerAdapter {

	@Value("${userpassword}")
	String userpassword;
	@Value("${testpassword}")
	String testpassword;
    String role = "SHELL";
	
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
      http
        .authorizeHttpRequests(requests -> requests
        .requestMatchers( new AntPathRequestMatcher("/webjars/**")).permitAll()
        .requestMatchers( new AntPathRequestMatcher("swagger-ui/**")).permitAll()
        .requestMatchers( new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
        .requestMatchers( new AntPathRequestMatcher("v3/api-docs/**")).permitAll()
        .requestMatchers( new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
        .anyRequest().authenticated())
        .formLogin(form -> form.loginPage("/login").permitAll());
      return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withUsername("user")
            .password(userpassword)
            .roles(role)
            .build();
        UserDetails test = User.withUsername("test")
            .password(testpassword)
            .roles(role)
            .build();
        return new InMemoryUserDetailsManager(user,test);
    }
}
