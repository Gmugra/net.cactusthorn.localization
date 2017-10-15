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

public class Localization {

	private static final Logger LOGGER = LoggerFactory.getLogger(Localization.class);
	
	private Map<String, TranslationsMap> translations;
	
	private Localization(Map<String, TranslationsMap> translations) {
		this.translations = translations;
	}
	
	public String getTranslation(Locale locale, String key) {
		return getTranslation(locale, key, null);
	}
	
	public String getTranslation(Locale locale, String key, final Map<String, String> params) {
		
		String languageTag = locale.toLanguageTag();
		
		if (translations.containsKey(languageTag)) {
			return translations.get(languageTag).getTranslation(key, params);
		}

		LOGGER.warn("getTranslation({},{}) is not available.", languageTag, key);
		return "unknown translation " + languageTag + ":" + key;
	}
	
	public static Localization load(Path l10nDirectory) throws IOException, ScriptException {
		return load(l10nDirectory, UTF_8);
	}
	
	public static Localization load(Path l10nDirectory, Charset charset) throws IOException, ScriptException {
		
		if (!Files.isDirectory(l10nDirectory) ) {
			throw new IOException("l10nDirectory Path is not Directory.");
		}
		
		File[] files = l10nDirectory.toFile().listFiles(f -> f.getName().endsWith(".properties"));
		
		Map<String, TranslationsMap> trs = new HashMap<>();
		
		for(File file : files) {
			
			TranslationsMap trm = loadFile(file, charset);
			trs.put(trm.getSysLocaleLanguageTag(), trm);
		}
		
		return new Localization(trs);
	}
	
	private static final String HTML_SUFFIX = "$html";
	
	static TranslationsMap loadFile(File file, Charset charset) throws IOException, ScriptException {
		
		String fileName = file.getName();
		LOGGER.info("Localization file \"{}\" is loading...", fileName);
		
		Properties properties = new Properties();
		try (BufferedReader buf = Files.newBufferedReader(file.toPath(), charset ) ) {
			properties.load(buf);
		}
		
		Sys sys = new Sys(properties );
		
		String fileLocale = fileName.substring(0, fileName.indexOf('.') );
		if (!fileLocale.equals(sys.localeToLanguageTag() ) ) {
			LOGGER.error("Localization file \"{}\", file name do not fit _system.locale={}", fileName, sys.localeToLanguageTag() );
			return null;
		}

		TranslationsMap tr = new TranslationsMap(sys);
		
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
