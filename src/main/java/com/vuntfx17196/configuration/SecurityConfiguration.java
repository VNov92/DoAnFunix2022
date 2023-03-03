package com.vuntfx17196.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  private final UserDetailsService userDetailsService;

  public SecurityConfiguration(UserDetailsService userDetailsService,
      AuthenticationSuccessHandler authenticationSuccessHandler) {
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  private final String[] publicPages = {"/", "/index", "/category/**", "/product/**", "/search/**",
      "/register",
      "/forgotpassword", "/account/**", "/error", "/api/**"};
  private final String[] publicResources = {"/resources/**", "/static/**", "/images/**",
      "/productImages/**",
      "/css/**", "/js/**"};

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, RememberMeServices rememberMeServices)
      throws Exception {
    http.csrf().disable().authorizeHttpRequests(authorize -> authorize
        .requestMatchers("/admin/**", "/actuator/**").hasRole("ADMIN")
        .requestMatchers("/user/**").hasRole("USER")
        .requestMatchers(publicPages).permitAll()
        .requestMatchers(publicResources).permitAll());
    http.formLogin(form -> form
            .loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/").permitAll())
        .logout(
            logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll())
        .rememberMe((remember) -> remember.rememberMeServices(rememberMeServices));
    return http.build();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

  @Bean
  RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
    TokenBasedRememberMeServices.RememberMeTokenAlgorithm encodingAlgorithm = TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256;
    TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices("myKey",
        userDetailsService, encodingAlgorithm);
    rememberMe.setMatchingAlgorithm(TokenBasedRememberMeServices.RememberMeTokenAlgorithm.MD5);
    return rememberMe;
  }
}
