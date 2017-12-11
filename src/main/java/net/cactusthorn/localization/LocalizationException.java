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

import java.util.Locale;

public class LocalizationException extends RuntimeException {
	
	private static final long serialVersionUID = 0L;

	public LocalizationException(Locale locale, String message, Throwable e) {
		super((locale != null ? "Locale: " + locale.toLanguageTag() + ", " : "" ) + message, e );
	}
	
	public LocalizationException(Locale locale, String message) {
		this(locale, message, null);
	}
	
	public LocalizationException(String message) {
		this(null, message, null);
	}
	
	public LocalizationException(String message, Throwable e) {
		this(null, message, e);
	}

}
