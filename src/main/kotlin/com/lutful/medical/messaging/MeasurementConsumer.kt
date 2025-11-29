package com.lutful.medical.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.lutful.medical.measurement.model.dto.CreateMeasurementRequest
import com.lutful.medical.measurement.service.MeasurementService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class MeasurementConsumer(
    private val measurementService: MeasurementService,
    private val objectMapper: ObjectMapper // 1. Inject ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(MeasurementConsumer::class.java)

    /**
     * Fix for ClassCastException:
     * We consume String (JSON) instead of the DTO directly.
     * This bypasses Spring's type inference issues with Kotlin.
     */
    @Bean
    fun processTelemetry(): Consumer<String> {
        return Consumer { messageJson ->
            try {
                // 2. Manual Deserialization (Safe & Controlled)
                val request = objectMapper.readValue(messageJson, CreateMeasurementRequest::class.java)

                logger.info("KAFKA_READ: Saving data for patient ${request.patientId}")
                measurementService.createMeasurement(request)

            } catch (e: Exception) {
                // 3. Poison Pill Handling
                // If JSON is bad, we catch it here so the Consumer doesn't crash loop.
                logger.error("KAFKA_ERROR: Failed to deserialize message: $messageJson", e)
            }
        }
    }
}