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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import jakarta.el.ELException;
import net.cactusthorn.localization.core.LocalizationKeys;

public abstract class AbstractLocalizationLoader implements LocalizationLoader {

	protected Class<? extends AbstractLocalization> localizationClass = BasicLocalization.class;
	
	protected String systemId;
	
	protected Charset charset = UTF_8;
	
	protected String l10nDirectory = DEFAULT_DIRECTORY;

	public AbstractLocalizationLoader(String systemId) {
		this.systemId = systemId;
	}

	@Override
	public LocalizationLoader encoded(Charset charset) {
		this.charset = charset;
		return this;
	}

	@Override
	public LocalizationLoader instanceOf(Class<? extends AbstractLocalization> localizationClass) {
		this.localizationClass = localizationClass;
		return this;
	}
	
	@Override
	public LocalizationLoader from(String l10nDirectory) {
		if (l10nDirectory == null ) this.l10nDirectory = DEFAULT_DIRECTORY;
		else this.l10nDirectory = l10nDirectory;
		return this;
	}
	
	@Override
	public Localization load() throws URISyntaxException, IOException {
		return load(l10nDirectoryToURI(l10nDirectory));
	}
	
	@Override
	public Map<Locale, LocalizationKeys> loadAsMap() throws URISyntaxException, IOException {
		return loadAsMap(l10nDirectoryToURI(l10nDirectory));
	}
	
	public static URI l10nDirectoryToURI(String l10nDirectory) throws URISyntaxException {
		URL url = Thread.currentThread().getContextClassLoader().getResource(l10nDirectory);
		if (url != null ) return url.toURI();
		return Paths.get(l10nDirectory).toUri();
	}
	
	protected Localization load(URI l10nDirectoryURI) throws URISyntaxException, IOException {

		Map<Locale, LocalizationKeys> localizationKeys = loadAsMap();
		
		try {
			Constructor<? extends Localization> constructor = localizationClass.getConstructor(Map.class, String.class, String.class, Charset.class);
			return constructor.newInstance(localizationKeys, systemId, l10nDirectory, charset );
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<Locale, LocalizationKeys> loadAsMap(URI l10nDirectoryURI ) throws URISyntaxException, IOException {

		Map<Locale, LocalizationKeys> defaults = loadFiles(l10nDirectoryURI, true);
		Map<Locale, LocalizationKeys> locales = loadFiles(l10nDirectoryURI, false);

		locales.entrySet().forEach(e -> { if (defaults.containsKey(e.getKey())) { defaults.get(e.getKey()).combineWith(e.getValue()); } } );
		locales.entrySet().forEach(e -> defaults.putIfAbsent(e.getKey(), e.getValue() ) );

		return defaults;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map<String,String> propertiesAsMap(InputStream inputStream) throws IOException {
		
		Properties properties = new Properties();
		try (InputStreamReader reader = new InputStreamReader(inputStream, charset);
				BufferedReader buf = new BufferedReader(reader) ) {
			properties.load(buf);
		}
		
		return (Map)properties;
	}
	
	protected abstract Map<Locale, LocalizationKeys> loadFiles(URI l10nDirectoryURI, boolean defaults) throws IOException;
	
	protected Map<Locale, LocalizationKeys> loadMap(Path l10nDirectory, boolean defaults) throws IOException {
		
		Map<Locale, LocalizationKeys> trs = new HashMap<>();
		
		Files.walkFileTree(l10nDirectory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (dir.equals(l10nDirectory) ) return super.preVisitDirectory(dir, attrs);
				return FileVisitResult.SKIP_SUBTREE;
			}
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				
				String fileName = String.valueOf(file.getFileName());

				if (!defaults && fileName.startsWith(DEFAULT_FILE_PREFIX) ) return FileVisitResult.CONTINUE;
				if (defaults && !fileName.startsWith(DEFAULT_FILE_PREFIX) ) return FileVisitResult.CONTINUE;
				
				if (fileName.endsWith(".properties") ) {

					if (defaults) {
						fileName = fileName.substring(DEFAULT_FILE_PREFIX.length() );
					}

					String fileLanguageTag = fileName.substring(0, fileName.indexOf('.') );
					
					try (InputStream inputStream = getInputStream(file ) ) {
						try {
							LocalizationKeys trm = new LocalizationKeys(defaults ? null : systemId, fileLanguageTag, propertiesAsMap(inputStream ) );
							trs.put(trm.getLocale(), trm);
						} catch (LocalizationException | ELException e) {
							throw new IOException("Something wrong with file \"" + file.getFileName() + "\"", e);
						}
					}
				}
				
				return FileVisitResult.CONTINUE;
			}
		});
		
		return trs;
	}
	
	protected abstract InputStream getInputStream(Path file) throws IOException;
}
