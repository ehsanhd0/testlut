package com.lutful.medical.messaging

import com.lutful.medical.measurement.model.dto.CreateMeasurementRequest
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Service

@Service
class KafkaProducer(private val streamBridge: StreamBridge) {

    private val logger = LoggerFactory.getLogger(KafkaProducer::class.java)

    fun sendToStream(request: CreateMeasurementRequest) {
        logger.info("KAFKA_SEND: Pushing data for patient ${request.patientId}")
        // 'telemetry-out-0' matches application.yml binding
        streamBridge.send("telemetry-out-0", request)
    }
}