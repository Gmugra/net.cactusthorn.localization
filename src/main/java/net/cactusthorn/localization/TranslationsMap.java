package net.cactusthorn.localization;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.script.ScriptException;

import lombok.ToString;
import net.cactusthorn.localization.formats.Formats;

@ToString(exclude="formats")
class TranslationsMap implements Map<String, Translation> {

	private Sys sys;
	private Formats formats;
	private Map<String,Translation> translations = new HashMap<>();
	
	TranslationsMap(String systemId, String languageTag, Properties properties) throws LocalizationException, ScriptException {
		
		this.sys = new Sys(properties );
		
		if (!languageTag.equals(sys.localeToLanguageTag() ) ) {
			throw new LocalizationException("Wrong value of _system.languageTag=" + sys.localeToLanguageTag() + ", expected: _system.languageTag=" + languageTag );
		}
		
		if (!systemId.equals(sys.getId()) ) {
			throw new LocalizationException("Wrong _system.id=" + sys.getId() + ", expected: _system.id=" + systemId );
		}
		
		this.formats = new Formats(sys.getLocale(), properties );
		load(properties );
	}
	
	void combineWith(TranslationsMap map ) {
		
		sys.combineWith(map.sys);
		formats.combineWith(map.formats);
		
		//Translation::combineWith do nothing if key is not same, so we can use it here
		translations.entrySet().forEach(e -> e.getValue().combineWith(map.get(e.getKey() ) ) );
		
		map.entrySet().forEach(e -> translations.putIfAbsent(e.getKey(), e.getValue() ) );
	}
	
	void setDefault(String key, String value, boolean escapeHtml) {
		if (!translations.containsKey(key ) ) {
			translations.put(key, new Translation(key));
		}
		translations.get(key).setDefault(value, escapeHtml );
	}
	
	void addPluralSpecial(String key, int special, String value, boolean escapeHtml) {
		if (!translations.containsKey(key ) ) {
			translations.put(key, new Translation(key));
		}
		translations.get(key).addPluralSpecial(special, value, escapeHtml );
	}
	
	void addPluralSpecial(String key, String special, String value, boolean escapeHtml) {
		addPluralSpecial(key, Integer.parseInt(special), value, escapeHtml );
	}
	
	void addPlural(String key, int plural, String value, boolean escapeHtml) {
		if (!translations.containsKey(key ) ) {
			translations.put(key, new Translation(key));
		}
		translations.get(key).addPlural(plural, value, escapeHtml );
	}
	
	void addPlural(String key, String plural, String value, boolean escapeHtml) {
		addPlural(key, Integer.parseInt(plural), value, escapeHtml );
	}
	
	String getTranslation(String key, final Map<String, ?> params) {
		Translation translation = get(key);
		if (translation == null ) {
			return null;
		}
		return translation.get(sys, formats, params );
	}
	
	String getTranslation(String key) {
		return getTranslation(key, null);
	}
	
	private static final String HTML_SUFFIX = "$html";
	
	private void load(Properties properties ) {
		
		for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
			
			String name = (String)e.nextElement();
			
			if (name.indexOf("_system.") == 0 ) {
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
				
				setDefault(key, properties.getProperty(name), escapeHtml );
				continue;
			}
			
			String firstPart = key.substring(0, lastDot);
			String lastPart = key.substring(lastDot+1);
			
			if (lastPart.charAt(0) != '$' && isPositiveInteger(lastPart ) ) {
				
				addPluralSpecial(firstPart, Integer.parseInt(lastPart), properties.getProperty(name), escapeHtml);
				continue;
			}
			
			if (lastPart.charAt(0) == '$' ) {
				
				String tmp = lastPart.substring(1);
				if (isPositiveInteger(tmp ) ) {
					addPlural(firstPart, Integer.parseInt(tmp), properties.getProperty(name ), escapeHtml );
				} else {
					setDefault(key, properties.getProperty(name), escapeHtml);
				}
				continue;
			}
			
			setDefault(key, properties.getProperty(name), escapeHtml);
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
	
	public Locale getLocale() {
		return sys.getLocale();
	}
	
	public String format(String formatName, Object obj) throws LocalizationException {
		return formats.format(formatName, obj); 
	}
	
	@Override
	public void clear() {
		translations.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return translations.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return translations.containsValue(value);
	}

	@Override
	public Set<Entry<String, Translation>> entrySet() {
		return translations.entrySet();
	}

	@Override
	public Translation get(Object key) {
		return translations.get(key);
	}

	@Override
	public boolean isEmpty() {
		return translations.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return translations.keySet();
	}

	@Override
	public Translation put(String key, Translation value) {
		return translations.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Translation> m) {
		translations.putAll(m);
	}

	@Override
	public Translation remove(Object key) {
		return translations.remove(key);
	}

	@Override
	public int size() {
		return translations.size();
	}

	@Override
	public Collection<Translation> values() {
		return translations.values();
	}
}
