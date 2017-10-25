package net.cactusthorn.localization.formats;

import java.time.format.FormatStyle;
import java.util.Locale;

import net.cactusthorn.localization.LocalizationException;

class FormatProperties {
	
	String name;
	FormatType type;
	String pattern;
	
	char groupingSeparator;
	char decimalSeparator;
	
	char monetaryDecimalSeparator;
	String currencySymbol;
	
	char percentSymbol;
	
	FormatStyle dateStyle;
	FormatStyle timeStyle;
	
	FormatProperties(String name ) {
		this.name = name;
	}
	
	void set(final String formatName, final  String property, final  String value, final Locale locale ) {
		
		switch (property ) {
		case "type":
			try {
				type = FormatType.valueOf(value.toUpperCase()); break;
			} catch (IllegalArgumentException iae) {
				throw localizationException(locale, formatName, "unknown type", value, iae);
			}
		case "pattern": pattern = value; break;
		case "currencySymbol": currencySymbol = value; break;
		case "groupingSeparator": 
			if (value.length() != 1 ) {
				throw localizationException(locale, formatName, "wrong groupingSeparator", value, null);
			} else {
				groupingSeparator = value.charAt(0); 
			}
			break;
		case "decimalSeparator": 
			if (value.length() != 1 ) {
				throw localizationException(locale, formatName, "wrong decimalSeparator", value, null);
			} else {
				decimalSeparator = value.charAt(0); 
			}
			break;
		case "monetaryDecimalSeparator": 
			if (value.length() != 1 ) {
				throw localizationException(locale, formatName, "wrong monetaryDecimalSeparator", value, null);
			} else {
				monetaryDecimalSeparator = value.charAt(0); 
			}
			break;
		case "percentSymbol": 
			if (value.length() != 1 ) {
				throw localizationException(locale, formatName, "wrong percentSymbol", value, null);
			} else {
				percentSymbol = value.charAt(0); 
			}
			break;
		case "dateStyle": 
			try {
				dateStyle = FormatStyle.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException iae ) {
				throw localizationException(locale, formatName, "wrong dateStyle", value, iae);
			}
			break;
		case "timeStyle": 
			try {
				timeStyle = FormatStyle.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException iae ) {
				throw localizationException(locale, formatName, "wrong timeStyle", value, iae);
				
			}
			break;
		}
	}
	
	private LocalizationException localizationException(Locale locale, String format, String value, String message, Exception e) {
		
		return new LocalizationException(locale, "format: " + format + " -> " + message + ": \"" + value + "\"", e);
	}
}
