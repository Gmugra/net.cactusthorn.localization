package net.cactusthorn.localization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Translation {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Translation.class);
	
	private static final String PS = "{{";
	private static final String PE = "}}";
	
	private static final CharSequenceTranslator ESCAPE_HTML_BASIC = new LookupTranslator(EntityArrays.BASIC_ESCAPE);
	
	Sys sys;
	String key;
	String defaultMessage = "";
	Map<Integer,String> plurals;
	Map<Integer,String> specials;
	
	static String escapeHtmlBasic(String input ) {
		return ESCAPE_HTML_BASIC.translate(input);
	}
	
	Translation(Sys sys, String key) {
		this.sys = sys;
		this.key = key;
	}
	
	Translation setDefault(String defaultMessage) {
		return setDefault(defaultMessage, sys.isEscapeHtml());
	}
	
	Translation setDefault(String defaultMessage, boolean escapeHtml) {
		this.defaultMessage = escapeHtml ? escapeHtmlBasic(defaultMessage) : defaultMessage;
		return this;
	}
	
	Translation addPlural(int plural, String message, boolean escapeHtml) {
		
		if(plurals == null ) {
			plurals = new HashMap<>();
		}
		plurals.put(plural, escapeHtml ? escapeHtmlBasic(message) : message );
		return this;
	}
	
	Translation addPlural(int plural, String message) {
		return addPlural(plural, message, sys.isEscapeHtml());
	}
	
	Translation addPluralSpecial(int special, String message, boolean escapeHtml) {
		
		if(specials == null ) {
			specials = new HashMap<>();
		}
		specials.put(special, escapeHtml ? escapeHtmlBasic(message) : message );
		return this;
	}
	
	Translation addPluralSpecial(int plural, String message) {
		return addPluralSpecial(plural, message, sys.isEscapeHtml());
	}
	
	String get() {
		
		return get(null);
	}
	
	String get(final Map<String, String> params ) {
		
		if (params == null || params.isEmpty() ) {
			logMissingParameters(key, defaultMessage);
			return defaultMessage;
		}
		
		if (!params.containsKey("count")) {
			return replace(key, defaultMessage, params);
		}
		
		if (plurals == null && specials == null ) {
			return replace(key, defaultMessage, params);
		}
		
		int count = -1;
		try {
			count = Integer.parseInt(params.get("count"));
		} catch (NumberFormatException nfe) {
			LOGGER.error("Locale: {}, wrong value \"{}\" of \"count\" parameter for key \"{}\" ", sys.localeToLanguageTag(), params.get("count"), key);
			return replace(key, defaultMessage, params);
		}
		
		if (specials != null && specials.containsKey(count ) ) {
			return replace(key + '.' + count, specials.get(count ), params);
		}
		
		if (plurals == null ) {
			return replace(key, defaultMessage, params);
		}
		
		int plural = -1;
		try {
			plural = sys.evalPlural(count);
		} catch (ScriptException te) {
			LOGGER.error("Locale: {}, count={}, key \"{}\" ", sys.localeToLanguageTag(), count, key, te);
			return replace(key, defaultMessage, params);
		}
		
		if (plurals.containsKey(plural ) ) {
			return replace(key + '.' + '$' + plural, plurals.get(plural ), params);
		}
		
		return replace(key, defaultMessage, params);
	}
	
	private String replace(String key, String message, final Map<String, String> params) {
		
		String result = message;
		
		for (Map.Entry<String,String> e : params.entrySet() ) {
			result = result.replace(PS + e.getKey() + PE, e.getValue()); 
		}
		
		logMissingParameters(key, result);
		
		return result;
	}
	
	private void logMissingParameters(String key, String message ) {
		
		if (!LOGGER.isWarnEnabled()) {
			return;
		}
		
		List<String> missing = null;
		for(int startIndex = message.indexOf(PS);startIndex != -1; ) {
			int endIndex = message.indexOf(PE, startIndex);
			if (endIndex == -1) {
				break;
			}
			if (missing == null ) {
				missing = new ArrayList<>();
			}
			missing.add(message.substring(startIndex + PS.length(), endIndex));
			startIndex = message.indexOf(PS, endIndex + PE.length());
		}
		
		if (missing != null) {
			LOGGER.warn("Locale: {}, not all parameters provided for key \"{}\", missing parameters: {}", sys.localeToLanguageTag(), key, missing);
		}
	}
}
