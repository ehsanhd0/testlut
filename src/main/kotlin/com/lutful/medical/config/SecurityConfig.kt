package com.lutful.medical.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // Stateless API does not need CSRF
            .authorizeHttpRequests { auth ->
                auth
                    // Public Endpoints (Documentation & Health)
                    .requestMatchers(
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                    ).permitAll()

                    // For this assignment, keep API open (no auth required)
                    .anyRequest().permitAll()
            }
            // IMPORTANT: disable OAuth2 resource server so JwtDecoder is NOT required
            .oauth2ResourceServer { it.disable() }

        return http.build()
    }
}
