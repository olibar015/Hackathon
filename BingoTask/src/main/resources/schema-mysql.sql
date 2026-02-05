-- Create database if not exists
CREATE DATABASE IF NOT EXISTS bingodb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bingodb;

-- Users table
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    level INT DEFAULT 1,
    xp INT DEFAULT 0,
    xp_to_next_level INT DEFAULT 100,
    total_points INT DEFAULT 0,
    current_streak INT DEFAULT 0,
    best_streak INT DEFAULT 0,
    last_activity_date DATE,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_total_points (total_points DESC),
    INDEX idx_last_activity (last_activity_date DESC)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tasks table
CREATE TABLE IF NOT EXISTS tasks (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     title VARCHAR(100) NOT NULL,
    description TEXT,
    points INT NOT NULL,
    category VARCHAR(50),
    icon_url VARCHAR(500),
    position_x INT,
    position_y INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_active (is_active),
    INDEX idx_position (position_x, position_y)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User tasks (completion history)
CREATE TABLE IF NOT EXISTS user_tasks (
                                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                          user_id BIGINT NOT NULL,
                                          task_id BIGINT NOT NULL,
                                          completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          points_earned INT NOT NULL,
                                          counted_for_bingo BOOLEAN DEFAULT FALSE,
                                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    INDEX idx_user_completion (user_id, completed_at DESC),
    INDEX idx_task_completion (task_id, completed_at DESC),
    INDEX idx_user_task_date (user_id, task_id, completed_at)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bingo cards
CREATE TABLE IF NOT EXISTS bingo_cards (
                                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                           user_id BIGINT NOT NULL,
                                           start_date DATE NOT NULL,
                                           end_date DATE NOT NULL,
                                           completed_lines INT DEFAULT 0,
                                           total_points INT DEFAULT 0,
                                           is_active BOOLEAN DEFAULT TRUE,
                                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_active (user_id, is_active),
    INDEX idx_dates (start_date, end_date),
    UNIQUE KEY unique_active_card (user_id, is_active)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bingo cells (individual cells in bingo grid)
CREATE TABLE IF NOT EXISTS bingo_cells (
                                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                           bingo_card_id BIGINT NOT NULL,
                                           task_id BIGINT,
                                           position_x INT NOT NULL,
                                           position_y INT NOT NULL,
                                           is_completed BOOLEAN DEFAULT FALSE,
                                           completed_at TIMESTAMP NULL,
                                           points_earned INT DEFAULT 0,
                                           FOREIGN KEY (bingo_card_id) REFERENCES bingo_cards(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE SET NULL,
    INDEX idx_bingo_card (bingo_card_id),
    INDEX idx_position (position_x, position_y),
    UNIQUE KEY unique_cell_position (bingo_card_id, position_x, position_y)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Achievement definitions (predefined achievements)
CREATE TABLE IF NOT EXISTS achievement_definitions (
                                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                       name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    icon_url VARCHAR(500),
    category VARCHAR(50),
    criteria_type VARCHAR(50) NOT NULL,
    criteria_value INT NOT NULL,
    points_reward INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_rare BOOLEAN DEFAULT FALSE,
    tips TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_criteria (criteria_type),
    INDEX idx_active (is_active)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User achievements (achievements earned by users)
CREATE TABLE IF NOT EXISTS achievements (
                                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            user_id BIGINT NOT NULL,
                                            name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_url VARCHAR(500),
    category VARCHAR(50),
    points_reward INT DEFAULT 0,
    is_rare BOOLEAN DEFAULT FALSE,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_achievements (user_id, earned_at DESC),
    INDEX idx_achievement_name (name),
    UNIQUE KEY unique_user_achievement (user_id, name)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;