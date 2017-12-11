/*******************************************************************************
 * Copyright (C) 2017, Alexei Khatskevich
 * All rights reserved.
 * 
 * Licensed under the BSD 2-clause (Simplified) License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://opensource.org/licenses/BSD-2-Clause
 ******************************************************************************/
package net.cactusthorn.localization;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

	private Class<? extends AbstractLocalization> localizationClass = BasicLocalization.class;
	
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

	public LocalizationLoader setClass(Class<? extends AbstractLocalization> localizationClass) {
		this.localizationClass = localizationClass;
		return this;
	}
	
	public Localization load() throws IOException {
		
		Map<Locale, LocalizationKeys> localizationKeys = loadAsMap();
		
		try {
			Constructor<? extends Localization> constructor = localizationClass.getConstructor(Map.class, String.class, Path.class, Charset.class);
			return constructor.newInstance(localizationKeys, systemId, l10nDirectory, charset );
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Map<Locale, LocalizationKeys> loadAsMap() throws IOException {
		
		if (l10nDirectory == null ) {
			try {
				l10nDirectory = Paths.get(LocalizationLoader.class.getClassLoader().getResource("L10n").toURI());
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}

		Map<Locale, LocalizationKeys> defaults = loadMap(true);
		Map<Locale, LocalizationKeys> locales = loadMap(false);

		locales.entrySet().forEach(e -> { if (defaults.containsKey(e.getKey())) { defaults.get(e.getKey()).combineWith(e.getValue()); } } );
		locales.entrySet().forEach(e -> defaults.putIfAbsent(e.getKey(), e.getValue() ) );

		return defaults;
	}
	
	public static final String DEFAULT_FILE_PREFIX = "default.";
	
	protected Map<Locale, LocalizationKeys> loadMap(boolean defaults) throws IOException {
		
		if (!Files.isDirectory(l10nDirectory ) ) {
			throw new IOException("l10nDirectory path " + l10nDirectory + " is not directory");
		}
		
		File[] files;
		if (defaults) {
			files = l10nDirectory.toFile().listFiles(f -> !f.isDirectory() && f.getName().endsWith(".properties") && f.getName().startsWith(DEFAULT_FILE_PREFIX) );
		} else {
			files = l10nDirectory.toFile().listFiles(f -> !f.isDirectory() && f.getName().endsWith(".properties") && !f.getName().startsWith(DEFAULT_FILE_PREFIX) );
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
