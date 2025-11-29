package com.lutful.medical.measurement.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // 1. Disable CSRF (Cross-Site Request Forgery)
            // Essential for stateless APIs. We use JWTs, not session cookies, so CSRF isn't a threat.
            .csrf { it.disable() }

            // 2. Define Authorization Rules
            // Order matters! Specific rules must come before generic ones.
            .authorizeHttpRequests { auth ->
                auth
                    // Allow public access to monitoring and documentation
                    .requestMatchers(
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                    ).permitAll()

                    // Strict rule: Only tokens with "sensor:write" scope can POST data
                    // Note: Spring Security adds "SCOPE_" prefix automatically to JWT scopes
                    .requestMatchers(HttpMethod.POST, "/api/v1/measurements")
                    .hasAuthority("SCOPE_sensor:write")

                    // All other requests (like GET /api/v1/measurements) require valid login
                    .anyRequest().authenticated()
            }

            // 3. Configure OAuth2 Resource Server
            // This tells Spring to check the "Authorization: Bearer <token>" header
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt {
                    // Uses default configuration:
                    // - Validates signature using the issuer-uri in application.yml
                    // - Checks expiration
                    // - Converts "scope" claim to authorities with "SCOPE_" prefix
                }
            }

        return http.build()
    }
}