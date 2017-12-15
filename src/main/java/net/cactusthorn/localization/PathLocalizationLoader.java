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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

import net.cactusthorn.localization.core.LocalizationKeys;

public class PathLocalizationLoader extends AbstractLocalizationLoader {

	public PathLocalizationLoader(String systemId) {
		super(systemId);
	}
	
	public static Path l10nDirectoryToPath(String l10nDirectory) throws URISyntaxException {
		return Paths.get(l10nDirectoryToURI(l10nDirectory));
	}
	
	@Override
	protected Map<Locale, LocalizationKeys> loadFiles(URI l10nDirectoryURI, boolean defaults) throws IOException {
		
		Path l10nDirectoryPath = Paths.get(l10nDirectoryURI);
		
		if (!Files.isDirectory(l10nDirectoryPath ) ) {
			throw new IOException("l10nDirectory path " + l10nDirectory + " is not directory");
		}
		
		return loadMap(l10nDirectoryPath, defaults);
	}
	
	@Override
	protected InputStream getInputStream(Path file) throws IOException {
		return Files.newInputStream(file);
	}
}
