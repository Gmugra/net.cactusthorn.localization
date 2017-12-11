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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.cactusthorn.localization.LocalizationException;
import net.cactusthorn.localization.core.Sys;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Locale;

import javax.script.ScriptException;

public class SysTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testPluralExpressionEN() throws ScriptException  {
		
		Sys sys = new Sys("id", Locale.ENGLISH, 2, "(count!=1);", true);
		
		assertEquals(1, sys.evalPlural(0));
		assertEquals(0, sys.evalPlural(1));
		assertEquals(1, sys.evalPlural(22));
	}
	
	@Test
	public void testPluralExpressionRU() throws ScriptException  {
		
		Sys sys = new Sys("id", new Locale("ru","ru"), 3, 
				"(count%10==1 && count%100!=11 ? 0 : count%10>=2 && count%10<=4 && (count%100<10 || count%100>=20) ? 1 : 2);", true);
		
		assertEquals(2, sys.evalPlural(0));
		assertEquals(0, sys.evalPlural(1));
		assertEquals(2, sys.evalPlural(100));
		assertEquals(1, sys.evalPlural(3));
	}
	
	@Test
	public void testNullLocale() throws ScriptException  {
		
		expectedException.expect(LocalizationException.class);
		expectedException.expectMessage(is("_system.languageTag is required"));
		
		new Sys("id", null, 2, "(count!=1);", true);
	}
	
	@Test
	public void testNullLocaleByProperties() throws ScriptException  {
		
		expectedException.expect(LocalizationException.class);
		expectedException.expectMessage(is("_system.languageTag is required"));
		
		new Sys(Collections.emptyMap() );
	}

	@Test
	public void testCombineWith() throws ScriptException  {
		
		Sys sysOne = new Sys(null, Locale.ENGLISH, 2, "(count!=1);", true);
		Sys sysTwo = new Sys("id", Locale.ENGLISH, null, null, null);
		
		sysOne.combineWith(sysTwo);
		
		assertEquals("Sys(id=id, locale=en, nplurals=2, pluralExpression=(count!=1);, escapeHtml=true)", sysOne.toString());
		assertEquals(1, sysOne.evalPlural(22));
	}
	
}
