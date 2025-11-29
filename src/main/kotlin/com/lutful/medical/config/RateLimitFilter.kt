package com.lutful.medical.measurement.config

// 1. Bucket4j Imports (Modern Builder Pattern)
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import java.time.Duration

// 2. Servlet Imports (MUST be jakarta.servlet for Spring Boot 3)
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class RateLimitFilter : Filter {

    // 1. Define the limit using the new Builder Pattern
    // "Capacity of 10, refilling 10 tokens every 1 minute"
    private val limit = Bandwidth.builder()
        .capacity(10)
        .refillGreedy(10, Duration.ofMinutes(1))
        .build()

    // 2. Create the bucket
    private val bucket = Bucket.builder()
        .addLimit(limit)
        .build()

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        // 3. Try to consume 1 token
        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response)
        } else {
            val httpResponse = response as HttpServletResponse
            httpResponse.status = 429 // Too Many Requests
            httpResponse.contentType = "text/plain"
            httpResponse.writer.write("Rate limit exceeded. Maximum 10 requests per minute.")
        }
    }
}