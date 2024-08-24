ALTER TABLE conversion_history
    ALTER COLUMN amount DECIMAL(19, 6);

ALTER TABLE conversion_history
    ALTER COLUMN converted_amount DECIMAL(19, 6);