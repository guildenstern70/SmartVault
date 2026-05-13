# AGENTS Guide - SmartVault
## Project Snapshot
- Stack: Spring Boot `4.1.0-SNAPSHOT`, Spring Batch, Spring Vault, JPA (`build.gradle.kts`), Java 21 toolchain.
- Core behavior: one-step batch import from CSV into `persons`, with Vault-provided DB credentials outside `test` profile.

## Architecture and Data Flow
- Entrypoint: `src/main/java/net/littlelite/vault/VaultApplication.java`; app exits after job completion (`SpringApplication.exit(...)`).
- Batch wiring lives in `src/main/java/net/littlelite/vault/config/BatchConfiguration.java`.
- Flow is `sample-data.csv` -> `FlatFileItemReader<PersonDto>` -> `PersonProcessor` (uppercase names) -> `JdbcBatchItemWriter` (`INSERT INTO persons...`).
- `JobCompletionNotificationListener` verifies output by querying `persons` and logging rows.
- Shared DTO contract is `src/main/java/net/littlelite/vault/dto/PersonDto.java` (record with `firstName`, `lastName`).

## Vault/DataSource Boundary
- Vault logic is isolated in `src/main/java/net/littlelite/vault/vault/VaultManager.java`.
- `getPgDataSource()` in `BatchConfiguration` is `@Profile("!test")`; tests intentionally avoid Vault/Postgres wiring.
- Vault path/keys are fixed to `secret/db-credentials` with `database-username` and `database-password`.
- `app.vault.setup-on-startup` controls optional secret bootstrap (`application.yaml`, `application-test.yaml`).
- Current Vault settings are dev-style constants (`127.0.0.1:8200`, token `dev-only-token`); treat as POC defaults.

## Developer Workflows (verified)
```bash
./gradlew --no-daemon test
./gradlew --no-daemon build
./gradlew --no-daemon bootRun
vault server -dev -dev-root-token-id="dev-only-token"
```

## Testing and Conventions
- Unit pattern: instantiate class directly + AssertJ (`src/test/java/net/littlelite/vault/batch/PersonProcessorTest.java`).
- Integration pattern: `@SpringBatchTest` + `JobOperatorTestUtils.startJob()` (`src/test/java/net/littlelite/vault/batch/PersonBatchIntegrationTest.java`).
- Test DB: H2 with schema from `src/test/resources/schema-all.sql`; tests clear `persons` in `@AfterEach`.
- Keep SQL/table naming consistent with `persons` and preserve chunk size `3` in `step1(...)` unless changing batch semantics intentionally.
- `src/main/resources/application.yaml` targets a remote Postgres URL; prefer `test` profile for routine local automation.
