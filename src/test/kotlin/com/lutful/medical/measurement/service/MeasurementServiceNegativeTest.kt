package com.lutful.medical.measurement.service

import com.lutful.medical.measurement.repository.MedicalMeasurementRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.time.Instant
import java.time.temporal.ChronoUnit

class MeasurementServiceNegativeTest {

    // We mock the repository because we are testing the Service's validation logic,
    // not the database interaction.
    private val repository = mock(MedicalMeasurementRepository::class.java)
    private val service = MeasurementServiceImpl(repository)

    @Test
    fun `searchMeasurements should throw exception when 'from' date is after 'to' date`() {
        // Arrange
        val now = Instant.now()
        val fromDate = now
        val toDate = now.minus(1, ChronoUnit.HOURS) // Invalid: 'to' is in the past relative to 'from'

        // Act & Assert
        // We expect the service to throw an IllegalArgumentException
        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.searchMeasurements(
                patientId = "patient-123",
                from = fromDate,
                to = toDate
            )
        }

        // Verify the exception message is what we expect
        assertEquals("Invalid time range: 'from' cannot be after 'to'", exception.message)
    }
}