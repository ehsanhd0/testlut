package com.lutful.medical.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .anyRequest().authenticated()
            }
            // DELETE or COMMENT OUT this line:
            // .httpBasic(Customizer.withDefaults())

            // ADD this line instead:
            .oauth2ResourceServer { it.jwt(Customizer.withDefaults()) }

        return http.build()
    }

    // --- FIX: Add a default user for testing ---
    @Bean
    fun userDetailsService(): UserDetailsService {
        val user = User.builder()
            .username("admin")
            .password("{noop}admin123") // {noop} means no encoding for this demo
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user)
    }
}