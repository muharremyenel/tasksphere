package com.tasksphere.taskmanager.infrastructure.config;

import com.tasksphere.taskmanager.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

//import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/authenticate").permitAll()
                .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/tasks").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/tasks/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/tasks/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/categories/**").hasRole("ADMIN")
                // User endpoints
                .requestMatchers("/api/v1/users/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/tasks/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/v1/tasks/*/status").authenticated()
                .requestMatchers("/api/v1/comments/**").authenticated()
                .requestMatchers("/api/v1/tags/**").authenticated()
                .requestMatchers("/api/v1/teams/my-team").authenticated()
                .requestMatchers("/api/v1/teams/{teamId}/members").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/teams/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/teams/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/teams/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/tasks/{taskId}/comments/**").authenticated()
                .anyRequest().authenticated()
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 