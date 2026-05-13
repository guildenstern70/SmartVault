package net.littlelite.vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VaultApplication {

	public static void main(String[] args) {

		System.exit(SpringApplication.exit(SpringApplication.run(VaultApplication.class, args)));
	}

}

