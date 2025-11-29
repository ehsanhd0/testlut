package com.lutful.medical.measurement.service

import com.lutful.medical.messaging.MeasurementProducer
import com.lutful.medical.measurement.model.dto.CreateMeasurementRequest
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.random.Random

@Component
@Profile("!test")
class MeasurementSimulator(
    private val producer: MeasurementProducer
) {

    private val logger = LoggerFactory.getLogger(MeasurementSimulator::class.java)
    private val patientIds = listOf("patient-1", "patient-2", "patient-3", "patient-4")

    @Scheduled(fixedRateString = "\${app.simulation.rate:5000}")
    fun simulateIncomingSensorData() {
        val patientId = patientIds.random()

        val request = CreateMeasurementRequest(
            patientId = patientId,
            systolic = Random.nextInt(110, 160),
            diastolic = Random.nextInt(70, 100),
            heartRate = Random.nextInt(60, 120),
            measuredAt = Instant.now()
        )

        try {
            producer.sendToStream(request)
        } catch (e: Exception) {
            logger.error("SIMULATOR: Failed to push data", e)
        }
    }
}