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

import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.BeforeClass;
import org.junit.Rule;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Locale;

import static net.cactusthorn.localization.Parameter.*;

public class LoggingLocalizationTest {

	static Localization localization;
	
	static Locale en = Locale.forLanguageTag("en"); //also locale fallback will work
	static Locale fr_FR = Locale.forLanguageTag("fr-FR");
	
	@BeforeClass
	public static void loadL10n() throws IOException {
		
		localization = new LocalizationLoader("test-app").setClass(LoggingLocalization.class).load();
	}
	
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();
	
	@Test
	public void testUnavailableLocale() throws IOException {
		
		String text = localization.get(fr_FR, "x.y.z.apple", count(0) );
		
		assertThat(systemOutRule.getLog(), containsString("Locale: fr-FR, Unavailable locale"));
		
		assertEquals("Locale: fr-FR, Unavailable locale", text);
	}
	
	@Test
	public void testUnavailableKey() {
		
		String text = localization.get(en, "x.m.z.apple", count(0) );
		
		assertThat(systemOutRule.getLog(), containsString("Locale: en-US, unavailable key: x.m.z.apple"));
		
		assertEquals("Locale: en-US, unavailable key: x.m.z.apple", text);
	}
	
	@Test
	public void testWrongCount() {
		
		String text = localization.get(en, "x.y.z.apple", of(COUNT, "xxxx") );
		
		assertThat(systemOutRule.getLog(), containsString("Locale: en-US, wrong value \"xxxx\" of {{count}} parameter for the key: x.y.z.apple"));
		
		assertEquals("apples by default", text);
	}
	
	@Test
	public void testWrongFormatNumber() {
		
		String text = localization.format(en, "number", fr_FR );
		
		assertThat(systemOutRule.getLog(), containsString("Locale: en-US, format: \"number\", Unknown class for number formatting: java.util.Locale"));
		
		assertEquals("fr_FR", text);
	}
	
	@Test
	public void testNull() {
		
		String text = localization.format(en, "number", null );
		
		assertEquals("null", text);
	}
}
