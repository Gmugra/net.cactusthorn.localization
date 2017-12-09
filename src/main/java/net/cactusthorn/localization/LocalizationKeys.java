package net.cactusthorn.localization;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.script.ScriptException;

import net.cactusthorn.localization.formats.Formats;

class LocalizationKeys {

	private Sys sys;
	private Formats formats;
	private Map<String,LocalizationKey> translations = new HashMap<>();
	
	LocalizationKeys(String systemId, String languageTag, Map<String,String> properties) throws LocalizationException, ScriptException {
		
		this.sys = new Sys(properties );
		
		if (!languageTag.equals(sys.localeToLanguageTag() ) ) {
			throw new LocalizationException("Wrong value of " + Sys.TAG + "=" + sys.localeToLanguageTag() + ", expected: " + Sys.TAG + "=" + languageTag );
		}
		
		if (!systemId.equals(sys.getId()) ) {
			throw new LocalizationException("Wrong " + Sys.ID + "=" + sys.getId() + ", expected: " + Sys.ID + "=" + systemId );
		}
		
		this.formats = new Formats(sys.getLocale(), properties );
		load(properties );
	}
	
	void combineWith(LocalizationKeys map ) {
		
		sys.combineWith(map.sys);
		formats.combineWith(map.formats);
		
		//Translation::combineWith do nothing if key is not same, so we can use it here
		translations.entrySet().forEach(e -> e.getValue().combineWith(map.translations.get(e.getKey() ) ) );
		
		map.translations.entrySet().forEach(e -> translations.putIfAbsent(e.getKey(), e.getValue() ) );
	}
	
	void addDefault(String key, String value, boolean escapeHtml) {
		if (!translations.containsKey(key ) ) {
			translations.put(key, new LocalizationKey(key));
		}
		translations.get(key).setDefault(value, escapeHtml );
	}
	
	void addPluralSpecial(String key, int special, String value, boolean escapeHtml) {
		if (!translations.containsKey(key ) ) {
			translations.put(key, new LocalizationKey(key));
		}
		translations.get(key).addPluralSpecial(special, value, escapeHtml );
	}
	
	void addPluralSpecial(String key, String special, String value, boolean escapeHtml) {
		addPluralSpecial(key, Integer.parseInt(special), value, escapeHtml );
	}
	
	void addPlural(String key, int plural, String value, boolean escapeHtml) {
		if (!translations.containsKey(key ) ) {
			translations.put(key, new LocalizationKey(key));
		}
		translations.get(key).addPlural(plural, value, escapeHtml );
	}
	
	void addPlural(String key, String plural, String value, boolean escapeHtml) {
		addPlural(key, Integer.parseInt(plural), value, escapeHtml );
	}
	
	String get(String key) {
		return get(key, true, null);
	}
	
	String get(String key, boolean withFormatting) throws LocalizationException {
		return get(key, withFormatting, null);
	}
	
	String get(String key, final Map<String, ?> params) {
		return get(key, true, params);
	}
	
	String get(String key, boolean withFormatting, final Map<String, ?> params) {
		LocalizationKey translation = translations.get(key);
		if (translation == null ) {
			throw new LocalizationKeyException(sys.getLocale(), "unavailable key: " + key );
		}
		return translation.get(sys, withFormatting ? formats : null, params );
	}
	
	String getDefault(String key) {
		LocalizationKey translation = translations.get(key);
		if (translation == null ) {
			throw new LocalizationKeyException(sys.getLocale(), "unavailable key: " + key );
		}
		return translation.getDefault();
	}
	
	private static final String HTML_SUFFIX = "$html";
	
	private void load(Map<String,String> properties ) {
		
		for (Map.Entry<String, String> e : properties.entrySet() ) {
			
			String name = e.getKey();
			
			if (name.indexOf(Sys.SYSTEM_PREFIX) == 0 || name.indexOf(Formats.FORMAT_PREFIX) == 0 ) {
				continue;
			}
			
			String key = name;
			
			boolean escapeHtml = sys.isEscapeHtml();
			
			{
				int index = name.lastIndexOf(HTML_SUFFIX );
				if (index != -1 && index == name.length() - HTML_SUFFIX.length() ) {
					
					escapeHtml = false;
					key = name.substring(0, index);
				}
			}
			
			int lastDot = key.lastIndexOf('.');
			
			if (lastDot == -1 || lastDot == key.length()-1 || key.charAt(key.length()-1 ) == '$' ) {
				
				addDefault(key, properties.get(name), escapeHtml );
				continue;
			}
			
			String firstPart = key.substring(0, lastDot);
			String lastPart = key.substring(lastDot+1);
			
			if (lastPart.charAt(0) != '$' && isPositiveInteger(lastPart ) ) {
				
				addPluralSpecial(firstPart, Integer.parseInt(lastPart), properties.get(name), escapeHtml);
				continue;
			}
			
			if (lastPart.charAt(0) == '$' ) {
				
				String tmp = lastPart.substring(1);
				if (isPositiveInteger(tmp ) ) {
					addPlural(firstPart, Integer.parseInt(tmp), properties.get(name ), escapeHtml );
				} else {
					addDefault(key, properties.get(name), escapeHtml);
				}
				continue;
			}
			
			addDefault(key, properties.get(name), escapeHtml);
		}
	}
	
	private static boolean isPositiveInteger(String str) {
	    if (str == null) {
	        return false;
	    }
	    int length = str.length();
	    if (length == 0) {
	        return false;
	    }
	    for (int i = 0; i < length; i++) {
	        char c = str.charAt(i);
	        if (c < '0' || c > '9') {
	            return false;
	        }
	    }
	    return true;
	}
	
	Locale getLocale() {
		return sys.getLocale();
	}
	
	public String format(String formatName, Object obj) throws LocalizationException {
		return formats.format(formatName, obj); 
	}
}
