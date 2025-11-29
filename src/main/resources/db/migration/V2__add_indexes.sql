-- src/main/resources/db/migration/V2__add_indexes.sql

CREATE INDEX IF NOT EXISTS idx_medical_measurement_patient_measured_at
    ON medical_measurement (patient_id, measured_at DESC);

CREATE INDEX IF NOT EXISTS idx_medical_measurement_patient_received_at
    ON medical_measurement (patient_id, received_at DESC);
