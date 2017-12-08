package net.cactusthorn.localization;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;

import lombok.ToString;
import net.cactusthorn.localization.formats.Formats;

@ToString
public class Translation {
	
	static final String PS = "{{";
	static final String PE = "}}";
	static final int PSL = PS.length();
	static final int PEL = PE.length();
	
	private static final CharSequenceTranslator ESCAPE_HTML_BASIC = new LookupTranslator(EntityArrays.BASIC_ESCAPE);

	String key;
	String defaultMessage;
	Map<Integer,String> plurals;
	Map<Integer,String> specials;
	
	private static String escapeHtmlBasic(String input ) {
		return ESCAPE_HTML_BASIC.translate(input);
	}
	
	Translation(String key)  {
		this.key = key;
	}
	
	void combineWith(Translation translation ) {
		
		if (!this.key.equals(translation.key) ) return;
		
		if (translation.defaultMessage != null) this.defaultMessage = translation.defaultMessage;
		
		if (this.plurals != null && translation.plurals != null ) this.plurals.putAll(translation.plurals);
		else if (this.plurals == null && translation.plurals != null ) this.plurals = translation.plurals;
			
		if (this.specials != null && translation.specials != null ) this.specials.putAll(translation.specials);
		else if (this.specials == null && translation.specials != null ) this.specials = translation.specials;
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
	
	Translation addPluralSpecial(int special, String message, boolean escapeHtml) {
		
		if(specials == null ) {
			specials = new HashMap<>();
		}
		specials.put(special, escapeHtml ? escapeHtmlBasic(message) : message );
		return this;
	}
	
	//*****
	
	String defaultMessage() {
		return defaultMessage == null ? "" : defaultMessage;
	}
	
	String get(Sys sys) throws LocalizationException {
		return get(sys, null, Parameter.EMPTY_PARAM_MAP);
	}
	
	String get(Sys sys, final Parameter<?>... parameters) throws LocalizationException {
		return get(sys, null, parameters);
	}
	
	String get(Sys sys, final Map<String, ?> parameters) throws LocalizationException {
		return get(sys, null, parameters);
	}
	
	String get(Sys sys, Formats formats ) throws LocalizationException {
		return get(sys, formats, Parameter.EMPTY_PARAM_MAP);
	}
	
	String get(Sys sys, Formats formats, final Parameter<?>... parameters ) throws LocalizationException {	
		return get(sys, formats, Parameter.asMap(parameters));
	}
	
	String get(Sys sys, Formats formats, final Map<String, ?> parameters) throws LocalizationException {
		
		if (parameters == null || parameters.isEmpty() ) {
			return defaultMessage();
		}
		
		if (!parameters.containsKey("count")) {
			return replace(sys, formats, key, defaultMessage(), parameters);
		}
		
		if (plurals == null && specials == null ) {
			return replace(sys, formats, key, defaultMessage(), parameters);
		}
		
		int count = -1;
		if (parameters.containsKey("count") ) {
			Object obj = parameters.get("count");
			if (obj != null) {
				try {
					count = (int)parameters.get("count");
				} catch (ClassCastException cce) {
					throw new LocalizationException(sys.getLocale(), "wrong value \"" + parameters.get("count") + "\" of {{count}} parameter for the key: " + key, cce);
				}
			}
		}
		
		if (specials != null && specials.containsKey(count ) ) {
			return replace(sys, formats, key + '.' + count, specials.get(count ), parameters);
		}
		
		if (plurals == null ) {
			return replace(sys, formats, key, defaultMessage(), parameters);
		}
		
		int plural = -1;
		try {
			plural = sys.evalPlural(count);
		} catch (ScriptException se) {
			throw new LocalizationException(sys.getLocale(), "count=" + count + ", key \"" + key + "\"", se);
		}
		
		if (plurals.containsKey(plural ) ) {
			return replace(sys, formats, key + '.' + '$' + plural, plurals.get(plural ), parameters);
		}
		
		return replace(sys, formats, key, defaultMessage(), parameters);
	}
	
	private String replace(Sys sys, Formats formats, String key, String message, final Map<String, ?> params) {
		
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
				if (formats != null && format != null) {
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
		
		return result.toString();
	}
}
