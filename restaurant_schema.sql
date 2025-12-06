-- Create restaurant table
CREATE TABLE IF NOT EXISTS restaurant (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address TEXT,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- Create menus table
CREATE TABLE IF NOT EXISTS menus (
    id UUID PRIMARY KEY,
    restaurant_id UUID NOT NULL,
    product_id UUID NOT NULL,
    category_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    available BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- Create order_approvals table
CREATE TABLE IF NOT EXISTS order_approvals (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    restaurant_id UUID NOT NULL,
    approval_status VARCHAR(20) NOT NULL,
    rejection_reason TEXT,
    approved_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

