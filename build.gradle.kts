/*
 * SmartVault Project
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

plugins {
	java
	id("org.springframework.boot") version "4.1.0-SNAPSHOT"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "net.littlelite"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-batch")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.vault:spring-vault-core:4.0.2")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-batch-test")
	testImplementation("org.assertj:assertj-core:3.27.7")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
