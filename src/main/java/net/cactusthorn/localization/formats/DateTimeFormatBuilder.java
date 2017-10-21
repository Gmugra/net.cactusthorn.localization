package net.cactusthorn.localization.formats;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DateTimeFormatBuilder {

	private static DateTimeFormatter createByPattern(Locale locale, FormatProperties formatProperties ) {
		
		if (formatProperties != null && formatProperties.pattern != null) {
			return DateTimeFormatter.ofPattern(formatProperties.pattern, locale);
		}
		return null;
	}
	
	public static DateTimeFormatter buildDate(Locale locale, FormatProperties formatProperties ) {
		
		DateTimeFormatter dtf = createByPattern(locale, formatProperties);
		if (dtf == null) {
			dtf =  DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
		}
		return dtf;
	}
	
	public static DateTimeFormatter buildDateTime(Locale locale, FormatProperties formatProperties ) {
		
		DateTimeFormatter dtf = createByPattern(locale, formatProperties);
		if (dtf == null) {
			dtf =  DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(locale);
		}
		return dtf;
	}
	
	public static DateTimeFormatter buildTime(Locale locale, FormatProperties formatProperties ) {
		
		DateTimeFormatter dtf = createByPattern(locale, formatProperties);
		if (dtf == null) {
			dtf =  DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(locale);
		}
		return dtf;
	}
}
