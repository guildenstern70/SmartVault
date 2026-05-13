# SmartVault

This project is a POC for integrating Spring Batch with HashiCorp Vault. It demonstrates how to securely 
manage secrets and sensitive data in a batch processing application using Vault.

## Setup Vault

Install HashiCorp Vault on your local machine. You can follow the official documentation 
for installation instructions: https://developer.hashicorp.com/vault/docs/install

### Run Application

First of all, start the Vault server in development mode. This will create a Vault instance with a 
single unsealed node and a root token for authentication.

```bash
vault server -dev -dev-root-token-id="dev-only-token"
```

The run the Spring Boot application using the following command:

```bash
./gradlew --no-daemon bootRun
```

## Reference Documentation
https://spring.io/guides/gs/batch-processing
https://developer.hashicorp.com/vault/docs/get-started/developer-qs
https://github.com/hashicorp/vault-examples/blob/main/examples/_quick-start/java/Example.java
