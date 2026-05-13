/*
 * SmartVault Project
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.vault.batch;

import net.littlelite.vault.dto.PersonDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class PersonProcessor implements ItemProcessor<PersonDto, PersonDto>
{
    private static final Logger log = LoggerFactory.getLogger(PersonProcessor.class);

    @Override
    public PersonDto process(final PersonDto personDto)
    {

        final String firstName = personDto.firstName().toUpperCase();
        final String lastName = personDto.lastName().toUpperCase();

        final PersonDto transformedPerson = new PersonDto(firstName, lastName);

        log.info("Converting ({}) into ({})", personDto, transformedPerson);

        return transformedPerson;
    }
}


