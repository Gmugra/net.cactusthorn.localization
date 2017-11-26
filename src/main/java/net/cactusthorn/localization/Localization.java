package net.cactusthorn.localization;

import java.nio.charset.Charset;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cactusthorn.localization.formats.Formats;

public class Localization {

	private static final Logger LOGGER = LoggerFactory.getLogger(Localization.class);
	
	private Map<Locale, TranslationsMap> translations;
	
	private Localization(Map<Locale, TranslationsMap> translations) {
		this.translations = translations;
	}
	
	public String getTranslation(Locale locale, String key) {
		return getTranslation(locale, key, (Map<String, ?>)null);
	}
	
	public String getTranslation(Locale locale, String key, Parameter<?>... parameters) {
		return getTranslation(locale, key, Parameter.asMap(parameters));
	}
	
	public String getTranslation(Locale locale, String key, final Map<String, ?> params) {
	
		if (translations.containsKey(locale)) {
			return translations.get(locale).getTranslation(key, params);
		}

		LOGGER.warn("getTranslation({},{}) is not available.", locale.toLanguageTag(), key);
		return "unknown translation " + locale.toLanguageTag() + ":" + key;
	}
	
	public Formats getFormats(Locale locale ) {
		
		if (!translations.containsKey(locale)) return null;
		return translations.get(locale).getFormats();
	}
	
	public static Localization load(String systemId, Path l10nDirectory) throws IOException, ScriptException {
		return load(systemId, l10nDirectory, UTF_8);
	}
	
	public static Localization load(String systemId, Path l10nDirectory, Charset charset) throws IOException, ScriptException {
		
		if (!Files.isDirectory(l10nDirectory) ) {
			throw new IOException("l10nDirectory Path is not Directory.");
		}
		
		File[] files = l10nDirectory.toFile().listFiles(f -> f.getName().endsWith(".properties"));
		
		Map<Locale, TranslationsMap> trs = new HashMap<>();
		
		for(File file : files) {
			
			TranslationsMap trm = loadFile(systemId, file, charset);
			trs.put(trm.sys.getLocale(), trm);
		}
		
		return new Localization(trs);
	}
	
	private static final String HTML_SUFFIX = "$html";
	
	private static TranslationsMap loadFile(String systemId, File file, Charset charset) throws IOException, ScriptException {
		
		String fileName = file.getName();
		
		Properties properties = new Properties();
		try (BufferedReader buf = Files.newBufferedReader(file.toPath(), charset ) ) {
			properties.load(buf);
		}
		
		Sys sys = new Sys(properties );
		
		String fileLocale = fileName.substring(0, fileName.indexOf('.') );
		if (!fileLocale.equals(sys.localeToLanguageTag() ) ) {
			throw new LocalizationException(sys.getLocale(), "Localization file \"" + fileName + "\", the file name do not fit _system.languageTag=" + sys.localeToLanguageTag() );
		}

		if (!systemId.equals(sys.getId()) ) {
			throw new LocalizationException(sys.getLocale(), "Localization file \"" + fileName + "\", wrong _system.id=" + sys.getId() +", expected: _system.id=" + systemId );
		}
		
		Formats formats = new Formats(sys, properties );
		
		TranslationsMap tr = new TranslationsMap(sys, formats);
		
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
				
				tr.setDefault(key, properties.getProperty(name), escapeHtml );
				continue;
			}
			
			String firstPart = key.substring(0, lastDot);
			String lastPart = key.substring(lastDot+1);
			
			if (lastPart.charAt(0) != '$' && isPositiveInteger(lastPart ) ) {
				
				tr.addPluralSpecial(firstPart, Integer.parseInt(lastPart), properties.getProperty(name), escapeHtml);
				continue;
			}
			
			if (lastPart.charAt(0) == '$' ) {
				
				String tmp = lastPart.substring(1);
				if (isPositiveInteger(tmp ) ) {
					tr.addPlural(firstPart, Integer.parseInt(tmp), properties.getProperty(name ), escapeHtml );
				} else {
					tr.setDefault(key, properties.getProperty(name), escapeHtml);
				}
				continue;
			}
			
			tr.setDefault(key, properties.getProperty(name), escapeHtml);
		}
		
		LOGGER.info("Localization file \"{}\" is loaded.", fileName);
		
		return tr;
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
}
