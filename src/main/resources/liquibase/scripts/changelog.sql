--liquibase formatted sql

--changeset ivanogor:1
CREATE TABLE notification_task(
    chat_id SERIAL PRIMARY KEY,
    task TEXT NOT NULL,
    date DATE NOT NULL
)