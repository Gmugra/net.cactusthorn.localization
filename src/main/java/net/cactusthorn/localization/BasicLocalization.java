package net.cactusthorn.localization;

import java.util.Locale;
import java.util.Map;

import net.cactusthorn.localization.core.LocalizationKeys;

public class BasicLocalization implements Localization{
	
	private Map<Locale, LocalizationKeys> translations;
	
	public BasicLocalization(Map<Locale, LocalizationKeys> translations) {
		this.translations = translations;
	}
	
	@Override
	public String get(Locale locale, String key) {
		return get(locale, key, true, (Map<String, ?>)null);
	}
	
	@Override
	public String get(Locale locale, String key, Parameter<?>... parameters) {
		return get(locale, key, true, Parameter.asMap(parameters));
	}
	
	@Override
	public String get(Locale locale, String key, boolean withFormatting, Parameter<?>... parameters ) {
		return get(locale, key, withFormatting, Parameter.asMap(parameters));
	}
	
	@Override
	public String get(Locale locale, String key, Map<String, ?> parameters) {
		return get(locale, key, true, parameters);
	}
	
	@Override
	public String get(Locale locale, String key, boolean withFormatting, Map<String, ?> parameters) {
	
		if (!translations.containsKey(locale)) {
			
			throw new LocalizationLocaleException(locale, "Unavailable locale");
		}
			
		return translations.get(locale).get(key, withFormatting, parameters);
	}
	
	@Override
	public String getDefault(Locale locale, String key) {
		
		if (!translations.containsKey(locale)) {
			
			throw new LocalizationLocaleException(locale, "Unavailable locale");
		}
		
		return translations.get(locale).getDefault(key);
	}
	
	@Override
	public String format(Locale locale, String formatName, Object obj) {
		
		if (!translations.containsKey(locale) ) {
			throw new LocalizationLocaleException(locale, "Unavailable locale");
		}
		
		return translations.get(locale).format(formatName, obj); 
	}
}
