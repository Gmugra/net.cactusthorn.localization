package net.cactusthorn.localization;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.cactusthorn.localization.core.LocalizationKey;
import net.cactusthorn.localization.core.LocalizationKeys;

@Slf4j
public class LoggingLocalization extends BasicLocalization {

	public LoggingLocalization(Map<Locale, LocalizationKeys> translations) {
		super(translations);
	}

	@Override
	public String get(Locale locale, String key, boolean withFormatting, Map<String, ?> params) {
		
		try {
			
			String text = super.get(locale, key, withFormatting, params);
			logMissingParameters(locale, key, text);
			return text;
		} catch (LocalizationKeyException | LocalizationLocaleException e ) {
			
			log.error("",e);
			return e.getMessage();
		} catch (LocalizationFormatException e ) {
			
			log.error("",e);
			
			//LocalizationFormatException mean that correct key has bean found, but logic failed to format some parameter.
			//So, lets return found value without formatted parameters. Must work without exception.
			String text = super.get(locale, key, false, params);
			logMissingParameters(locale, key, text);
			return text;
		} catch (LocalizationException e ) {
			
			log.error("",e);
			
			//LocalizationException at this moment mean that, before formatting, something wrong with parameters
			//So, lets return default message ASIS
			//Note: LocalizationLocaleException will never happens here, because checked for the key before
			String text = super.getDefault(locale, key);
			return text.isEmpty() ? key + " : default text is undefined" : text;
		}
	}
	
	private void logMissingParameters(Locale locale, String key, String text ) {
	
		if (!log.isWarnEnabled()) {
			return;
		}
		
		List<String> missing = null;
		for(int startIndex = text.indexOf(LocalizationKey.PS);startIndex != -1; ) {
			int endIndex = text.indexOf(LocalizationKey.PE, startIndex);
			if (endIndex == -1) {
				break;
			}
			if (missing == null ) {
				missing = new ArrayList<>();
			}
			missing.add(text.substring(startIndex + LocalizationKey.PSL, endIndex));
			startIndex = text.indexOf(LocalizationKey.PS, endIndex + LocalizationKey.PEL);
		}
		
		if (missing != null) {
			log.warn("Locale: {}, not all parameters provided for key \"{}\", missing parameters: {}", locale.toLanguageTag(), key, missing);
		}
	}
}
