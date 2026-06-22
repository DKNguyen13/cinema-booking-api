CREATE TABLE rooms (
    room_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL UNIQUE,
    total_seats INT NOT NULL
);