package net.cactusthorn.localization.formats;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FormatProperties {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FormatProperties.class);
	
	String name;
	FormatType type;
	String pattern;
	
	char groupingSeparator;
	char decimalSeparator;
	
	char monetaryDecimalSeparator;
	String currencySymbol;
	
	char percent;
	
	FormatProperties(String name ) {
		this.name = name;
	}
	
	void set(final String formatName, final  String property, final  String value, final Locale locale ) {
		
		switch (property ) {
		case "type":
			try {
				type = FormatType.valueOf(value.toUpperCase()); break;
			} catch (IllegalArgumentException iae) {
				LOGGER.error("Locale: \"{}\", format: \"{}\" -> unknown type: \"{}\"", locale.toLanguageTag(), formatName, value);
			}
		case "pattern": pattern = value; break;
		case "currencySymbol": currencySymbol = value; break;
		case "groupingSeparator": 
			if (value.length() != 1 ) {
				LOGGER.error("Locale: \"{}\", format: \"{}\" -> wrong groupingSeparator type: \"{}\"", locale.toLanguageTag(), formatName, value);
			} else {
				groupingSeparator = value.charAt(0); 
			}
			break;
		case "decimalSeparator": 
			if (value.length() != 1 ) {
				LOGGER.error("Locale: \"{}\", format: \"{}\" -> wrong decimalSeparator type: \"{}\"", locale.toLanguageTag(), formatName, value);
			} else {
				decimalSeparator = value.charAt(0); 
			}
			break;
		case "monetaryDecimalSeparator": 
			if (value.length() != 1 ) {
				LOGGER.error("Locale: \"{}\", format: \"{}\" -> wrong monetaryDecimalSeparator type: \"{}\"", locale.toLanguageTag(), formatName, value);
			} else {
				monetaryDecimalSeparator = value.charAt(0); 
			}
			break;
		case "percent": 
			if (value.length() != 1 ) {
				LOGGER.error("Locale: \"{}\", format: \"{}\" -> wrong percent type: \"{}\"", locale.toLanguageTag(), formatName, value);
			} else {
				percent = value.charAt(0); 
			}
			break;
		}
	}
	
}
