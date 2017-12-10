package net.cactusthorn.localization;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import net.cactusthorn.localization.core.LocalizationKeys;

public class BasicLocalization implements Localization{
	
	private Map<Locale, LocalizationKeys> translations;
	
	public BasicLocalization(Map<Locale, LocalizationKeys> translations) {
		this.translations = translations;
	}
	
	@Override
	public Locale findNearest(Locale locale) {
		
		if (translations.containsKey(locale) ) return locale;
		
		if (!"".equals(locale.getVariant() ) ) {
			
			Optional<Locale> found = 
				translations.keySet().stream()
					.filter(l -> l.getLanguage().equals(locale.getLanguage() ) && l.getCountry().equals(locale.getLanguage() ) )
					.findAny();
			
			if (found.isPresent() ) return found.get();
		}
		
		Optional<Locale> found = 
			translations.keySet().stream()
				.filter(l -> l.getLanguage().equals(locale.getLanguage() ) )
				.findAny();
			
		if (found.isPresent() ) return found.get();
		
		return null;
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
	
		Locale exists = findNearest(locale);
		
		if (exists == null) {
			
			throw new LocalizationLocaleException(locale, "Unavailable locale");
		}
			
		return translations.get(exists).get(key, withFormatting, parameters);
	}
	
	@Override
	public String getDefault(Locale locale, String key) {
		
		Locale exists = findNearest(locale);
		
		if (exists == null) {
			
			throw new LocalizationLocaleException(locale, "Unavailable locale");
		}
		
		return translations.get(exists).getDefault(key);
	}
	
	@Override
	public String format(Locale locale, String formatName, Object obj) {
		
		Locale exists = findNearest(locale);
		
		if (exists == null) {
			throw new LocalizationLocaleException(locale, "Unavailable locale");
		}
		
		return translations.get(exists).format(formatName, obj); 
	}
}
