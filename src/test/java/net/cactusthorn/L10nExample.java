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
package net.cactusthorn;

import java.util.Locale;

import net.cactusthorn.localization.L10n;
import net.cactusthorn.localization.LoggingLocalization;

public class L10nExample {
	
	static Locale en_US = Locale.forLanguageTag("en-US");
	
	static { 
		L10n.theOnlyAttemptToInitInstance("test-app", "L10n", LoggingLocalization.class);
	}
	
	public static void main(String... args) {
		
		System.out.println(L10n.instance().get(en_US, "super.key"));
	}

}
