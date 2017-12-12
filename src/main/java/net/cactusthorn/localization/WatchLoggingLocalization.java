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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import static java.nio.file.StandardWatchEventKinds.*;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import net.cactusthorn.localization.core.LocalizationKeys;

@Slf4j
//must be final, because start the thread in the constructor
public final class WatchLoggingLocalization extends LoggingLocalization implements Runnable {
	
	private final WatchService watchService = FileSystems.getDefault().newWatchService();
	
	private final Thread thread;
	
	private final LocalizationLoader loader;
	
	private final Map<Path,Long> timeStamps = new HashMap<>();
	
	public WatchLoggingLocalization(Map<Locale, LocalizationKeys> translations, String systemId, Path l10nDirectory, Charset charset) throws IOException {
		
		super(new ConcurrentHashMap<>(translations), systemId, l10nDirectory, charset);
		
		l10nDirectory.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);
		
		loader = new LocalizationLoader(this.systemId).setL10nDirectory(this.l10nDirectory).setCharset(charset);
		
		Files.walkFileTree(l10nDirectory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				timeStamps.put(file, file.toFile().lastModified());
				return FileVisitResult.CONTINUE;
			}
		});
		
		thread = new Thread(this);
		thread.setName("WatchLoggingLocalization:" + thread.getName());
		thread.start();
	}
	
	public void interrupt() {
		thread.interrupt();
	}
	
	@Override
	public void run() {

		log.info("Watch directory: {}", l10nDirectory.toString());
		
		while (true) {

			// wait for key to be signaled
			WatchKey key;
			try {
				key = watchService.take();
			} catch (InterruptedException ie) {
				log.warn("WatchLoggingLocalization thread Interrupted.");
				return;
			}
			
			key.pollEvents().stream().filter(this::checkWatchEvent).filter(this::checkFile).forEach(this::reload);

			// reset the key - this step is critical if you want to receive further watch events. 
			// If the key is no longer valid, the directory is inaccessible so exit the loop.             
			if (!key.reset()) {
				log.error("WatchKey is not longer valid!");
				return;
			}
		}
	}
	
	private boolean checkWatchEvent(WatchEvent<?> event) {
		
		WatchEvent.Kind<?> kind = event.kind();
		
		// This key is registered only for ENTRY_CREATE & ENTRY_MODIFY events,
		// but an OVERFLOW event can occur regardless if events are lost or discarded.
		return !OVERFLOW.equals(kind) && (ENTRY_CREATE.equals(kind) || ENTRY_MODIFY.equals(kind));
	}
	
	private boolean checkFile(WatchEvent<?> event) {
		
		String fileName = ((Path)event.context()).getFileName().toString();
		
		//some temporary files(e.g. rsync) started from ".", -> ignore them
		if (fileName.indexOf('.') == 0 || !fileName.endsWith(".properties") ) return false;
	
		//is it directory or not, size and lastModified we will know only after resolve
		Path resolvedPath = l10nDirectory.resolve((Path) event.context());
		File resolvedFile = resolvedPath.toFile();
		
		if (resolvedFile.isDirectory() ) return false;
		
		//File with 0(zero) size. Empty file, YES.
		//Events according such files(not directories!) sometimes happens.
		//May be reason is Windows?
		//Anyway - such files must be ignored.
		if (resolvedFile.length() == 0) return false;

		//multiple event because of same file change happens sometimes
		//lets check that lastModified is new to avoid unnecessary work 		
		Long oldTime = timeStamps.get(resolvedPath);
		long newTime = resolvedFile.lastModified();
		if (oldTime == null ) {
			timeStamps.put(resolvedPath, resolvedFile.lastModified());
		} else if (newTime > oldTime ) {
			timeStamps.put(resolvedPath, resolvedFile.lastModified());
		} else {
			log.debug("event for the file: {} skipped, because lastModified is same as before", fileName);
			return false;
		}

		return true;
	}
	
	private void reload(WatchEvent<?> event) {
		
		String fileName = ((Path)event.context()).getFileName().toString();
		
		Map<Locale, LocalizationKeys> localizationKeys;
		try {
			localizationKeys = loader.loadAsMap();
		} catch (Exception e) {
			log.error("reload localization is failed", e );
			return;
		}
		localizationKeys.entrySet().forEach(e -> translations.put(e.getKey(), e.getValue() ) );
		log.info("localization was reloaded because the file {} has been changed", fileName );
	}
}
