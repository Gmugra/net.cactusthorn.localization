package net.cactusthorn.localization;

import java.util.Locale;
import java.util.Map;

public interface Localization {

	String get(Locale locale, String key);
	
	String get(Locale locale, String key, Parameter<?>... parameters);
	
	String get(Locale locale, String key, boolean withFormatting, Parameter<?>... parameters );
	
	String get(Locale locale, String key, Map<String, ?> parameters);
	
	String get(Locale locale, String key, boolean withFormatting, Map<String, ?> parameters);
	
	String getDefault(Locale locale, String key);
	
	String format(Locale locale, String formatName, Object obj);
}
