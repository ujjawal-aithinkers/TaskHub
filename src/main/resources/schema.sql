CREATE TABLE activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    action_details TEXT,
    performed_by BIGINT NOT NULL,
    priority VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);
