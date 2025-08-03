package com.backend.smartmart.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    @Lazy
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    @Lazy
    private UserDetailsService jwtService; // JwtService is injected here

    // AuthenticationManager bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // SecurityFilterChain bean to configure HTTP security settings
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .cors(cors -> {}) // Configure CORS
            .csrf(csrf -> csrf.disable()) // Disable CSRF
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/authenticate" , "/registerNewUser","/getAllProducts","/getProductDetailsById/{productId}").permitAll() // Allow access to /authenticate endpoint
                .requestMatchers(org.springframework.http.HttpHeaders.ALLOW).permitAll() // Allow ALLOW header
                .anyRequest().authenticated() // Authenticate all other requests
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint)) // Custom entry point for JWT
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Stateless session management

        // Add JwtRequestFilter before UsernamePasswordAuthenticationFilter
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    // PasswordEncoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Global authentication manager configuration
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(jwtService).passwordEncoder(passwordEncoder());
    }
}
