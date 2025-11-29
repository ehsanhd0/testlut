package com.lutful.medical.measurement.service

import com.lutful.medical.measurement.model.dto.CreateMeasurementRequest
import com.lutful.medical.measurement.model.dto.MeasurementResponse
import com.lutful.medical.measurement.model.entity.MedicalMeasurement
import com.lutful.medical.measurement.repository.MedicalMeasurementRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class MeasurementServiceImpl(
    private val repository: MedicalMeasurementRepository
) : MeasurementService {

    private val logger = LoggerFactory.getLogger(MeasurementServiceImpl::class.java)

    override fun createMeasurement(request: CreateMeasurementRequest): MeasurementResponse {
        logger.info("Saving measurement for patient {}", request.patientId)

        val entity = MedicalMeasurement(
            patientId = request.patientId,
            systolic = request.systolic,
            diastolic = request.diastolic,
            heartRate = request.heartRate,
            measuredAt = request.measuredAt,
            receivedAt = Instant.now()
        )

        val saved = repository.save(entity)
        return saved.toResponse()
    }

    override fun searchMeasurements(
        patientId: String?,
        from: Instant?,
        to: Instant?
    ): List<MeasurementResponse> {

        // --- FIX: Logic to handle default 24-hour window ---
        val effectiveTo = to ?: Instant.now()
        val effectiveFrom = from ?: effectiveTo.minus(24, ChronoUnit.HOURS)

        logger.info(
            "Searching measurements. Patient: {}, Time: {} to {}",
            patientId ?: "ALL", effectiveFrom, effectiveTo
        )

        // --- FIX: Validation (Negative Case) ---
        if (effectiveFrom.isAfter(effectiveTo)) {
            throw IllegalArgumentException("Invalid time range: 'from' cannot be after 'to'")
        }

        val results = if (patientId != null) {
            // Filter by Patient + Time Range
            repository.findByPatientIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                patientId, effectiveFrom, effectiveTo
            )
        } else {
            // Filter by Global Time Range (Default or Specified)
            repository.findByMeasuredAtBetweenOrderByMeasuredAtDesc(
                effectiveFrom, effectiveTo
            )
        }

        return results.map { it.toResponse() }
    }

    private fun MedicalMeasurement.toResponse() = MeasurementResponse(
        id = id,
        patientId = patientId,
        systolic = systolic,
        diastolic = diastolic,
        heartRate = heartRate,
        measuredAt = measuredAt,
        receivedAt = receivedAt
    )
}