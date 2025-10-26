ALTER TABLE payments ADD COLUMN idempotency_key VARCHAR(255) UNIQUE;
CREATE INDEX idx_idempotency_key ON payments(idempotency_key);