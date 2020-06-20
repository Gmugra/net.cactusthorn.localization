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
	
	private static final String ENGLISH_EXPRESION = "${count!=1?1:0}";

	@Test
	public void testPluralExpressionEN() {

		Sys sys = new Sys("id", Locale.ENGLISH, 2, ENGLISH_EXPRESION, true);

		assertEquals(1, sys.evalPlural(0));
		assertEquals(0, sys.evalPlural(1));
		assertEquals(1, sys.evalPlural(22));
	}

	@Test
	public void testPluralExpressionRU() {

		Sys sys = new Sys("id", new Locale("ru", "ru"), 3,
				"${count%10==1 && count%100!=11 ? 0 : count%10>=2 && count%10<=4 && (count%100<10 || count%100>=20) ? 1 : 2}", true);

		assertEquals(2, sys.evalPlural(0));
		assertEquals(0, sys.evalPlural(1));
		assertEquals(2, sys.evalPlural(100));
		assertEquals(1, sys.evalPlural(3));
	}

	@Test
	public void testNullLocale() {

		Exception exception = assertThrows(LocalizationException.class, () -> new Sys("id", null, 2, ENGLISH_EXPRESION, true));
		assertEquals("_system.languageTag is required", exception.getMessage());
	}

	@Test
	public void testNullLocaleByProperties() {

		Exception exception = assertThrows(LocalizationException.class, () -> new Sys(Collections.emptyMap()));
		assertEquals("_system.languageTag is required", exception.getMessage());

	}

	@Test
	public void testCombineWith() {

		Sys sysOne = new Sys(null, Locale.ENGLISH, 2, ENGLISH_EXPRESION, true);
		Sys sysTwo = new Sys("id", Locale.ENGLISH, null, null, null);

		sysOne.combineWith(sysTwo);

		assertEquals("Sys(id=id, locale=en, nplurals=2, pluralExpression=${count!=1?1:0}, escapeHtml=true)", sysOne.toString());
		assertEquals(1, sysOne.evalPlural(22));
	}

}
