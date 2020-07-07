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

import org.junit.jupiter.api.Test;

import net.cactusthorn.localization.LocalizationException;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Locale;

public class SysTest {

    @Test
    public void testPluralExpressionEN() {

        Sys sys = new Sys("id", Locale.ENGLISH, true);

        assertEquals(1, sys.evalPlural(0));
        assertEquals(0, sys.evalPlural(1));
        assertEquals(1, sys.evalPlural(22));
    }

    @Test
    public void testPluralExpressionRU() {

        Sys sys = new Sys("id", new Locale("ru", "ru"), true);

        assertEquals(2, sys.evalPlural(0));
        assertEquals(0, sys.evalPlural(1));
        assertEquals(2, sys.evalPlural(100));
        assertEquals(1, sys.evalPlural(3));
    }

    @Test
    public void testNullLocale() {

        Exception exception = assertThrows(LocalizationException.class, () -> new Sys("id", null, true));
        assertEquals("_system.languageTag is required", exception.getMessage());
    }

    @Test
    public void testNullLocaleByProperties() {

        Exception exception = assertThrows(LocalizationException.class, () -> new Sys(Collections.emptyMap()));
        assertEquals("_system.languageTag is required", exception.getMessage());

    }

    @Test
    public void testCombineWith() {

        Sys sysOne = new Sys(null, Locale.ENGLISH, true);
        Sys sysTwo = new Sys("id", Locale.ENGLISH, null);

        sysOne.combineWith(sysTwo);

        assertEquals("Sys(id=id, locale=en, escapeHtml=true)", sysOne.toString());
        assertEquals(1, sysOne.evalPlural(22));
    }

}
