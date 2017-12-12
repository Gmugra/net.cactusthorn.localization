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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class L10nTest {
	
	static Locale en_US = new Locale("en","US");
	
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().muteForSuccessfulTests();
	
	@BeforeClass
	public static void before() throws URISyntaxException, IOException, InterruptedException {
		
		Path l10nDirectory = Paths.get(L10nTest.class.getClassLoader().getResource("L10n").toURI());
		
		//The file-watching-thread from WatchLoggingLocalization will work just fine. 
		//Note: the test can finish too fast and JUnit kill that all, so you will see nothing in log
		//However, to see that is possible and working - uncomment next line
		//L10n.theOnlyAttemptToInitInstance("test-app", l10nDirectory, UTF_8, WatchLoggingLocalization.class);
	
		L10n.theOnlyAttemptToInitInstance("test-app", l10nDirectory, UTF_8, LoggingLocalization.class);
	}

	@Test
	public void testHolder() {
		
		assertEquals("Super value", L10n.instance().get(en_US, "super.key"));
	}
}
