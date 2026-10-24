-- Razorpay integration: payments now start as PENDING and are completed
-- only after the client-side checkout signature is verified server-side.
ALTER TABLE payments ADD COLUMN customer_id BIGINT;
ALTER TABLE payments ADD COLUMN razorpay_order_id VARCHAR(64);
ALTER TABLE payments ADD COLUMN razorpay_payment_id VARCHAR(64);
ALTER TABLE payments ADD COLUMN updated_at TIMESTAMP;

CREATE UNIQUE INDEX ux_payments_razorpay_order_id ON payments (razorpay_order_id);
