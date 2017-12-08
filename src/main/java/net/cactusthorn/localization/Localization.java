package net.cactusthorn.localization;

import java.nio.charset.Charset;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.script.ScriptException;

//import lombok.extern.slf4j.Slf4j;

//@Slf4j
public class Localization {
	
	private Map<Locale, Translations> translations;
	
	private Localization(Map<Locale, Translations> translations) {
		this.translations = translations;
	}
	
	public String get(Locale locale, String key) {
		return get(locale, key, (Map<String, ?>)null);
	}
	
	public String get(Locale locale, String key, Parameter<?>... parameters) {
		return get(locale, key, Parameter.asMap(parameters));
	}
	
	public String get(Locale locale, String key, final Map<String, ?> params) {
	
		if (!translations.containsKey(locale)) {
			
			throw new LocalizationLocaleException(locale, "Unavailable locale");
		}
			
		return translations.get(locale).get(key, params);
		/*try {
			String result = translations.get(locale).getTranslation(key, params);
			logMissingParameters(locale, key, result);
			return result;
		} catch (LocalizationException le) {
			log.error("", le);
			return translations.get(locale).getDefault(key);
		}*/
	}
	
	public String format(Locale locale, String formatName, Object obj) throws LocalizationException {
		
		if (!translations.containsKey(locale) ) {
			throw new LocalizationLocaleException(locale, "Unavailable locale");
		}
		
		return translations.get(locale).format(formatName, obj); 
	}
	
	public static Localization load(String systemId, Path l10nDirectory) throws IOException, ScriptException {
		return load(systemId, l10nDirectory, UTF_8);
	}
	
	public static Localization load(String systemId, Path l10nDirectory, Charset charset) throws IOException {
		
		if (!Files.isDirectory(l10nDirectory) ) {
			throw new IOException("l10nDirectory path " + l10nDirectory + " is not directory");
		}
		
		File[] files = l10nDirectory.toFile().listFiles(f -> f.getName().endsWith(".properties"));
		
		Map<Locale, Translations> trs = new HashMap<>();
		
		if (files != null ) {
			for(File file : files) {
				
				try {
					Translations trm = loadFile(systemId, file, charset);
					trs.put(trm.getLocale(), trm);
				} catch (LocalizationException | ScriptException e) {
					throw new IOException("Something wrong with file \"" + file.getName() + "\"", e);
				}
			}
		}
		
		return new Localization(trs);
	}
	
	private static Translations loadFile(String systemId, File file, Charset charset) throws IOException, LocalizationException, ScriptException {
		
		String fileName = file.getName();
		
		Properties properties = new Properties();
		try (BufferedReader buf = Files.newBufferedReader(file.toPath(), charset ) ) {
			properties.load(buf);
		}
		
		String fileLanguageTag = fileName.substring(0, fileName.indexOf('.') );
		Translations tr = new Translations(systemId, fileLanguageTag, properties);
		
		//log.info("Localization file \"{}\" is successfully loaded.", fileName);
		
		return tr;
	}
	
	/*private void logMissingParameters(Locale locale, String key, String message ) {
	
		if (!log.isWarnEnabled()) {
			return;
		}
		
		List<String> missing = null;
		for(int startIndex = message.indexOf(Translation.PS);startIndex != -1; ) {
			int endIndex = message.indexOf(Translation.PE, startIndex);
			if (endIndex == -1) {
				break;
			}
			if (missing == null ) {
				missing = new ArrayList<>();
			}
			missing.add(message.substring(startIndex + Translation.PSL, endIndex));
			startIndex = message.indexOf(Translation.PS, endIndex + Translation.PEL);
		}
		
		if (missing != null) {
			log.warn("Locale: {}, not all parameters provided for key \"{}\", missing parameters: {}", locale.toLanguageTag(), key, missing);
		}
	}*/
}
