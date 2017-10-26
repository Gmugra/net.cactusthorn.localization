package net.cactusthorn.localization;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.cactusthorn.localization.formats.Formats;

class TranslationsMap implements Map<String, Translation> {

	Sys sys;
	Formats formats;
	private Map<String,Translation> translations = new HashMap<>();
	
	TranslationsMap(Sys sys, Formats formats) {
		this.sys = sys;
		this.formats = formats;
	}
	
	void setDefault(String key, String value, boolean escapeHtml) {
		if (!translations.containsKey(key ) ) {
			translations.put(key, new Translation(sys, formats, key));
		}
		translations.get(key).setDefault(value, escapeHtml );
	}
	
	void addPluralSpecial(String key, int special, String value, boolean escapeHtml) {
		if (!translations.containsKey(key ) ) {
			translations.put(key, new Translation(sys, formats, key));
		}
		translations.get(key).addPluralSpecial(special, value, escapeHtml );
	}
	
	void addPluralSpecial(String key, String special, String value, boolean escapeHtml) {
		addPluralSpecial(key, Integer.parseInt(special), value, escapeHtml );
	}
	
	void addPlural(String key, int plural, String value, boolean escapeHtml) {
		if (!translations.containsKey(key ) ) {
			translations.put(key, new Translation(sys, formats, key));
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
		return translation.get(params );
	}
	
	String getTranslation(String key) {
		return getTranslation(key, null);
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

	@Override
	public String toString() {
		return sys.toString() + translations.toString();
	}
}
