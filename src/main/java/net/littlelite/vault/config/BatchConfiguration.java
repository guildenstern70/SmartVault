/*
 * SmartVault Project
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.vault.config;

import net.littlelite.vault.batch.JobCompletionNotificationListener;
import net.littlelite.vault.batch.PersonProcessor;
import net.littlelite.vault.dto.PersonDto;
import net.littlelite.vault.vault.VaultManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration
{
    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);
    private final VaultManager vaultManager;

    @Value("${app.vault.setup-on-startup:false}")
    private boolean setupVaultOnStartup;

    @Value("${app.datasource.url:unknown}")
    private String dataSourceUrl;

    @Autowired
    public BatchConfiguration(VaultManager vaultManager)
    {
        this.vaultManager = vaultManager;
    }

    @Bean
    @Profile("!test")
    public DataSource getPgDataSource()
    {
        var vaultTemplate = this.vaultManager.getVault();

        if (setupVaultOnStartup)
        {
            this.vaultManager.setupVault(vaultTemplate);
        }

        // Read DB credentials from Vault after optional bootstrap.
        String dbUsername = this.vaultManager.getSecret(vaultTemplate,
                "database-username");
        String dbPassword = this.vaultManager.getSecret(vaultTemplate,
                "database-password");

        if (dbUsername == null || dbPassword == null)
        {
            throw new IllegalStateException("Missing database credentials in Vault at path 'secret/db-credentials'");
        }

        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(dataSourceUrl);
        dataSourceBuilder.username(dbUsername);
        dataSourceBuilder.password(dbPassword);
        return dataSourceBuilder.build();
    }

    @Bean
    public FlatFileItemReader<PersonDto> reader()
    {
        return new FlatFileItemReaderBuilder<PersonDto>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names("firstName", "lastName")
                .targetType(PersonDto.class)
                .build();
    }

    @Bean
    public PersonProcessor processor()
    {
        return new PersonProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<PersonDto> writer(DataSource dataSource)
    {
        return new JdbcBatchItemWriterBuilder<PersonDto>()
                .sql("INSERT INTO persons (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository,
                             Step step1,
                             JobCompletionNotificationListener listener)
    {
        log.info("Setting up import user job");

        if (setupVaultOnStartup)
        {
            // Initialize Vault secrets only when explicitly enabled for the active profile.
            var vaultTemplate = this.vaultManager.getVault();
            this.vaultManager.setupVault(vaultTemplate);
        }

        return new JobBuilder(jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      FlatFileItemReader<PersonDto> reader,
                      PersonProcessor processor,
                      JdbcBatchItemWriter<PersonDto> writer)
    {
        return new StepBuilder(jobRepository)
                .<PersonDto, PersonDto>chunk(3)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
