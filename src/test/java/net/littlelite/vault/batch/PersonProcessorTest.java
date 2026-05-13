/*
 * SmartVault Project
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.vault.batch;

import net.littlelite.vault.dto.PersonDto;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PersonProcessorTest {

    @Test
    void shouldTransformToUppercase() {
        PersonProcessor processor = new PersonProcessor();
        PersonDto input = new PersonDto("John", "Doe");
        
        PersonDto output = processor.process(input);
        
        assertThat(output).isNotNull();
        assertThat(output.firstName()).isEqualTo("JOHN");
        assertThat(output.lastName()).isEqualTo("DOE");
    }
}
