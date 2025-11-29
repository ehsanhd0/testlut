package com.lutful.medical.messaging


import com.lutful.medical.measurement.model.dto.CreateMeasurementRequest
import com.lutful.medical.measurement.service.MeasurementService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class KafkaConsumer(private val measurementService: MeasurementService) {

    private val logger = LoggerFactory.getLogger(KafkaConsumer::class.java)

    @Bean
    fun processTelemetry(): Consumer<CreateMeasurementRequest> {
        return Consumer { request ->
            logger.info("KAFKA_READ: Saving data for patient ${request.patientId}")
            measurementService.createMeasurement(request)
        }
    }
}