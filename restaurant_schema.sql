-- 1. Bảng Restaurant
CREATE TABLE IF NOT EXISTS "restaurant" (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- 2. Bảng Menus
CREATE TABLE IF NOT EXISTS "menus" (
    id UUID PRIMARY KEY,
    restaurant_id UUID NOT NULL REFERENCES "restaurant"(id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    category_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- 3. Bảng Order Approvals
CREATE TABLE IF NOT EXISTS "order_approvals" (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    restaurant_id UUID NOT NULL REFERENCES "restaurant"(id),
    approval_status VARCHAR(20) NOT NULL,
    rejection_reason TEXT,
    approved_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,
    UNIQUE(order_id, restaurant_id)
);

-- Tạo index đúng cú pháp (không có schema "restaurant".xxx nữa)
CREATE INDEX IF NOT EXISTS idx_restaurants_name ON "restaurant" (name);
CREATE INDEX IF NOT EXISTS idx_menus_restaurant_id ON "menus" (restaurant_id);
CREATE INDEX IF NOT EXISTS idx_order_approvals_order_id ON "order_approvals" (order_id);