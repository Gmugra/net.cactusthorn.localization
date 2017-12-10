package net.cactusthorn.localization;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.script.ScriptException;

import net.cactusthorn.localization.core.LocalizationKeys;

public class LocalizationLoader {

	private Class<? extends Localization> localizationClass = BasicLocalization.class;
	
	private Path l10nDirectory;
	
	private String systemId;
	
	private Charset charset = UTF_8;

	public LocalizationLoader(String systemId) {
		this.systemId = systemId;
	}

	public LocalizationLoader setL10nDirectory(Path l10nDirectory) {
		this.l10nDirectory = l10nDirectory;
		return this;
	}

	public LocalizationLoader setCharset(Charset charset) {
		this.charset = charset;
		return this;
	}

	public LocalizationLoader setClass(Class<? extends Localization> localizationClass) {
		this.localizationClass = localizationClass;
		return this;
	}

	public Localization load() throws IOException {

		try {
			Constructor<? extends Localization> constructor = localizationClass.getConstructor(Map.class);
			
			Map<Locale, LocalizationKeys> defaults = loadMap(true);
			Map<Locale, LocalizationKeys> locales = loadMap(false);

			locales.entrySet().forEach(e -> { if (defaults.containsKey(e.getKey())) { defaults.get(e.getKey()).combineWith(e.getValue()); } } );
			locales.entrySet().forEach(e -> defaults.putIfAbsent(e.getKey(), e.getValue() ) );

			return constructor.newInstance(defaults );
		} catch (RuntimeException | IOException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static final String DEFAULT_FILE_PREFIX = "default.";
	
	protected Map<Locale, LocalizationKeys> loadMap(boolean defaults) throws IOException, URISyntaxException {
		
		Path path = l10nDirectory;
		if (path == null ) {
			path = Paths.get(LocalizationLoader.class.getClassLoader().getResource("L10n").toURI());
		}
		
		if (!Files.isDirectory(path) ) {
			throw new IOException("l10nDirectory path " + l10nDirectory + " is not directory");
		}
		
		File[] files;
		if (defaults) {
			files = path.toFile().listFiles(f -> f.getName().endsWith(".properties") && f.getName().startsWith(DEFAULT_FILE_PREFIX) );
		} else {
			files = path.toFile().listFiles(f -> f.getName().endsWith(".properties") && !f.getName().startsWith(DEFAULT_FILE_PREFIX) );
		}
		
		Map<Locale, LocalizationKeys> trs = new HashMap<>();
		
		if (files != null ) {
			for(File file : files) {
				
				try {
					LocalizationKeys trm = loadFile(file, defaults);
					trs.put(trm.getLocale(), trm);
				} catch (LocalizationException | ScriptException e) {
					throw new IOException("Something wrong with file \"" + file.getName() + "\"", e);
				}
			}
		}
		
		return trs;
	}
	
	protected LocalizationKeys loadFile(File file, boolean defaults) throws IOException, LocalizationException, ScriptException {
		
		String fileName = file.getName();
		if (defaults) {
			fileName = fileName.substring(DEFAULT_FILE_PREFIX.length() );
		}
		
		String fileLanguageTag = fileName.substring(0, fileName.indexOf('.') );
		
		LocalizationKeys tr = new LocalizationKeys(defaults ? null : systemId, fileLanguageTag, propertiesAsMap(file ) );
		
		return tr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map<String,String> propertiesAsMap(File file) throws IOException {
		
		Properties properties = new Properties();
		try (BufferedReader buf = Files.newBufferedReader(file.toPath(), charset ) ) {
			properties.load(buf);
		}
		
		return (Map)properties;
	}
}
