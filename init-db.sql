-- CREATE DB
CREATE DATABASE IF NOT EXISTS maverick_labs;
USE maverick_labs;

-- CREATE TABLE games
CREATE TABLE IF NOT EXISTS games (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    release_order INT NOT NULL,
    CONSTRAINT uk_code UNIQUE KEY (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- INSERT DATA INTO TABLE games
INSERT INTO games (code, title, release_order) VALUES
('MMX', 'Mega Man X', 1),
('MMX2', 'Mega Man X2', 2),
('MMX3', 'Mega Man X3', 3),
('MMX4', 'Mega Man X4', 4);

