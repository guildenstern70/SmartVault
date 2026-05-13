/*
 * SmartVault Project
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.vault.vault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.Versioned;

import java.util.HashMap;
import java.util.Map;

@Component
public class VaultManager
{

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8200;
    private static final String SCHEME = "http";
    private static final String TOKEN = "dev-only-token";
    private static final String SECRET_PATH = "db-credentials";
    private static final String DB_USERNAME = "postgres.hlkmmtguntxcotpbdgiu";
    private static final String DB_PASSWORD = "*tFBbYKvAJ_bK2";

    private static final Logger log = LoggerFactory.getLogger(VaultManager.class);

    public VaultTemplate getVault()
    {
        log.info("Initializing VaultTemplate with host: {}, port: {}, scheme: {}, token: {}", HOST, PORT, SCHEME, TOKEN);

        VaultEndpoint vaultEndpoint = new VaultEndpoint();

        vaultEndpoint.setHost(HOST);
        vaultEndpoint.setPort(PORT);
        vaultEndpoint.setScheme(SCHEME);

        return new VaultTemplate(
                vaultEndpoint,
                new TokenAuthentication(TOKEN)
        );
    }

    public void setupVault(VaultTemplate vaultTemplate)
    {
        log.info("Setting up Vault with database username and password");

        if (this.getSecret(vaultTemplate, "database-username") != null ||
            this.getSecret(vaultTemplate, "database-password") != null)
        {
            log.info("Secrets already exist in Vault, skipping setup");
            return;
        }

        Map<String, String> dbData = new HashMap<>();
        dbData.put("database-username", DB_USERNAME);
        dbData.put("database-password", DB_PASSWORD);

        vaultTemplate
                .opsForVersionedKeyValue("secret")
                .put(SECRET_PATH, dbData);

        log.info("Vault setup completed");
    }

    public String getSecret(VaultTemplate vaultTemplate,
                            String key)
    {
        log.info("Retrieving secret from Vault at path: db-credentials, key: {}", key);
        String retrievedValue = null;

        Versioned<Map<String, Object>> readResponse = vaultTemplate
                .opsForVersionedKeyValue("secret")
                .get(SECRET_PATH);

        if (readResponse != null && readResponse.hasData()) {
            var data = readResponse.getData();
            if (data != null)
            {
                log.info("Data retrieved from Vault: {}", data);
                retrievedValue = (String) readResponse.getData().get(key);
            } else
            {
                log.warn("No data found at path: db-credentials");
            }

        }

        return retrievedValue;
    }


}
