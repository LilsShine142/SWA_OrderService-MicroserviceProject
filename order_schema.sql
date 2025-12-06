-- 2. Tạo Enum Type cho trạng thái đơn hàng (BẮT BUỘC CÓ CÁI NÀY TRƯỚC)
CREATE TYPE order_status AS ENUM ('PENDING', 'PAID', 'APPROVED', 'CANCELLED', 'CANCELLING');

-- 3. Bảng Orders (Đổi tên thành số nhiều 'orders' để tránh từ khóa đặc biệt)
CREATE TABLE orders (
    id UUID NOT NULL,
    customer_id UUID NOT NULL,
    restaurant_id UUID NOT NULL,
    tracking_id UUID NOT NULL,
    price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    order_status order_status NOT NULL, -- Sử dụng Enum vừa tạo ở trên
    failure_messages VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,
    street VARCHAR(255) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    city VARCHAR(100) NOT NULL,
    CONSTRAINT orders_pkey PRIMARY KEY (id),
    CONSTRAINT tracking_id_unique UNIQUE (tracking_id)
);

-- 4. Bảng Order items
CREATE TABLE order_items (
    id BIGINT NOT NULL,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    sub_total NUMERIC(10,2) NOT NULL CHECK (sub_total >= 0),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT order_items_pkey PRIMARY KEY (id), -- Khóa chính

    -- Ràng buộc khóa ngoại trỏ về bảng orders
    CONSTRAINT fk_order_items_orders FOREIGN KEY (order_id)
        REFERENCES orders (id)
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

-- 5. Bảng Order logs
CREATE TABLE order_logs (
    id BIGSERIAL NOT NULL,
    order_id UUID NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_data JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    compensation_status VARCHAR(50),
    CONSTRAINT order_logs_pkey PRIMARY KEY (id)
);

-- Tạo index
CREATE INDEX idx_order_logs_order_id ON order_logs(order_id);