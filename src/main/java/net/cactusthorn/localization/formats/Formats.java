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

import static net.cactusthorn.localization.formats.FormatType.*;

import java.text.Format;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lombok.ToString;
import net.cactusthorn.localization.LocalizationFormatException;

@ToString
public class Formats {
	
	private Locale locale;
	
	private Map<String, Format> numberFormats = new HashMap<>();
	
	private Map<String, DateTimeFormatter> dateTimeFormats = new HashMap<>();
	
	public Formats(Locale locale, Map<String,String> properties ) {
	
		this.locale = (Locale)locale.clone();
		
		for (FormatProperties formatProperties : parse(properties ).values() ) {
			
			if (formatProperties.type == null ) {
				continue;
			}
			
			switch (formatProperties.type) {
				case NUMBER: numberFormats.put(formatProperties.name, NumberFormatBuilder.buildNumber(locale, formatProperties)); break;
				case CURRENCY: numberFormats.put(formatProperties.name, NumberFormatBuilder.buildCurrency(locale, formatProperties)); break;
				case INTEGER: numberFormats.put(formatProperties.name, NumberFormatBuilder.buildInteger(locale, formatProperties)); break;
				case PERCENT: numberFormats.put(formatProperties.name, NumberFormatBuilder.buildPercent(locale, formatProperties)); break;
				case DATETIME: dateTimeFormats.put(formatProperties.name, DateTimeFormatBuilder.buildDateTime(locale, formatProperties)); break;
				case DATE: dateTimeFormats.put(formatProperties.name, DateTimeFormatBuilder.buildDate(locale, formatProperties)); break;
				case TIME: dateTimeFormats.put(formatProperties.name, DateTimeFormatBuilder.buildTime(locale, formatProperties)); break;
			}	
			
		}
		
		//add default formats
		if (!numberFormats.containsKey(NUMBER.toString() ) ) {
			numberFormats.put(NUMBER.toString(), NumberFormatBuilder.buildNumber(locale, null)); 
		}
		if (!numberFormats.containsKey(CURRENCY.toString() ) ) {
			numberFormats.put(CURRENCY.toString(), NumberFormatBuilder.buildCurrency(locale, null)); 
		}
		if (!numberFormats.containsKey(INTEGER.toString() ) ) {
			numberFormats.put(INTEGER.toString(), NumberFormatBuilder.buildInteger(locale, null)); 
		}
		if (!numberFormats.containsKey(PERCENT.toString() ) ) {
			numberFormats.put(PERCENT.toString(), NumberFormatBuilder.buildPercent(locale, null)); 
		}
		if (!dateTimeFormats.containsKey(DATETIME.toString() ) ) {
			dateTimeFormats.put(DATETIME.toString(), DateTimeFormatBuilder.buildDateTime(locale, null)); 
		}
		if (!dateTimeFormats.containsKey(DATE.toString() ) ) {
			dateTimeFormats.put(DATE.toString(), DateTimeFormatBuilder.buildDate(locale, null)); 
		}
		if (!dateTimeFormats.containsKey(TIME.toString() ) ) {
			dateTimeFormats.put(TIME.toString(), DateTimeFormatBuilder.buildTime(locale, null)); 
		}
	}
	
	public void combineWith(Formats formats ) {
		
		if (!this.locale.equals(formats.locale) ) return;
		
		numberFormats.putAll(formats.numberFormats);
		dateTimeFormats.putAll(formats.dateTimeFormats);
	}
	
	public String format(String formatName, Object obj) throws LocalizationFormatException {
		
		if (obj == null ) {
			return "null";
		}
		
		if (obj instanceof String || obj instanceof StringBuilder || obj instanceof StringBuffer ) {
			return obj.toString();
		}
		
		if (dateTimeFormats.containsKey(formatName) ) {
		
			if (obj instanceof ZonedDateTime ) {
				return formatDateTime(formatName, (ZonedDateTime)obj);
			}
			if (obj instanceof LocalDateTime ) {
				return formatDateTime(formatName, ((LocalDateTime)obj).atZone(ZoneId.systemDefault() ) );
			}
			if (obj instanceof LocalDate ) {
				return formatDateTime(formatName, ((LocalDate)obj).atStartOfDay(ZoneId.systemDefault() ) );
			}
			if (obj instanceof LocalTime ) {
				return formatDateTime(formatName, ((LocalTime)obj).atDate(LocalDate.now()).atZone(ZoneId.systemDefault() ) );
			}
			if (obj instanceof java.util.Date ) {
				ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(((java.util.Date)obj).toInstant(), ZoneId.systemDefault());
				return formatDateTime(formatName, zonedDateTime);
			}
			if (obj instanceof java.util.Calendar ) {
				java.util.Calendar calendar = (java.util.Calendar) obj;  	
				ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
				return formatDateTime(formatName, zonedDateTime);
			}
			
			throw new LocalizationFormatException(locale, "format: \"" + formatName + "\", Unknown class for date/time formatting: " + obj.getClass().getName() );
		}
		
		if (numberFormats.containsKey(formatName) ) {
			
			//to be thread safe we need to create new format object every time,
			//but clone() is more simple to use and also, in this case, working faster (based on JMH tests with JVM 8)
			Format format = (Format)numberFormats.get(formatName).clone();
			try {
				return format.format(obj);
			} catch (IllegalArgumentException iae) {
				
				throw new LocalizationFormatException(locale, "format: \"" + formatName + "\", Unknown class for number formatting: " + obj.getClass().getName(), iae );
			}
		}
		
		throw new LocalizationFormatException(locale, "Unknown format: \"" + formatName + "\"" );
	}
	
	private String formatDateTime(String formatName, ZonedDateTime zonedDateTime) {
		DateTimeFormatter format = dateTimeFormats.get(formatName);
		return format.format(zonedDateTime );	
	}
	
	public static final String FORMAT_PREFIX = "_format.";
	
	private Map<String, FormatProperties> parse(Map<String,String> properties ) {
		
		Map<String, FormatProperties> fp = new HashMap<>();
		
		for (Map.Entry<String, String> e : properties.entrySet() ) {	
			
			if (e.getKey().indexOf(FORMAT_PREFIX) != 0 ) {
				continue;
			}
			
			String subname = e.getKey().substring(FORMAT_PREFIX.length());
			
			int dotIndex = subname.indexOf('.');  
			if (dotIndex == -1 ) {
				continue;
			}
			
			String formatName = subname.substring(0, dotIndex);
			
			if (!fp.containsKey(formatName ) ) {
				fp.put(formatName, new FormatProperties(formatName, locale) );
			}

			fp.get(formatName).set(formatName, subname.substring(dotIndex+1), e.getValue() );
		}
		
		return fp;
	}
	
}
