package com.lutful.medical.measurement.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "medical_measurement",
    indexes = [
        Index(name = "idx_patient_id", columnList = "patient_id")
    ]
)
data class MedicalMeasurement(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    val patientId: String,

    @Column(nullable = false)
    val systolic: Int,

    @Column(nullable = false)
    val diastolic: Int,

    @Column(name = "heart_rate", nullable = false)
    val heartRate: Int,

    @Column(name = "measured_at", nullable = false)
    val measuredAt: Instant,

    @Column(name = "received_at", nullable = false)
    val receivedAt: Instant = Instant.now()
)
