/*******************************************************************************
 * Copyright (C) 2017, Alexei Khatskevich
 * All rights reserved.
 *
 * Licensed under the BSD 2-clause (Simplified) License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/BSD-2-Clause
 ******************************************************************************/
package net.cactusthorn.localization;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;

public class LocalizationLoaderTest {

    @Test
    public void testNotDirectory() throws URISyntaxException, IOException {

        Exception exception = assertThrows(IOException.class,
                () -> new PathLocalizationLoader("test-app").from("L10n/ru-RU.properties").load());

        assertTrue(exception.getMessage().contains("is not directory"));
    }

    @Test
    public void testWrongSystemId() throws URISyntaxException, IOException {

        Exception exception = assertThrows(IOException.class,
                () -> new PathLocalizationLoader("my-super-app").from("WrongSystemId").instanceOf(LoggingLocalization.class).load());

        assertTrue(exception.getMessage().contains("Something wrong with file \"fr-CA.properties\""));
        assertEquals("Wrong _system.id=test-app, expected: _system.id=my-super-app", exception.getCause().getMessage());
    }

    @Test
    public void testWrongLanguageTag() throws URISyntaxException, IOException {

        Exception exception = assertThrows(IOException.class, () -> new PathLocalizationLoader("test-app").from("WrongLanguageTag").load());

        assertTrue(exception.getMessage().contains("Something wrong with file \"fr-CA.properties\""));
        assertEquals("Wrong value of _system.languageTag=en-US, expected: _system.languageTag=fr-CA", exception.getCause().getMessage());
    }
}
