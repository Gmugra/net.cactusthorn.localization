package net.cactusthorn.localization;

import java.nio.charset.Charset;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.script.ScriptException;

public class Localization {
	
	private Map<Locale, LocalizationKeys> translations;
	
	protected Localization(Map<Locale, LocalizationKeys> translations) {
		this.translations = translations;
	}
	
	public String get(Locale locale, String key) {
		return get(locale, key, (Map<String, ?>)null);
	}
	
	public String get(Locale locale, String key, Parameter<?>... parameters) {
		return get(locale, key, true, Parameter.asMap(parameters));
	}
	
	public String get(Locale locale, String key, boolean withFormatting, Parameter<?>... parameters ) {
		return get(locale, key, withFormatting, Parameter.asMap(parameters));
	}
	
	public String get(Locale locale, String key, final Map<String, ?> parameters) {
		return get(locale, key, true, parameters);
	}
	
	public String get(Locale locale, String key, boolean withFormatting, final Map<String, ?> parameters) {
	
		if (!translations.containsKey(locale)) {
			
			throw new LocalizationLocaleException(locale, "Unavailable locale");
		}
			
		return translations.get(locale).get(key, withFormatting, parameters);
	}
	
	public String getDefault(Locale locale, String key) {
		
		if (!translations.containsKey(locale)) {
			
			throw new LocalizationLocaleException(locale, "Unavailable locale");
		}
		
		return translations.get(locale).getDefault(key);
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
		
		return new Localization(loadToMap(systemId, l10nDirectory, charset));
	}
	
	protected static Map<Locale, LocalizationKeys> loadToMap(String systemId, Path l10nDirectory, Charset charset) throws IOException {
		
		if (!Files.isDirectory(l10nDirectory) ) {
			throw new IOException("l10nDirectory path " + l10nDirectory + " is not directory");
		}
		
		File[] files = l10nDirectory.toFile().listFiles(f -> f.getName().endsWith(".properties"));
		
		Map<Locale, LocalizationKeys> trs = new HashMap<>();
		
		if (files != null ) {
			for(File file : files) {
				
				try {
					LocalizationKeys trm = loadFile(systemId, file, charset);
					trs.put(trm.getLocale(), trm);
				} catch (LocalizationException | ScriptException e) {
					throw new IOException("Something wrong with file \"" + file.getName() + "\"", e);
				}
			}
		}
		
		return trs;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map<String,String> loadAsMap(File file, Charset charset) throws IOException {
		
		Properties properties = new Properties();
		try (BufferedReader buf = Files.newBufferedReader(file.toPath(), charset ) ) {
			properties.load(buf);
		}
		
		return (Map)properties;
	}
	
	private static LocalizationKeys loadFile(String systemId, File file, Charset charset) throws IOException, LocalizationException, ScriptException {
		
		String fileName = file.getName();
		String fileLanguageTag = fileName.substring(0, fileName.indexOf('.') );
		LocalizationKeys tr = new LocalizationKeys(systemId, fileLanguageTag, loadAsMap(file, charset) );
		
		return tr;
	}
}
