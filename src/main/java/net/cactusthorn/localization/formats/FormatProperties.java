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

import java.text.NumberFormat;
import java.time.format.FormatStyle;
import java.util.Locale;

import net.cactusthorn.localization.LocalizationException;

class FormatProperties {

	Locale locale;

	String name;
	FormatType type;
	String pattern;

	char groupingSeparator;
	char decimalSeparator;
	boolean groupingUsed;

	char monetaryDecimalSeparator;
	String currencySymbol;

	char percentSymbol;

	FormatStyle dateStyle;
	FormatStyle timeStyle;

	FormatProperties(String name, Locale locale) {
		this.name = name;
		this.locale = locale;
		groupingUsed = NumberFormat.getNumberInstance(locale).isGroupingUsed();
	}

	void set(final String formatName, FormatProperty property, String value) {

		switch (property) {
		case TYPE:
			try {
				type = FormatType.valueOf(value.toUpperCase(Locale.ENGLISH));
				break;
			} catch (IllegalArgumentException iae) {
				throw localizationException(locale, formatName, "unknown type", value, iae);
			}
		case PATTERN:
			pattern = value;
			break;
		case CURRENCY_SYMBOL:
			currencySymbol = value;
			break;
		case GROUPING_USED:
			groupingUsed = Boolean.valueOf(value);
			break;
		case GROUPING_SEPARATOR:
			if (value.length() != 1) {
				throw localizationException(locale, formatName, "wrong groupingSeparator", value, null);
			}
			groupingSeparator = value.charAt(0);
			break;
		case DECIMAL_SEPARATOR:
			if (value.length() != 1) {
				throw localizationException(locale, formatName, "wrong decimalSeparator", value, null);
			}
			decimalSeparator = value.charAt(0);
			break;
		case MONETARY_DECIMAL_SEPARATOR:
			if (value.length() != 1) {
				throw localizationException(locale, formatName, "wrong monetaryDecimalSeparator", value, null);
			}
			monetaryDecimalSeparator = value.charAt(0);
			break;
		case PERCENT_SYMBOL:
			if (value.length() != 1) {
				throw localizationException(locale, formatName, "wrong percentSymbol", value, null);
			}
			percentSymbol = value.charAt(0);
			break;
		case DATE_STYLE:
			try {
				dateStyle = FormatStyle.valueOf(value.toUpperCase(Locale.ENGLISH));
				break;
			} catch (IllegalArgumentException iae) {
				throw localizationException(locale, formatName, "wrong dateStyle", value, iae);
			}
		case TIME_STYLE:
			try {
				timeStyle = FormatStyle.valueOf(value.toUpperCase(Locale.ENGLISH));
				break;
			} catch (IllegalArgumentException iae) {
				throw localizationException(locale, formatName, "wrong timeStyle", value, iae);
			}
		}
	}

	private LocalizationException localizationException(Locale locale, String format, String value, String message, Exception e) {

		return new LocalizationException(locale, "format: " + format + " -> " + message + ": \"" + value + "\"", e);
	}
}
