CREATE TABLE IF NOT EXISTS aggregation_conversions(
    user_id int4 NOT NULL,
    chat_id int4 NOT NULL,
    json_data JSONB,
    created_at TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY (user_id, chat_id)
);

CREATE INDEX IF NOT EXISTS aggregation_conversions_idx
ON aggregation_conversions(user_id, chat_id);