package com.lutful.medical.measurement.service

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class RateLimitFilterTest(
    @Autowired val mockMvc: MockMvc
) {

    @Test
    fun `11th request should be rate limited`() {
        // First 10 requests should pass
        repeat(10) { i ->
            mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk)
        }

        // 11th request should be 429
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isTooManyRequests)
    }
}