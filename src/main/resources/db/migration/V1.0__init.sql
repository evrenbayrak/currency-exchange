CREATE TABLE conversion_history
(
    transaction_id   VARCHAR(36) PRIMARY KEY,
    base_currency    VARCHAR(3) NOT NULL,
    target_currency  VARCHAR(3) NOT NULL,
    amount           DOUBLE     NOT NULL,
    converted_amount DOUBLE     NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);