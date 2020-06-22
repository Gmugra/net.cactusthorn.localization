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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

public class LocaleFallbackTest {

    private static Localization localization;

    @BeforeAll
    public static void load() throws IOException, URISyntaxException {
        localization = new PathLocalizationLoader("test-app").load();
    }

    @Test
    public void checkOnlyLanguage() {

        Locale locale = new Locale("ru");

        assertEquals("ru_RU", localization.findNearest(locale).toString());
    }

    @Test
    public void checkVariant() {

        Locale locale = new Locale("ru", "RU", "abc");

        assertEquals("ru-RU", localization.findNearest(locale).toLanguageTag());
    }

    @Test
    public void checkExact() {

        Locale locale = new Locale("ru", "RU");

        assertEquals("ru_RU", localization.findNearest(locale).toString());
    }

    @Test
    public void checkNotExists() {

        Locale locale = new Locale("fr");

        assertNull(localization.findNearest(locale));
    }
}
