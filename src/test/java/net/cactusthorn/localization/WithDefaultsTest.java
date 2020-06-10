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
import java.util.Map;

import static net.cactusthorn.localization.Parameter.*;

public class WithDefaultsTest {

	static Localization localization;
	static Locale en_US = Locale.forLanguageTag("en-US");
	static java.util.Date date = new java.util.Date(1508570828338L);

	@BeforeAll
	public static void load() throws URISyntaxException, IOException {

		localization = new PathLocalizationLoader("test-app").from("L10nWithDefault").load();
	}

	@Test
	public void testFromDefault() {

		assertEquals("Super <br/> value", localization.get(en_US, "super.htmlkey"));
	}

	@Test
	public void testFromLocale() {

		Map<String, Object> params = asMap(of("first", "AAA"), of("second", "BBB"));

		assertEquals("FIRST: AAA, second:BBB<br/>", localization.get(en_US, "test.param.first", params));
	}

	@Test
	public void testFormatOverloading() {

		assertEquals("datetime=Saturday, October 21, 2017 9:27 AM", localization.get(en_US, "datetime_text_one", of("dt", date)));
		assertEquals("datetime=2017-10-21T09", localization.get(en_US, "datetime_text_two", of("dt", date)));
	}
}
