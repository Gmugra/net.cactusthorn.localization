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
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

import static net.cactusthorn.localization.Parameter.*;

public class LoggingLocalizationTest extends WithLoggerTestAncestor {

    @Override
    protected Logger getLogger() {
        return (Logger) LoggerFactory.getLogger(LoggingLocalization.class);
    }

    private static Localization localization;

    private static Locale en = Locale.forLanguageTag("en"); // also locale fallback will work
    private static Locale fr_FR = Locale.forLanguageTag("fr-FR");

    @BeforeAll
    public static void loadL10n() throws IOException, URISyntaxException {

        localization = new PathLocalizationLoader("test-app").instanceOf(LoggingLocalization.class).load();
    }

    @Test
    public void testUnavailableLocale() throws IOException {

        String text = localization.get(fr_FR, "x.y.z.apple", count(0));

        assertTrue(isCauseMessageInLog(Level.ERROR, "Locale: fr-FR, Unavailable locale"));

        assertEquals("Locale: fr-FR, Unavailable locale", text);
    }

    @Test
    public void testUnavailableKey() {

        String text = localization.get(en, "x.m.z.apple", count(0));

        assertTrue(isCauseMessageInLog(Level.ERROR, "Locale: en-US, unavailable key: x.m.z.apple"));

        assertEquals("Locale: en-US, unavailable key: x.m.z.apple", text);
    }

    @Test
    public void testWrongCount() {

        String text = localization.get(en, "x.y.z.apple", of(COUNT, "xxxx"));

        assertTrue(isCauseMessageInLog(Level.ERROR, "Locale: en-US, wrong value \"xxxx\" of {{count}} parameter for the key: x.y.z.apple"));

        assertEquals("apples by default", text);
    }

    @Test
    public void testWrongFormatNumber() {

        String text = localization.format(en, "number", fr_FR);

        assertTrue(isCauseMessageInLog(Level.ERROR,
                "Locale: en-US, format: \"number\", Unknown class for number formatting: java.util.Locale"));

        assertEquals("fr_FR", text);
    }

    @Test
    public void testNull() {

        String text = localization.format(en, "number", null);

        assertEquals("null", text);
    }
}
