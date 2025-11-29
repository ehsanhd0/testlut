package com.lutful.medical.config

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RateLimitFilter : Filter {

    // Define Limit: Capacity of 10 tokens, refilling 10 tokens every 1 minute
    private val limit = Bandwidth.builder()
        .capacity(10)
        .refillGreedy(10, Duration.ofMinutes(1))
        .build()

    // Create the bucket (In-Memory)
    private val bucket = Bucket.builder()
        .addLimit(limit)
        .build()

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpResponse = response as HttpServletResponse

        // Attempt to consume 1 token
        if (bucket.tryConsume(1)) {
            // Success: Proceed with the request
            chain.doFilter(request, response)
        } else {
            // Failure: Rate limit exceeded
            httpResponse.status = 429 // Too Many Requests
            httpResponse.contentType = "application/json"
            httpResponse.writer.write("""{"error": "Rate limit exceeded. Try again later."}""")
        }
    }
}