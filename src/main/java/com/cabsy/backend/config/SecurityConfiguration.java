// src/main/java/com/cabsy/backend/config/SecurityConfiguration.java
package com.cabsy.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource; // <-- New Import
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity // Enables Spring Security's web security features
public class SecurityConfiguration {

    @Value("${cors.allowedOrigin}")
    private String allowedOrigin;

    @Value("${cors.allowedMethods}")
    private String allowedMethods;

    @Value("${cors.allowedHeaders}")
    private String allowedHeaders;

    @Value("${cors.allowedCredentials}")
    private boolean allowedCredentials;

    @Value("${cors.corsConfiguration}")
    private String corsConfigurationPath;

    // Defines the security filter chain. This is crucial for Spring Security.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for REST APIs (unless you handle it on frontend)
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll() // Temporarily allow all requests (no authentication/authorization yet)
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Correctly use the bean

        return http.build();
    }

    // This is the missing bean that defines the CORS configuration source
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Convert comma-separated string to list for allowedMethods and allowedHeaders
        config.setAllowedOrigins(List.of(allowedOrigin));
        config.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        config.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        config.setAllowCredentials(allowedCredentials);
        config.addExposedHeader("Authorization"); // Expose Authorization header if your frontend needs to read it

        source.registerCorsConfiguration(corsConfigurationPath, config);
        return source; // Return the CorsConfigurationSource
    }
}