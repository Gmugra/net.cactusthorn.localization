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
package net.cactusthorn.localization.formats;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.cactusthorn.localization.LocalizationException;
import net.cactusthorn.localization.formats.Formats;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

public class FormatsTest {
	
	static Locale enUSLocale = new Locale("en","US");
	
	static Formats formatsEN;
	static LocalDateTime localDateTimeEN;
	static ZonedDateTime zonedDateTimeEN;
	
	static java.util.Date date = new java.util.Date(1508570828338L);
	
	static Locale ruRUlocale = new Locale("ru","RU");
	static Formats formatsRU;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@BeforeClass
	public static void setupEN() throws URISyntaxException, IOException {
		
		Properties props = load("L10n/en-US.properties");
		
		formatsEN = new Formats(enUSLocale, (Map)props);
		
		{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", enUSLocale);
			localDateTimeEN = LocalDateTime.parse("2017-09-17T11:16:50", formatter);
		}
		
		{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", enUSLocale);
			zonedDateTimeEN = ZonedDateTime.parse("2017-09-17T11:16:50+01:00", formatter);
		}
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@BeforeClass
	public static void setupRU() throws URISyntaxException, IOException {
		
		Properties props = load("L10n/ru-RU.properties");
	
		formatsRU = new Formats(ruRUlocale, (Map)props );
	}

	private static Properties load(String resourceName) throws URISyntaxException, IOException {
		Properties properties = new Properties();
		Path path = Paths.get(FormatsTest.class.getClassLoader().getResource(resourceName).toURI());
		try (BufferedReader buf = Files.newBufferedReader(path, UTF_8 ) ) {
			properties.load(buf);
		}
		return properties;
	}
	
	@Test
	public void testFormats() {
		
		assertEquals("2,000|2", formatsEN.format("special", 2000.20 ));
		assertEquals("$$2;000*20", formatsEN.format("curr", 2000.20 ));
		assertEquals("200", formatsEN.format("integer", 200.2 ));
		assertEquals("2,000.22", formatsEN.format("number", 2000.22f ));
		assertEquals("113,120&", formatsEN.format("percent", 1131.20 ));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCombineWith() throws URISyntaxException, IOException {
		
		Properties props = load("CombineWith/en-US.properties");
		
		Formats formats = new Formats(enUSLocale, (Map)props);
		
		formats.combineWith(formatsEN);
		
		assertEquals("11:16:50 AM",formats.format("time", zonedDateTimeEN));
		assertEquals("9/17/17 11:16 AM",formats.format("combineDate", zonedDateTimeEN));
		
		assertEquals("9876.0", formats.format("np1", 9876));
		assertEquals("9876#5", formats.format("combineNumber", 9876.45));
	}
	
	@Test
	public void testWrongClass() {
		
		expectedException.expect(LocalizationException.class);
		expectedException.expectMessage("Locale: en-US, format: \"special\", Unknown class for number formatting: java.util.Locale");
		
		formatsEN.format("special", enUSLocale);
	}
	

	@Test
	public void testStringAndNull() {
		
		assertEquals("rfrfrfrf", formatsEN.format("special", "rfrfrfrf"));
		assertEquals("null", formatsEN.format("special", null));
	}
	
	@Test
	public void testPattern() {
		
		assertEquals("9876.0", formatsEN.format("np1", 9876));
	}

	@Test
	public void testLocalDateTime() {
		
		assertEquals("Sep 17, 2017 11:16:50 AM",formatsEN.format("datetime", localDateTimeEN));
		assertEquals("Sep 17, 2017",formatsEN.format("date", localDateTimeEN));
		assertEquals("11:16:50 AM",formatsEN.format("time", localDateTimeEN));
	}
	
	@Test
	public void testZonedDateTime() {
		
		assertEquals("Sep 17, 2017 11:16:50 AM",formatsEN.format("datetime", zonedDateTimeEN));
		assertEquals("Sep 17, 2017",formatsEN.format("date", zonedDateTimeEN));
		assertEquals("11:16:50 AM",formatsEN.format("time", zonedDateTimeEN));
	}
	
	@Test
	public void testUtilDate() {
		
		assertEquals("Oct 21, 2017 9:27:08 AM",formatsEN.format("datetime", date));	
	}
	
	@Test
	public void testCalendar() {
		
		java.util.Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
		
		assertEquals("Oct 21, 2017 6:27:08 PM",formatsEN.format("datetime", calendar));
	}
	
	@Test
	public void testStyleFormat() {
		
		java.util.Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
		
		assertEquals("Saturday, October 21, 2017 6:27 PM",formatsEN.format("dt1", calendar));
	}

	@Test
	public void testPatternFormat() {
		
		java.util.Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
		
		assertEquals("2017-10-21T18:27:08+11:00",formatsEN.format("dt2", calendar));
	}
	
	@Test
	public void testLocalDateTimePattern() {
		
		assertEquals("2017-09-17T11:16:50+02:00",formatsEN.format("dt2", localDateTimeEN));
	}
	
	@Test
	public void testLocalDate() {
		
		assertEquals("2017-09-17T00:00:00+02:00",formatsEN.format("dt2", localDateTimeEN.toLocalDate()));
	}
	
	@Test
	public void testWrongDateTimeObject() {
		
		expectedException.expect(LocalizationException.class);
		expectedException.expectMessage("Locale: en-US, format: \"dt2\", Unknown class for date/time formatting: java.lang.Long");
		
		formatsEN.format("dt2", 22L);
	}
	
	@Test
	public void testUnknownFormat() {
		
		expectedException.expect(LocalizationException.class);
		expectedException.expectMessage("Locale: en-US, Unknown format: \"something\"");
		
		formatsEN.format("something", 22L);
	}
	
	@Test
	public void testTime() {
		
		assertEquals("11:16:50 AM",formatsEN.format("time", localDateTimeEN));
		assertEquals("11:16:50 AM",formatsEN.format("time", localDateTimeEN.toLocalTime()));
	}
	
	@Test
	public void testGroupingUsed() {
		
		assertEquals("2 000,22", formatsRU.format("number", 2000.22f ));
		assertEquals("2000,22", formatsRU.format("numb", 2000.22f ));
	}
}
