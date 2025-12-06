

-- Thay đổi các giá trị trong ngoặc đơn tùy theo logic của bạn (ví dụ: PENDING, COMPLETED, FAILED...)
CREATE TYPE payment_status AS ENUM ('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED');

-- Bảng Payments (tổng hợp chính cho quản lý thanh toán)
CREATE TABLE "payment" (
    id UUID NOT NULL,
    order_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    amount NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
    payment_status payment_status NOT NULL,
    transaction_id VARCHAR(255) UNIQUE,
    failure_reason VARCHAR,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,
    transaction_start_at TIMESTAMP WITH TIME ZONE,
    transaction_end_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT payments_pkey PRIMARY KEY (id)
);