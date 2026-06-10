CREATE DATABASE IF NOT EXISTS drs;
USE drs;

CREATE TABLE IF NOT EXISTS disaster_reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    report_title VARCHAR(150) NOT NULL DEFAULT 'Untitled Disaster Report',
    disaster_type VARCHAR(60) NOT NULL,
    severity VARCHAR(40) NOT NULL,
    location VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    reported_by VARCHAR(100) NOT NULL,
    contact_number VARCHAR(40) NOT NULL,
    status VARCHAR(40) NOT NULL,
    priority_level VARCHAR(40) NOT NULL,
    evacuation_advice TEXT,
    recommended_resources TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS disaster_assessments (
    assessment_id INT AUTO_INCREMENT PRIMARY KEY,
    report_id INT NOT NULL,
    damage_level VARCHAR(40) NOT NULL,
    people_affected INT NOT NULL,
    infrastructure_damage BOOLEAN NOT NULL,
    priority_score INT NOT NULL,
    priority_level VARCHAR(40) NOT NULL,
    assessment_summary TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assessment_report FOREIGN KEY (report_id)
        REFERENCES disaster_reports(report_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(120) NOT NULL UNIQUE,
    service_type VARCHAR(120) NOT NULL,
    contact_number VARCHAR(40) NOT NULL,
    availability_status VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS response_tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    report_id INT NOT NULL,
    department_id INT NOT NULL,
    activity_type VARCHAR(80) NOT NULL,
    task_description TEXT NOT NULL,
    priority_level VARCHAR(40) NOT NULL,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_report FOREIGN KEY (report_id)
        REFERENCES disaster_reports(report_id) ON DELETE CASCADE,
    CONSTRAINT fk_task_department FOREIGN KEY (department_id)
        REFERENCES departments(department_id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS resources (
    resource_id INT AUTO_INCREMENT PRIMARY KEY,
    resource_name VARCHAR(120) NOT NULL UNIQUE,
    category VARCHAR(80) NOT NULL,
    quantity_available INT NOT NULL
);

CREATE TABLE IF NOT EXISTS resource_allocations (
    allocation_id INT AUTO_INCREMENT PRIMARY KEY,
    report_id INT NOT NULL,
    resource_id INT NOT NULL,
    quantity_allocated INT NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_allocation_report FOREIGN KEY (report_id)
        REFERENCES disaster_reports(report_id) ON DELETE CASCADE,
    CONSTRAINT fk_allocation_resource FOREIGN KEY (resource_id)
        REFERENCES resources(resource_id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(80) NOT NULL UNIQUE,
    role VARCHAR(40) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_events (
    audit_id INT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(60) NOT NULL,
    entity_id INT NOT NULL,
    entity_label VARCHAR(180) NOT NULL,
    action_type VARCHAR(80) NOT NULL,
    username VARCHAR(80) NOT NULL,
    change_details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Default application users.
-- Plain-text passwords for testing/marking:
-- admin/Admin@123, reporter/Reporter@123, assessment_officer/Assessment@123,
-- resource_officer/Resource@123, department_officer/Department@123, auditor/Auditor@123
INSERT IGNORE INTO users (username, role, password_hash) VALUES
('admin', 'ADMIN', '6G94qKPK8LYNjnTllCqm2G3BUM08AzOK7yW30tfjrMc='),
('reporter', 'REPORTER', 'jVqWnxXmyMSNqxSNDw/GkHz171lqXOetbx7yUG9BIL8='),
('assessment_officer', 'ASSESSMENT_OFFICER', 'hngSU66pQtyJkXXyPB94BjLyjYIvVAU2hRRzJc9aOA0='),
('resource_officer', 'RESOURCE_OFFICER', 's/bsxNCKVBv59nRMrZOv7aQw9Jjlqsza2e0VICz1jGw='),
('department_officer', 'DEPARTMENT_OFFICER', 'lOW8HGfMzUlAkkTTPbN6oyBMIUGTR9sh7KHoQDJ/58s='),
('auditor', 'AUDITOR', 'tCwW8FwW5g7e0/RGC88LQbXEnpzMRT5weZQJ2IV1t/g=');

-- Default departments and external organisations.
INSERT IGNORE INTO departments (department_name, service_type, contact_number, availability_status) VALUES
('Fire and Emergency', 'Fire control, rescue and evacuation', '000-FIRE', 'Available'),
('Hospital and Ambulance', 'Medical support and patient transport', '000-MED', 'Available'),
('Police', 'Public safety and law enforcement', '000-POL', 'Available'),
('Electricity Authority', 'Power isolation and repair', '131-ELEC', 'Available'),
('Transportation Department', 'Road access and transport coordination', '131-ROAD', 'Available'),
('Waste Management', 'Debris and waste removal', '131-WASTE', 'Available'),
('Water Supply', 'Clean water and supply restoration', '131-WATER', 'Available'),
('School Emergency Liaison', 'School safety and temporary shelter support', '131-SCHOOL', 'Available');

-- Default emergency resources.
INSERT IGNORE INTO resources (resource_name, category, quantity_available) VALUES
('Fire Truck', 'Fire Response', 8),
('Ambulance', 'Medical', 12),
('Police Patrol Unit', 'Security', 10),
('Evacuation Team', 'Evacuation', 6),
('Rescue Boat', 'Flood Rescue', 5),
('Search and Rescue Team', 'Rescue', 7),
('Temporary Shelter Kit', 'Relief', 50),
('Medical Team', 'Medical', 9),
('Electricity Repair Team', 'Infrastructure', 4),
('Debris Removal Truck', 'Waste Management', 6),
('Water Supply Tanker', 'Water Supply', 5);
