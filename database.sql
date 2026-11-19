DROP DATABASE IF EXISTS ev_charging_service;
CREATE DATABASE ev_charging_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ev_charging_service;

-- 1. Таблиця зарядних станцій
CREATE TABLE station (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(160) NOT NULL,
    city VARCHAR(120) NOT NULL
);

-- 2. Таблиця зарядних модулів
CREATE TABLE charging_module (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_id BIGINT NOT NULL,
    module_code VARCHAR(40) NOT NULL,
    connector_type VARCHAR(40) NOT NULL,
    power_kw DOUBLE NOT NULL,
    load_score DOUBLE NOT NULL,
    deficit_score DOUBLE NOT NULL,
    FOREIGN KEY (station_id) REFERENCES station(id)
);

-- 3. Таблиця інцидентів (об'єкт життєвого циклу)
CREATE TABLE incident (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    module_id BIGINT NOT NULL,
    description VARCHAR(500) NOT NULL,
    state VARCHAR(30) NOT NULL,
    f1 DOUBLE NOT NULL,
    f2 DOUBLE NOT NULL,
    f3 DOUBLE NOT NULL,
    priority_index DOUBLE NOT NULL,
    FOREIGN KEY (module_id) REFERENCES charging_module(id)
);

-- 4. Таблиця ремонтних завдань
CREATE TABLE repair_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    incident_id BIGINT NOT NULL,
    description VARCHAR(500) NOT NULL,
    planned_date VARCHAR(10) NOT NULL,
    FOREIGN KEY (incident_id) REFERENCES incident(id)
);

-- Початкові демонстраційні дані: станції
INSERT INTO station (code, name, city) VALUES
('PLT-EV01', 'ЕкоЗаряд Центр', 'Полтава'),
('PLT-EV02', 'ЕкоЗаряд Траса М03', 'Полтава');

-- Початкові демонстраційні дані: зарядні модулі
INSERT INTO charging_module (station_id, module_code, connector_type, power_kw, load_score, deficit_score) VALUES
(1, 'MOD-101', 'CCS2', 120.0, 80.0, 60.0),
(1, 'MOD-102', 'Type 2', 22.0, 65.0, 60.0),
(2, 'MOD-201', 'CHAdeMO', 50.0, 90.0, 85.0);

-- Початкові демонстраційні дані: інцидент (I = 0.40*80 + 0.35*70 + 0.25*60 = 71.50)
INSERT INTO incident (module_id, description, state, f1, f2, f3, priority_index) VALUES
(1, 'Збій контролера живлення постійного струму', 'RECORDED', 80.0, 70.0, 60.0, 71.50);

-- Початкові демонстраційні дані: ремонтне завдання
INSERT INTO repair_task (incident_id, description, planned_date) VALUES
(1, 'Діагностика силового блока та заміна плати керування', '2026-11-25');
