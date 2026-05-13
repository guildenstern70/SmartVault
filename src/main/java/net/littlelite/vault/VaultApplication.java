/*
 * SmartVault Project
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VaultApplication
{

    public static void main(String[] args)
    {
        System.exit(SpringApplication.exit(SpringApplication.run(VaultApplication.class, args)));
    }

}

