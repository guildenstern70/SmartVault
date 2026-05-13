/*
 * SmartVault Project
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

DROP TABLE persons IF EXISTS;

CREATE TABLE persons  (
    person_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);
