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
    id UUID NOT NULL,                                   -- Định danh tự tăng cho mỗi món trong đơn hàng
    order_id UUID NOT NULL,                                -- Định danh của đơn hàng liên quan
    product_id UUID NOT NULL,                              -- Định danh của sản phẩm
    price NUMERIC(10,2) NOT NULL CHECK (price >= 0),       -- Giá của món, kiểm tra không âm
    quantity INTEGER NOT NULL CHECK (quantity > 0),        -- Số lượng, kiểm tra lớn hơn 0
    sub_total NUMERIC(10,2) NOT NULL CHECK (sub_total >= 0), -- Tổng tiền của món, kiểm tra không âm
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Thời điểm tạo bản ghi, mặc định là thời gian hiện tại
    updated_at TIMESTAMP WITH TIME ZONE,                   -- Thời điểm cập nhật bản ghi cuối cùng
    CONSTRAINT order_items_pkey PRIMARY KEY (id, order_id) -- Khóa chính ghép từ id và order_id
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