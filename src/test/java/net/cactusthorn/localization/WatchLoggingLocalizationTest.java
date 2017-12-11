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
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class WatchLoggingLocalizationTest {
	
	static Locale ru_RU = new Locale("ru","RU");
	
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();
	
	@Test
	public void testCopyNew() throws URISyntaxException, IOException, InterruptedException {
	
		Path l10nDirectory =  prepare();
		
		Localization localization  = new LocalizationLoader("test-app").setL10nDirectory(l10nDirectory).setClass(WatchLoggingLocalization.class).load();
		
		assertEquals("Locale: ru-RU, Unavailable locale", localization.get(ru_RU, "super.key") );
		
		copy(l10nDirectory, "L10n", "ru-RU.properties");
	
		Thread.sleep(1000); //give a bit time for WatchLoggingLocalization's thread to reload files
		
		assertEquals("\u0421\u0443\u043f\u0435\u0440",localization.get(ru_RU, "super.key"));
		
		((WatchLoggingLocalization)localization).interrupt();
		((WatchLoggingLocalization)localization).interrupt();
		
		deleteAll(l10nDirectory);
	}
	
	@Test
	public void testFail() throws URISyntaxException, IOException, InterruptedException {
	
		Path l10nDirectory =  prepare();
		
		Localization localization  = new LocalizationLoader("test-app").setL10nDirectory(l10nDirectory).setClass(WatchLoggingLocalization.class).load();
		
		copy(l10nDirectory,"WrongLanguageTag", "fr-CA.properties");
	
		Thread.sleep(1000); //give a bit time for WatchLoggingLocalization's thread to reload files
		
		assertThat(systemOutRule.getLog(), containsString("Fail to reload"));
		
		((WatchLoggingLocalization)localization).interrupt();
		
		deleteAll(l10nDirectory);
	}
	
	private static void copy(Path l10nDirectory, String resourceDir, String file ) throws URISyntaxException, IOException {
		
		Path pathRU = Paths.get(WatchLoggingLocalizationTest.class.getClassLoader().getResource(resourceDir +"/" + file).toURI());
		
		Files.copy(pathRU, Paths.get(l10nDirectory.toString(), file), StandardCopyOption.REPLACE_EXISTING );
	}
	
	private static Path prepare() throws URISyntaxException, IOException {
		
		Path path = Paths.get(WatchLoggingLocalizationTest.class.getClassLoader().getResource("").toURI());
		
		Path l10nDirectory = Paths.get(path.toString(), "TestWatch");
		
		if (!Files.exists(l10nDirectory)) {
			Files.createDirectories(l10nDirectory);
		} else {
			deleteAll(l10nDirectory);
			Files.createDirectories(l10nDirectory);
		}
		
		copy(l10nDirectory, "L10n", "en-US.properties");
		
		return l10nDirectory;
	}
	
	private static void deleteAll(Path l10nDirectory) throws IOException {
		
		Files.walkFileTree(l10nDirectory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				if (exc != null) {
					throw exc;
				}
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}
	
}
