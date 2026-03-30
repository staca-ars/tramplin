-- =====================================================
-- База данных платформы «Трамплин»
-- Скрипт создаёт все таблицы с полным набором полей
-- =====================================================

CREATE DATABASE tramplin;
USE tramplin;

-- -----------------------------------------------------
-- Таблица users
-- -----------------------------------------------------
CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(60) NOT NULL,            -- BCrypt хеш (60 символов)
                       name VARCHAR(100) NOT NULL,
                       role VARCHAR(20) NOT NULL,                -- seeker / employer / admin
                       visibility VARCHAR(20) DEFAULT 'public',
                       blocked BOOLEAN DEFAULT FALSE
);

-- -----------------------------------------------------
-- Таблица companies
-- -----------------------------------------------------
CREATE TABLE companies (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           description TEXT,
                           owner_id INT NOT NULL,
                           status VARCHAR(20) DEFAULT 'pending',
                           email VARCHAR(100),
                           phone VARCHAR(50),
                           website VARCHAR(255),
                           logo_url VARCHAR(255),
                           moderation_status VARCHAR(20) DEFAULT 'pending',
                           rejection_reason TEXT,
                           cover_url VARCHAR(255),
                           FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Таблица opportunities
-- -----------------------------------------------------
CREATE TABLE opportunities (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               title VARCHAR(200) NOT NULL,
                               description TEXT,
                               company_id INT NOT NULL,
                               type VARCHAR(50),                        -- internship / job / event / mentorship
                               format VARCHAR(50),                      -- remote / office / hybrid
                               location VARCHAR(100),
                               lat DOUBLE,
                               lng DOUBLE,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               expires_at TIMESTAMP NULL DEFAULT NULL,
                               moderation_status VARCHAR(20) DEFAULT 'pending',
                               rejection_reason TEXT,
                               cover_url VARCHAR(255),
                               FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Таблица applications
-- -----------------------------------------------------
CREATE TABLE applications (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              user_id INT NOT NULL,
                              opportunity_id INT NOT NULL,
                              status VARCHAR(20) DEFAULT 'pending',
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                              FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Таблица tags
-- -----------------------------------------------------
CREATE TABLE tags (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(50) UNIQUE NOT NULL
);

-- -----------------------------------------------------
-- Таблица opportunity_tags
-- -----------------------------------------------------
CREATE TABLE opportunity_tags (
                                  opportunity_id INT NOT NULL,
                                  tag_id INT NOT NULL,
                                  PRIMARY KEY (opportunity_id, tag_id),
                                  FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE,
                                  FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Таблица connections
-- -----------------------------------------------------
CREATE TABLE connections (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             user1_id INT NOT NULL,
                             user2_id INT NOT NULL,
                             FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE,
                             FOREIGN KEY (user2_id) REFERENCES users(id) ON DELETE CASCADE,
                             UNIQUE KEY unique_connection (user1_id, user2_id)
);

-- -----------------------------------------------------
-- Таблица seeker_profiles
-- -----------------------------------------------------
CREATE TABLE seeker_profiles (
                                 user_id INT PRIMARY KEY,
                                 full_name VARCHAR(100),
                                 university VARCHAR(100),
                                 course INT,
                                 graduation_year INT,
                                 skills TEXT,
                                 projects TEXT,
                                 github VARCHAR(255),
                                 portfolio VARCHAR(255),
                                 photo_url VARCHAR(255),
                                 cover_url VARCHAR(255),
                                 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- -----------------------------------------------------
-- Таблица favorites
-- -----------------------------------------------------
CREATE TABLE favorites (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           user_id INT NOT NULL,
                           opportunity_id INT NOT NULL,
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                           FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE,
                           UNIQUE KEY unique_favorite (user_id, opportunity_id)
);

-- -----------------------------------------------------
-- Добавим Администратора (Пароль: admin)
-- -----------------------------------------------------

INSERT INTO users (email, password, name, role, visibility, blocked)
VALUES ('admin@tramplin.ru', '$2a$10$xSQjIKvlCh0N30qwFgtcWeoXEMZOeqTosFDEZwc0Alq45as4OguZW', 'Admin', 'admin', 'public', 0);