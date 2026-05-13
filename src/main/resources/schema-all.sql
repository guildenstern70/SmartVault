/*
 * SmartVault Project
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

DROP TABLE people IF EXISTS;

CREATE TABLE people  (
     person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
     first_name VARCHAR(20),
     last_name VARCHAR(20)
);
