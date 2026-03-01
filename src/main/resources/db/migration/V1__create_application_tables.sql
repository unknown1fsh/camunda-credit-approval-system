CREATE TABLE IF NOT EXISTS credit_applications (
    id BIGSERIAL PRIMARY KEY,
    application_id VARCHAR(50) NOT NULL UNIQUE,
    process_instance_id VARCHAR(255),
    customer_id VARCHAR(100) NOT NULL,
    customer_name VARCHAR(200) NOT NULL,
    national_id VARCHAR(11) NOT NULL,
    age INTEGER NOT NULL,
    email VARCHAR(200) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    loan_amount DECIMAL(15,2) NOT NULL,
    term_months INTEGER NOT NULL,
    currency VARCHAR(10) DEFAULT 'TRY',
    employment_status VARCHAR(50) NOT NULL,
    monthly_income DECIMAL(15,2),
    employer VARCHAR(200),
    customer_segment VARCHAR(20),
    credit_history INTEGER,
    debt_to_income_ratio DECIMAL(5,2),
    existing_loans INTEGER,
    status VARCHAR(30) NOT NULL,
    risk_category VARCHAR(20),
    risk_score INTEGER,
    approver_level VARCHAR(30),
    rejection_reason VARCHAR(1000),
    approved_by VARCHAR(200),
    approved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_application_id ON credit_applications (application_id);
CREATE INDEX IF NOT EXISTS idx_process_instance_id ON credit_applications (process_instance_id);
CREATE INDEX IF NOT EXISTS idx_customer_id ON credit_applications (customer_id);
CREATE INDEX IF NOT EXISTS idx_status ON credit_applications (status);

CREATE TABLE IF NOT EXISTS risk_scores (
    id BIGSERIAL PRIMARY KEY,
    application_id VARCHAR(50) NOT NULL,
    total_score INTEGER NOT NULL,
    category VARCHAR(20) NOT NULL,
    credit_history_score INTEGER,
    income_score INTEGER,
    debt_ratio_score INTEGER,
    employment_score INTEGER,
    age_score INTEGER,
    credit_analyst_score INTEGER,
    credit_analyst_notes VARCHAR(1000),
    risk_analyst_score INTEGER,
    risk_analyst_notes VARCHAR(1000),
    fraud_score INTEGER,
    fraud_detected BOOLEAN DEFAULT FALSE,
    fraud_indicators VARCHAR(1000),
    recommended_action VARCHAR(20),
    evaluated_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_rs_application_id ON risk_scores (application_id);
CREATE INDEX IF NOT EXISTS idx_rs_category ON risk_scores (category);
CREATE INDEX IF NOT EXISTS idx_rs_fraud_detected ON risk_scores (fraud_detected);

CREATE TABLE IF NOT EXISTS documents (
    id BIGSERIAL PRIMARY KEY,
    application_id VARCHAR(50) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    file_path VARCHAR(500),
    file_name VARCHAR(200),
    file_size BIGINT,
    mime_type VARCHAR(100),
    uploaded_at TIMESTAMP,
    verified_at TIMESTAMP,
    verified_by VARCHAR(100),
    verification_notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_doc_application_id ON documents (application_id);
CREATE INDEX IF NOT EXISTS idx_doc_document_type ON documents (document_type);
CREATE INDEX IF NOT EXISTS idx_doc_status ON documents (status);

CREATE TABLE IF NOT EXISTS approval_decisions (
    id BIGSERIAL PRIMARY KEY,
    application_id VARCHAR(50) NOT NULL,
    task_id VARCHAR(255) NOT NULL,
    approver_level VARCHAR(30) NOT NULL,
    approver VARCHAR(100) NOT NULL,
    decision VARCHAR(20) NOT NULL,
    comments VARCHAR(2000),
    conditions VARCHAR(1000),
    escalated BOOLEAN DEFAULT FALSE,
    escalated_from VARCHAR(100),
    escalated_to VARCHAR(100),
    escalated_at TIMESTAMP,
    decided_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_ad_application_id ON approval_decisions (application_id);
CREATE INDEX IF NOT EXISTS idx_ad_task_id ON approval_decisions (task_id);
CREATE INDEX IF NOT EXISTS idx_ad_approver ON approval_decisions (approver);
CREATE INDEX IF NOT EXISTS idx_ad_escalated ON approval_decisions (escalated);

INSERT INTO credit_applications (application_id, customer_id, customer_name, national_id, age, email, phone,
    loan_amount, term_months, employment_status, monthly_income, status, created_at, updated_at)
VALUES
    ('APP-2026-00001', 'CUST001', 'Ahmet Yılmaz', '12345678901', 35, 'ahmet@example.com', '5551234567',
     50000.00, 12, 'PERMANENT', 15000.00, 'DRAFT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('APP-2026-00002', 'CUST002', 'Ayşe Demir', '98765432109', 28, 'ayse@example.com', '5559876543',
     100000.00, 24, 'SELF_EMPLOYED', 20000.00, 'DRAFT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (application_id) DO NOTHING;
