
-- V2__add_indexes.sql

-- 1. Composite Index (Crucial for your API)
-- optimized for: WHERE patient_id = 'X' ORDER BY measured_at DESC
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_patient_measured_at
ON medical_measurement (patient_id, measured_at DESC);

-- 2. (Optional) BRIN Index for global time analytics
-- Only add this if you expect MILLIONS of rows and need to query "All data from yesterday"
-- without a specific patient_id.
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_measured_at_brin
ON medical_measurement USING BRIN (measured_at);