/*
 * SmartVault Project
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.vault.batch;

import net.littlelite.vault.VaultApplication;
import net.littlelite.vault.config.BatchConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest(classes = {VaultApplication.class,
                           BatchConfiguration.class})
@ActiveProfiles("test")
class PersonBatchIntegrationTest {

    @Autowired
    private JobOperatorTestUtils jobOperatorTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("DELETE FROM persons");
    }

    @Test
    void testJob() throws Exception {
        JobExecution jobExecution = jobOperatorTestUtils.startJob();
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM persons", Integer.class);
        assertThat(count).isGreaterThan(0);
    }
}
