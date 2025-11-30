package com.lutful.medical.measurement.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.lutful.medical.measurement.controller.MeasurementController
import com.lutful.medical.measurement.model.dto.CreateMeasurementRequest
import com.lutful.medical.measurement.model.dto.MeasurementResponse
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@WebMvcTest(MeasurementController::class)
class MeasurementControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var measurementService: MeasurementService

    @Test
    @WithMockUser(username = "admin")
    fun `should return 400 Bad Request when systolic pressure is too high`() {
        // Arrange
        val invalidRequest = CreateMeasurementRequest(
            patientId = "patient-123",
            systolic = 300,
            diastolic = 80,
            heartRate = 70,
            measuredAt = Instant.now()
        )

        // Act & Assert
        mockMvc.perform(
            post("/api/v1/measurements")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(username = "admin")
    fun `should return 201 Created when data is valid`() {
        // Arrange
        val validRequest = CreateMeasurementRequest(
            patientId = "patient-123",
            systolic = 120,
            diastolic = 80,
            heartRate = 70,
            measuredAt = Instant.now()
        )

        val mockResponse = MeasurementResponse(
            id = 1L,
            patientId = "patient-123",
            systolic = 120,
            diastolic = 80,
            heartRate = 70,
            measuredAt = validRequest.measuredAt,
            receivedAt = Instant.now()
        )

        // FIX: Use 'whenever' (cleaner Kotlin syntax) and the correct 'any()' import.
        // This ensures a non-null object is passed to the mock to satisfy Kotlin's type safety.
        whenever(measurementService.createMeasurement(any())).thenReturn(mockResponse)

        // Act & Assert
        mockMvc.perform(
            post("/api/v1/measurements")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest))
        )
            .andExpect(status().isCreated)
    }
}