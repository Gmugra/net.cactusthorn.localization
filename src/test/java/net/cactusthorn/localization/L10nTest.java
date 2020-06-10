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

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class L10nTest {

	static Locale en_US = new Locale("en", "US");

	@BeforeAll
	public static void before() throws URISyntaxException, InterruptedException {

		// The file-watching-thread from WatchLoggingLocalization will work just fine.
		// Note: the test can finish too fast and JUnit kill that all, so you will see
		// nothing in log
		// However, to see that is possible and working - uncomment next line
		
		//L10n.theOnlyAttemptToInitInstance("test-app", "L10n", WatchLoggingLocalization.class);

		L10n.theOnlyAttemptToInitInstance("test-app", "L10n", LoggingLocalization.class);
	}

	@Test
	public void testHolder() {

		assertEquals("Super value", L10n.instance().get(en_US, "super.key"));
	}
}
