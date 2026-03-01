package com.creditapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security konfigürasyonu
 *
 * Bu configuration class uygulama güvenliğini sağlar:
 * - Camunda web app'leri için authentication
 * - REST API endpoints için authorization
 * - User roles ve permissions
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
                // In-memory user store (Production'da database kullanılmalı)
                UserDetails admin = User.builder()
                                .username("admin")
                                .password(passwordEncoder.encode("admin"))
                                .roles("ADMIN", "USER")
                                .build();

                UserDetails manager = User.builder()
                                .username("manager")
                                .password(passwordEncoder.encode("manager"))
                                .roles("MANAGER", "USER")
                                .build();

                UserDetails creditAnalyst = User.builder()
                                .username("analyst")
                                .password(passwordEncoder.encode("analyst"))
                                .roles("CREDIT_ANALYST", "USER")
                                .build();

                UserDetails riskAnalyst = User.builder()
                                .username("risk")
                                .password(passwordEncoder.encode("risk"))
                                .roles("RISK_ANALYST", "USER")
                                .build();

                return new InMemoryUserDetailsManager(admin, manager, creditAnalyst, riskAnalyst);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/api/**", "/engine-rest/**", "/camunda/**", "/h2-console/**"))
                                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoints
                                                .requestMatchers("/h2-console/**").permitAll()
                                                .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                                                .requestMatchers("/login", "/logout").permitAll()
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/error").permitAll()

                                                // Camunda endpoints - Webapp and REST
                                                .requestMatchers("/camunda/**").permitAll()
                                                .requestMatchers("/engine-rest/**").permitAll()
                                                .requestMatchers("/camunda-welcome").permitAll()

                                                // Application endpoints
                                                .requestMatchers("/applications/**").hasAnyRole("USER", "ADMIN")
                                                .requestMatchers("/tasks/**").hasAnyRole("USER", "ADMIN")
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")

                                                // Admin endpoints
                                                .requestMatchers("/admin/**").hasRole("ADMIN")

                                                .anyRequest().authenticated())
                                .httpBasic(basic -> {})
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/dashboard")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                // H2 Console iframe kullanır
                http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

                return http.build();
        }
}
