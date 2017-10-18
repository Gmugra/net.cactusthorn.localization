package net.cactusthorn.localization.formats;

import static net.cactusthorn.localization.formats.FormatType.*;

import java.text.Format;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Formats {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Formats.class);
	
	private Locale locale;
	
	private Map<String, Format> formats = new HashMap<>();
	
	public Formats(Locale locale, Properties properties ) {
	
		this.locale = locale;
		
		for (FormatProperties formatProperties : parse(properties ).values() ) {
			
			if (formatProperties.type == null ) {
				continue;
			}
			
			switch (formatProperties.type) {
				case NUMBER: formats.put(formatProperties.name, NumberFormatBuilder.build(locale, formatProperties)); break;
				case CURRENCY: formats.put(formatProperties.name, CurrencyFormatBuilder.build(locale, formatProperties)); break;
				case INTEGER: formats.put(formatProperties.name, IntegerFormatBuilder.build(locale, formatProperties)); break;
				case PERCENT: formats.put(formatProperties.name, PercentFormatBuilder.build(locale, formatProperties)); break;
			}	
			//add default formats
			if (!formats.containsKey(NUMBER.toString().toLowerCase() ) ) {
				formats.put(NUMBER.toString().toLowerCase(), NumberFormatBuilder.build(locale)); 
			}
			if (!formats.containsKey(CURRENCY.toString().toLowerCase() ) ) {
				formats.put(CURRENCY.toString().toLowerCase(), CurrencyFormatBuilder.build(locale)); 
			}
			if (!formats.containsKey(INTEGER.toString().toLowerCase() ) ) {
				formats.put(INTEGER.toString().toLowerCase(), IntegerFormatBuilder.build(locale)); 
			}
			if (!formats.containsKey(PERCENT.toString().toLowerCase() ) ) {
				formats.put(PERCENT.toString().toLowerCase(), PercentFormatBuilder.build(locale)); 
			}
		}
	}
	
	public String format(String formatName, Object obj) {
		
		if (obj == null ) {
			return "null";
		}
		
		if (formats.containsKey(formatName) ) {
			
			Format format = (Format)formats.get(formatName).clone();
			try {
				return format.format(obj);
			} catch (IllegalArgumentException iae) {
				LOGGER.error("Locale: \"{}\", format: \"{}\", Object: \"{}\"", locale.toLanguageTag(), formatName, obj, iae);
				return obj.toString();
			}
		}
		
		LOGGER.error("Locale: \"{}\", unknown format: \"{}\"", locale.toLanguageTag(), formatName);
		
		return obj.toString();
	}
	
	private static final String FORMAT_PREFIX = "_format.";
	
	private Map<String, FormatProperties> parse(Properties properties ) {
		
		Map<String, FormatProperties> fp = new HashMap<>();
		
		for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
			
			String name = (String)e.nextElement();
			
			if (name.indexOf(FORMAT_PREFIX) != 0 ) {
				continue;
			}
			
			String subname = name.substring(FORMAT_PREFIX.length());
			
			int dotIndex = subname.indexOf('.');  
			if (dotIndex == -1 ) {
				continue;
			}
			
			String formatName = subname.substring(0, dotIndex);
			
			if (!fp.containsKey(formatName ) ) {
				fp.put(formatName, new FormatProperties(formatName) );
			}

			fp.get(formatName).set(formatName, subname.substring(dotIndex+1), properties.getProperty(name), locale );
		}
		
		return fp;
	}
	
}
