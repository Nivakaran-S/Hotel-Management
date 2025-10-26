CREATE TABLE food_orders (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_number VARCHAR(50) UNIQUE NOT NULL,
                             order_type VARCHAR(20) NOT NULL,
                             table_id VARCHAR(50),
                             room_number VARCHAR(50),
                             guest_name VARCHAR(255) NOT NULL,
                             guest_email VARCHAR(255) NOT NULL,
                             guest_phone VARCHAR(50),
                             subtotal DECIMAL(10, 2) NOT NULL,
                             tax_amount DECIMAL(10, 2),
                             service_charge DECIMAL(10, 2),
                             total_amount DECIMAL(10, 2) NOT NULL,
                             status VARCHAR(20) NOT NULL,
                             special_instructions VARCHAR(1000),
                             order_date_time DATETIME NOT NULL,
                             estimated_delivery_time DATETIME,
                             actual_delivery_time DATETIME,
                             created_at DATETIME NOT NULL,
                             updated_at DATETIME NOT NULL,
                             ordered_by VARCHAR(255),

                             INDEX idx_order_number (order_number),
                             INDEX idx_guest_email (guest_email),
                             INDEX idx_status (status),
                             INDEX idx_order_type (order_type),
                             INDEX idx_table_id (table_id),
                             INDEX idx_room_number (room_number),
                             INDEX idx_order_date_time (order_date_time)
);

CREATE TABLE order_items (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_number VARCHAR(50) NOT NULL,
                             menu_item_id VARCHAR(50) NOT NULL,
                             menu_item_name VARCHAR(255) NOT NULL,
                             quantity INT NOT NULL,
                             unit_price DECIMAL(10, 2) NOT NULL,
                             total_price DECIMAL(10, 2) NOT NULL,
                             special_instructions VARCHAR(500),

                             INDEX idx_order_number (order_number),
                             INDEX idx_menu_item_id (menu_item_id),
                             FOREIGN KEY (order_number) REFERENCES food_orders(order_number) ON DELETE CASCADE
);