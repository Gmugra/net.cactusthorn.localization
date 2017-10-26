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

import net.cactusthorn.localization.formats.Formats;

public class Translation {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Translation.class);
	
	private static final String PS = "{{";
	private static final String PE = "}}";
	private static final int PSL = PS.length();
	private static final int PEL = PE.length();
	
	private static final CharSequenceTranslator ESCAPE_HTML_BASIC = new LookupTranslator(EntityArrays.BASIC_ESCAPE);
	
	Sys sys;
	Formats formats;
	String key;
	String defaultMessage = "";
	Map<Integer,String> plurals;
	Map<Integer,String> specials;
	
	static String escapeHtmlBasic(String input ) {
		return ESCAPE_HTML_BASIC.translate(input);
	}
	
	Translation(Sys sys, Formats formats, String key)  {
		this.sys = sys;
		this.formats = formats;
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
		return get(Parameter.EMPTY_PARAM_MAP);
	}
	
	String get(Parameter<?>... parameters ) {	
		return get(Parameter.asMap(parameters));
	}
	
	String get(final Map<String, ?> parameters ) {
		
		if (parameters == null || parameters.isEmpty() ) {
			logMissingParameters(key, defaultMessage);
			return defaultMessage;
		}
		
		if (!parameters.containsKey("count")) {
			return replace(key, defaultMessage, parameters);
		}
		
		if (plurals == null && specials == null ) {
			return replace(key, defaultMessage, parameters);
		}
		
		int count = -1;
		if (parameters.containsKey("count") ) {
			Object obj = parameters.get("count");
			if (obj != null) {
				try {
					count = (int)parameters.get("count");
				} catch (ClassCastException cce) {
					LOGGER.error("Locale: {}, wrong value \"{}\" of \"count\" parameter for key \"{}\" ", sys.localeToLanguageTag(), parameters.get("count"), key);
					return replace(key, defaultMessage, parameters);
				}
			}
		}
		
		if (specials != null && specials.containsKey(count ) ) {
			return replace(key + '.' + count, specials.get(count ), parameters);
		}
		
		if (plurals == null ) {
			return replace(key, defaultMessage, parameters);
		}
		
		int plural = -1;
		try {
			plural = sys.evalPlural(count);
		} catch (ScriptException te) {
			LOGGER.error("Locale: {}, count={}, key \"{}\" ", sys.localeToLanguageTag(), count, key, te);
			return replace(key, defaultMessage, parameters);
		}
		
		if (plurals.containsKey(plural ) ) {
			return replace(key + '.' + '$' + plural, plurals.get(plural ), parameters);
		}
		
		return replace(key, defaultMessage, parameters);
	}
	
	private String replace(String key, String message, final Map<String, ?> params) {
		
		StringBuilder result = new StringBuilder();
		int endIndex = -1 * PEL;
		int beginIndex = message.indexOf(PS);
		while (beginIndex != -1 ) {
	
			result.append(message.substring(endIndex + PEL, beginIndex ));
			
			endIndex = message.indexOf(PE, beginIndex + PSL);
			if (endIndex == -1) {
				break;
			}
			
			String parameter = message.substring(beginIndex + PSL, endIndex );
			String format = null;
			int commaIndex = parameter.indexOf(',');
			if (commaIndex != -1 ) {
				format = parameter.substring(commaIndex+1);
				parameter = parameter.substring(0, commaIndex);
			}
			
			if (params.containsKey(parameter ) ) {
				if (format != null) {
					result.append(formats.format(format, params.get(parameter) ) );
				} else {
					result.append(params.get(parameter));
				}
			} else {
				result.append(message.substring(beginIndex, endIndex + PEL ));
			}
			
			beginIndex = message.indexOf(PS, endIndex + PEL);
		}
		if (endIndex != -1 ) {
			result.append(message.substring(endIndex+2));
		} else if (beginIndex != -1 ) {
			result.append(message.substring(beginIndex));
		}
		
		logMissingParameters(key, result.toString());
		
		return result.toString();
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
			missing.add(message.substring(startIndex + PSL, endIndex));
			startIndex = message.indexOf(PS, endIndex + PEL);
		}
		
		if (missing != null) {
			LOGGER.warn("Locale: {}, not all parameters provided for key \"{}\", missing parameters: {}", sys.localeToLanguageTag(), key, missing);
		}
	}
}
