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
package net.cactusthorn.localization.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.cactusthorn.localization.LocalizationException;
import net.cactusthorn.localization.LocalizationFormatException;
import net.cactusthorn.localization.formats.Formats;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.cactusthorn.localization.core.Parameter.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LocalizationKeyTest {

    private static Sys sysEN;
    private static Formats formatsEN;

    private static Sys sysRU;
    private static Formats formatsRU;
    private static LocalizationKey trRU;

    @BeforeAll
    public static void setUpEN() throws URISyntaxException, IOException {

        Map<String, String> props = load("L10n/en-US.properties");
        sysEN = new Sys(props);
        formatsEN = new Formats(sysEN.locale(), props);
    }

    @BeforeAll
    public static void setUpRU() throws URISyntaxException, IOException {

        Map<String, String> props = load("L10n/ru-RU.properties");
        sysRU = new Sys(props);
        formatsRU = new Formats(sysRU.locale(), props);

        trRU = new LocalizationKey("test.key").setDefault("default text", sysRU.escapeHtml())
                .addPluralSpecial(0, "\u0412\u043E\u043E\u0431\u0449\u0435 \u043D\u0435\u0442 \u044F\u0431\u043B\u043E\u043A",
                        sysRU.escapeHtml())
                .addPluralSpecial(1, "\u041E\u0434\u043D\u043E \u044F\u0431\u043B\u043E\u043A\u043E", sysRU.escapeHtml())
                .addPlural(0, "{{count}} \u044F\u0431\u043B\u043E\u043A\u043E", sysRU.escapeHtml())
                .addPlural(1, "{{count}} \u044F\u0431\u043B\u043E\u043A\u0430", sysRU.escapeHtml())
                .addPlural(2, "{{count}} \u044F\u0431\u043B\u043E\u043A", sysRU.escapeHtml());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Map<String, String> load(String resourceName) throws URISyntaxException, IOException {
        Properties properties = new Properties();
        Path path = Paths.get(LocalizationKeyTest.class.getClassLoader().getResource(resourceName).toURI());
        try (BufferedReader buf = Files.newBufferedReader(path, UTF_8)) {
            properties.load(buf);
        }
        return (Map) properties;
    }

    @Test
    public void testCombineWith1() throws URISyntaxException, IOException {

        LocalizationKey trOne = new LocalizationKey("testSimple.key").setDefault("default message one", sysEN.escapeHtml());
        LocalizationKey trTwo = new LocalizationKey("testSimple.key").setDefault("default message two", sysEN.escapeHtml());

        trOne.combineWith(trTwo);

        assertEquals("default message two", trOne.get(sysEN, formatsEN));
    }

    @Test
    public void testCombineWith2() throws URISyntaxException, IOException {

        LocalizationKey trOne = new LocalizationKey("testSimple.key").addPlural(0, "single", sysEN.escapeHtml());
        LocalizationKey trTwo = new LocalizationKey("testSimple.key").setDefault("default message two", sysEN.escapeHtml());

        trOne.combineWith(trTwo);

        assertEquals("single", trOne.get(sysEN, formatsEN, count(1)));

        trOne = new LocalizationKey("testSimple.key").setDefault("default message two", sysEN.escapeHtml());
        trTwo = new LocalizationKey("testSimple.key").addPlural(0, "single", sysEN.escapeHtml());

        trOne.combineWith(trTwo);

        assertEquals("single", trOne.get(sysEN, formatsEN, count(1)));
    }

    @Test
    public void testCombineWith3() throws URISyntaxException, IOException {

        LocalizationKey trOne = new LocalizationKey("testSimple.key").addPluralSpecial(0, "single", sysEN.escapeHtml());
        LocalizationKey trTwo = new LocalizationKey("testSimple.key").setDefault("default message two", sysEN.escapeHtml());

        trOne.combineWith(trTwo);

        assertEquals("single", trOne.get(sysEN, formatsEN, count(0)));

        trOne = new LocalizationKey("testSimple.key").setDefault("default message two", sysEN.escapeHtml());
        trTwo = new LocalizationKey("testSimple.key").addPluralSpecial(0, "single", sysEN.escapeHtml());

        trOne.combineWith(trTwo);

        assertEquals("single", trOne.get(sysEN, formatsEN, count(0)));
    }

    @Test
    public void testSimple() {

        LocalizationKey tr = new LocalizationKey("testSimple.key").setDefault("default message", sysEN.escapeHtml());

        assertEquals("default message", tr.get(sysEN, formatsEN));
    }

    @Test
    public void testEscapeHtml() {

        LocalizationKey tr = new LocalizationKey("testSimple.key").setDefault("default <strong>&</strong> <br/> message",
                sysEN.escapeHtml());

        assertEquals("default &lt;strong&gt;&amp;&lt;/strong&gt; &lt;br/&gt; message", tr.get(sysEN, formatsEN));
    }

    @Test
    public void testNotEscapeHtml() {

        LocalizationKey tr = new LocalizationKey("testSimple.key").setDefault("default <strong>&</strong> <br/> message", false);

        assertEquals("default <strong>&</strong> <br/> message", tr.get(sysEN, formatsEN));
    }

    @Test
    public void testSimpleMissingParam() {

        LocalizationKey tr = new LocalizationKey("testSimpleMissingParam.key").setDefault("defaultX {{param1}} message {{param2}} ",
                sysEN.escapeHtml());

        assertEquals("defaultX {{param1}} message {{param2}} ", tr.get(sysEN, formatsEN, new HashMap<>()));
    }

    @Test
    public void testSimpleParam() {

        LocalizationKey tr = new LocalizationKey("testSimpleParam.key").setDefault("default {{param1}} message {{param2}} XYZ",
                sysEN.escapeHtml());

        assertEquals("default AAA message BBB XYZ", tr.get(sysEN, formatsEN, of("param1", "AAA"), of("param2", "BBB")));
    }

    @Test
    public void testSimpleSpecials() {

        LocalizationKey tr = new LocalizationKey("testSimpleSpecials.key").setDefault("default text", sysEN.escapeHtml())
                .addPluralSpecial(7, "special text {{boom}} for 7!", sysEN.escapeHtml());

        assertEquals("special text {{boom}} for 7!", tr.get(sysEN, formatsEN, count(7)));

        assertEquals("default text", tr.get(sysEN, formatsEN));
    }

    @Test
    public void testSimplePlural() {

        LocalizationKey tr = new LocalizationKey("testSimplePlural.key").setDefault("default text", sysEN.escapeHtml())
                .addPluralSpecial(7, "special text {{boom}} for 7!", sysEN.escapeHtml()).addPlural(0, "single", sysEN.escapeHtml())
                .addPlural(1, "not {{kaboom}} single", sysEN.escapeHtml());

        assertEquals("single", tr.get(sysEN, formatsEN, count(1)));

        assertEquals("not {{kaboom}} single", tr.get(sysEN, formatsEN, count(3)));

        assertEquals("special text BOOM for 7!", tr.get(sysEN, formatsEN, count(7), of("boom", "BOOM")));
    }

    @Test
    public void testPluralRU() {

        assertEquals("47563 \u044F\u0431\u043B\u043E\u043A\u0430", trRU.get(sysRU, formatsRU, count(47563)));
    }

    @Test
    public void testWrongParameters1() {

        LocalizationKey tr = new LocalizationKey("testSimpleParam.key").setDefault("default {{param1}} message {{param2 XYZ",
                sysEN.escapeHtml());

        assertEquals("default AAA message {{param2 XYZ", tr.get(sysEN, formatsEN, of("param1", "AAA"), of("param2", "BBB")));
    }

    @Test
    public void testWrongParameters2() {

        LocalizationKey tr = new LocalizationKey("testSimpleParam.key").setDefault("default param1}} message {{param2}} XYZ",
                sysEN.escapeHtml());

        assertEquals("default param1}} message BBB XYZ", tr.get(sysEN, formatsEN, of("param1", "AAA"), of("param2", "BBB")));
    }

    @Test
    public void testWrongParameters3() {

        LocalizationKey tr = new LocalizationKey("testWrongParameters3.key").setDefault("default {{param1 message param2}} XYZ",
                sysEN.escapeHtml());

        assertEquals("default {{param1 message param2}} XYZ", tr.get(sysEN, formatsEN, of("param1", "AAA"), of("param2", "BBB")));
    }

    @Test
    public void testWrongParameters4() {

        LocalizationKey tr = new LocalizationKey("testSimpleParam.key").setDefault("but there are no parameters", sysEN.escapeHtml());

        assertEquals("but there are no parameters", tr.get(sysEN, formatsEN, of("param1", "AAA"), of("param2", "BBB")));
    }

    @Test
    public void testWrongParameters5() {

        LocalizationKey tr = new LocalizationKey("testSimpleParam.key").setDefault("default {{param1 message param2 XYZ",
                sysEN.escapeHtml());

        assertEquals("default {{param1 message param2 XYZ", tr.get(sysEN, formatsEN, of("param1", "AAA"), of("param2", "BBB")));
    }

    @Test
    public void testFormatNumber() {

        LocalizationKey tr = new LocalizationKey("testSimpleParam.key").setDefault("default {{param1,number}}", sysEN.escapeHtml());

        assertEquals("default AAA", tr.get(sysEN, formatsEN, of("param1", "AAA")));
    }

    @Test
    public void testWrongFormatNumber() {

        LocalizationKey tr = new LocalizationKey("testSimpleParam.key").setDefault("default {{param1,number}}", sysEN.escapeHtml());

        Exception exception = assertThrows(LocalizationFormatException.class, () -> tr.get(sysEN, formatsEN, of("param1", sysEN)));

        String expectedMessage =
                "Locale: en-US, format: \"number\", Unknown class for number formatting: net.cactusthorn.localization.core.Sys";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testWrongCount() {

        LocalizationKey tr = new LocalizationKey("testSimple.key").setDefault("default message two", sysEN.escapeHtml()).addPlural(0,
                "single", sysEN.escapeHtml());

        Exception exception = assertThrows(LocalizationException.class, () -> tr.get(sysEN, formatsEN, of(COUNT, "XXXX")));

        String expectedMessage = "Locale: en-US, wrong value \"XXXX\" of {{count}} parameter for the key: testSimple.key";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}
