package net.cactusthorn.localization.formats;

import static net.cactusthorn.localization.formats.FormatType.*;

import java.text.Format;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.cactusthorn.localization.Sys;

@ToString
@Slf4j
public class Formats {
	
	private Locale locale;
	
	private Map<String, Format> numberFormats = new HashMap<>();
	
	private Map<String, DateTimeFormatter> dateTimeFormats = new HashMap<>();
	
	public Formats(Sys sys, Properties properties ) {
	
		this.locale = sys.getLocale();
		
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
		if (!numberFormats.containsKey(NUMBER.toString().toLowerCase() ) ) {
			numberFormats.put(NUMBER.toString().toLowerCase(), NumberFormatBuilder.buildNumber(locale, null)); 
		}
		if (!numberFormats.containsKey(CURRENCY.toString().toLowerCase() ) ) {
			numberFormats.put(CURRENCY.toString().toLowerCase(), NumberFormatBuilder.buildCurrency(locale, null)); 
		}
		if (!numberFormats.containsKey(INTEGER.toString().toLowerCase() ) ) {
			numberFormats.put(INTEGER.toString().toLowerCase(), NumberFormatBuilder.buildInteger(locale, null)); 
		}
		if (!numberFormats.containsKey(PERCENT.toString().toLowerCase() ) ) {
			numberFormats.put(PERCENT.toString().toLowerCase(), NumberFormatBuilder.buildPercent(locale, null)); 
		}
		if (!dateTimeFormats.containsKey(DATETIME.toString().toLowerCase() ) ) {
			dateTimeFormats.put(DATETIME.toString().toLowerCase(), DateTimeFormatBuilder.buildDateTime(locale, null)); 
		}
		if (!dateTimeFormats.containsKey(DATE.toString().toLowerCase() ) ) {
			dateTimeFormats.put(DATE.toString().toLowerCase(), DateTimeFormatBuilder.buildDate(locale, null)); 
		}
		if (!dateTimeFormats.containsKey(TIME.toString().toLowerCase() ) ) {
			dateTimeFormats.put(TIME.toString().toLowerCase(), DateTimeFormatBuilder.buildTime(locale, null)); 
		}
	}
	
	public String format(String formatName, Object obj) {
		
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
			log.error("Locale: \"{}\", unknown class for date time formatting : \"{}\"", locale.toLanguageTag(), obj.getClass().getName());
			return obj.toString();
		}
		
		if (numberFormats.containsKey(formatName) ) {
			
			//to be thread safe we need to create new format object every time,
			//but clone() is more simple to use and also, in this case, working faster (based on JMH tests)
			Format format = (Format)numberFormats.get(formatName).clone();
			try {
				return format.format(obj);
			} catch (IllegalArgumentException iae) {
				log.error("Locale: \"{}\", format: \"{}\", Object of class: {}", locale.toLanguageTag(), formatName, obj.getClass().getName(), iae);
				return obj.toString();
			}
		}
		
		log.error("Locale: \"{}\", unknown format: \"{}\"", locale.toLanguageTag(), formatName);
		
		return obj.toString();
	}
	
	private String formatDateTime(String formatName, ZonedDateTime zonedDateTime) {
		DateTimeFormatter format = dateTimeFormats.get(formatName);
		return format.format(zonedDateTime );	
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
				fp.put(formatName, new FormatProperties(formatName, locale) );
			}

			fp.get(formatName).set(formatName, subname.substring(dotIndex+1), properties.getProperty(name) );
		}
		
		return fp;
	}
	
}
