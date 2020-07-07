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

import static net.cactusthorn.localization.core.Parameter.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BasicLocalizationTest {

    private static Localization localization;
    private static Locale en_US = Locale.forLanguageTag("en-US");
    private static Locale ru_RU = Locale.forLanguageTag("ru-RU");
    private static Locale fr_FR = Locale.forLanguageTag("fr-FR");

    @BeforeAll
    public static void load() throws IOException, URISyntaxException {
        localization = new PathLocalizationLoader("test-app").load();
    }

    @Test
    public void testKeys() {

        assertEquals("short key", localization.get(en_US, "super"));
        assertEquals("dotkey", localization.get(en_US, "super."));
        assertEquals("dot dollar key", localization.get(en_US, "super.$"));
        assertEquals("dot <br/> value", localization.get(en_US, "superH."));
        assertEquals("dollar &lt;br/&gt; but not plural", localization.get(en_US, "superT.$test"));
    }

    @Test
    public void testParametrs() {

        Map<String, Object> params = asMap(of("first", "AAA"), of("second", "BBB"));

        assertEquals("first: AAA, second:BBB&lt;br/&gt;", localization.get(en_US, "test.param.first", params));
        assertEquals("BBB, BBB, BBB; <strong>AAA, AAA, AAA;</strong>", localization.get(en_US, "test.param.second", params));
    }

    @Test
    public void testParametrs2() {

        Map<String, String> params = new HashMap<>();
        params.put("first", "AAA");
        params.put("second", "BBB");

        assertEquals("first: AAA, second:BBB&lt;br/&gt;", localization.get(en_US, "test.param.first", params));
        assertEquals("BBB, BBB, BBB; <strong>AAA, AAA, AAA;</strong>", localization.get(en_US, "test.param.second", params));
    }

    @Test
    public void testApple() {

        assertEquals("apples by default", localization.get(en_US, "x.y.z.apple"));
        assertEquals("no any apples", localization.get(en_US, "x.y.z.apple", count(0)));
        assertEquals("one apple", localization.get(en_US, "x.y.z.apple", count(1)));
        assertEquals("special case:<br/> 22 apples", localization.get(en_US, "x.y.z.apple", count(22)));
        assertEquals("33<br/> apples", localization.get(en_US, "x.y.z.apple", count(33)));
    }

    @Test
    public void testFormat() {

        assertEquals("2,000.22", localization.format(en_US, "number", 2000.22f));
        assertEquals("2 000,22", localization.format(ru_RU, "number", 2000.22f));
    }

    @Test
    public void testFormatWrongLocale() {

        assertThrows(LocalizationLocaleException.class, () -> localization.format(fr_FR, "number", 2000.22f));
    }

    @Test
    public void testWrongFormatNumber() {

        Exception exception = assertThrows(LocalizationFormatException.class, () -> localization.format(en_US, "number", fr_FR));

        String expectedMessage = "Locale: en-US, format: \"number\", Unknown class for number formatting: java.util.Locale";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testUnavailableLocale() {

        Exception exception = assertThrows(LocalizationLocaleException.class, () -> localization.get(fr_FR, "x.y.z.apple", count(0)));

        String expectedMessage = "Locale: fr-FR, Unavailable locale";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testUnavailableKey() {

        Exception exception = assertThrows(LocalizationKeyException.class, () -> localization.get(en_US, "x.m.z.apple", count(0)));

        String expectedMessage = "Locale: en-US, unavailable key: x.m.z.apple";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testWrongCount() {

        Exception exception = assertThrows(LocalizationException.class, () -> localization.get(en_US, "x.y.z.apple", of(COUNT, "xxxx")));

        String expectedMessage = "Locale: en-US, wrong value \"xxxx\" of {{count}} parameter for the key: x.y.z.apple";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testGetDefaultExists() {

        String text = localization.getDefault(en_US, "x.y.z.apple");
        assertEquals("apples by default", text);
    }

    @Test
    public void testGetDefaultWrongKey() {

        assertThrows(LocalizationKeyException.class, () -> localization.getDefault(en_US, "x.A.z.apple"));
    }

    @Test
    public void testGetDefaultWrongLocale() {

        assertThrows(LocalizationLocaleException.class, () -> localization.getDefault(fr_FR, "x.y.z.apple"));
    }
}
