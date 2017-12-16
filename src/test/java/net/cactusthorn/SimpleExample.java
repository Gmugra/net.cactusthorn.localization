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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

import net.cactusthorn.localization.Localization;
import net.cactusthorn.localization.PathLocalizationLoader;

public class SimpleExample {

	static Locale en_US = Locale.forLanguageTag("en-US");
	
	public static void main(String... args) {
		
		try {

			String systemId = args[0];
			String l10nDirectory = args[1];
			
			Localization localization = new PathLocalizationLoader(systemId ).from(l10nDirectory ).load();
			
			System.out.println(localization.get(en_US, "super.key"));

		} catch (URISyntaxException | IOException e ) {
			e.printStackTrace();
		}
	}
}
