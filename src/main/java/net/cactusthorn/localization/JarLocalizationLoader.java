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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import net.cactusthorn.localization.core.LocalizationKeys;

public class JarLocalizationLoader extends AbstractLocalizationLoader {

	public JarLocalizationLoader(String systemId) {
		super(systemId);
	}
	
	@Override
	protected Map<Locale, LocalizationKeys> loadFiles(URI l10nDirectoryURI, boolean defaults) throws IOException {
		
		if (!"jar".equals(l10nDirectoryURI.getScheme() ) ) {
			throw new IOException("l10nDirectory path " + l10nDirectory + " is not found as jar resource");
		}
		
		try (FileSystem fileSystem = FileSystems.newFileSystem(l10nDirectoryURI, Collections.emptyMap() ) ) {
			return loadMap(Paths.get(l10nDirectoryURI ), defaults);
		}
	}
	
	@Override
	protected InputStream getInputStream(Path file) throws IOException {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(file.toString().substring(1) );
	}
	
}
