-- Create databases for all services
CREATE DATABASE IF NOT EXISTS booking_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS order_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS payment_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS guest_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS staff_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create service-specific users with least privilege
CREATE USER IF NOT EXISTS 'booking_user'@'%' IDENTIFIED BY 'booking_pass';
CREATE USER IF NOT EXISTS 'order_user'@'%' IDENTIFIED BY 'order_pass';
CREATE USER IF NOT EXISTS 'payment_user'@'%' IDENTIFIED BY 'payment_pass';
CREATE USER IF NOT EXISTS 'guest_user'@'%' IDENTIFIED BY 'guest_pass';
CREATE USER IF NOT EXISTS 'staff_user'@'%' IDENTIFIED BY 'staff_pass';

-- Grant privileges
GRANT ALL PRIVILEGES ON booking_service.* TO 'booking_user'@'%';
GRANT ALL PRIVILEGES ON order_service.* TO 'order_user'@'%';
GRANT ALL PRIVILEGES ON payment_service.* TO 'payment_user'@'%';
GRANT ALL PRIVILEGES ON guest_service.* TO 'guest_user'@'%';
GRANT ALL PRIVILEGES ON staff_service.* TO 'staff_user'@'%';

FLUSH PRIVILEGES;