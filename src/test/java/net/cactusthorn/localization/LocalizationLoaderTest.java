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

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LocalizationLoaderTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void testNotDirectory() throws URISyntaxException, IOException {
		
		expectedException.expect(IOException.class);
		expectedException.expectMessage(containsString("is not directory"));

		new PathLocalizationLoader("test-app").from("L10n/ru-RU.properties").load();
	}
	
	@Test
	public void testWrongSystemId() throws URISyntaxException, IOException {
		
		expectedException.expect(IOException.class);
		expectedException.expectMessage("Something wrong with file \"fr-CA.properties\"");
		expectedException.expectCause(
			allOf(
				isA(LocalizationException.class),
				hasProperty("message", is("Wrong _system.id=test-app, expected: _system.id=my-super-app"))
			)
		);
		
		new PathLocalizationLoader("my-super-app").from("WrongSystemId").instanceOf(LoggingLocalization.class).load();
	}
	
	@Test
	public void testWrongLanguageTag() throws URISyntaxException, IOException {
		
		expectedException.expect(IOException.class);
		expectedException.expectMessage("Something wrong with file \"fr-CA.properties\"");
		expectedException.expectCause(
			allOf(
				isA(LocalizationException.class),
				hasProperty("message", is("Wrong value of _system.languageTag=en-US, expected: _system.languageTag=fr-CA"))
			)
		);
		
		new PathLocalizationLoader("test-app").from("WrongLanguageTag").load();
	}
}
