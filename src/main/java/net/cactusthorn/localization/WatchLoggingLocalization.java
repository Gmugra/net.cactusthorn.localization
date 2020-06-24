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
import java.net.URISyntaxException;
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

import net.cactusthorn.localization.core.LocalizationKeys;

//must be final, because can start the thread in the constructor
public final class WatchLoggingLocalization extends LoggingLocalization implements Runnable {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WatchLoggingLocalization.class);

    private final Map<Path, Long> timeStamps = new HashMap<>();

    private final WatchService watchService = FileSystems.getDefault().newWatchService();

    private final Thread thread;

    private final LocalizationLoader loader;

    private final Path l10nDirectoryPath;

    public WatchLoggingLocalization(Map<Locale, LocalizationKeys> translations, String systemId, String l10nDirectory)
            throws IOException, URISyntaxException {

        super(new ConcurrentHashMap<>(translations), systemId, l10nDirectory);

        l10nDirectoryPath = PathLocalizationLoader.l10nDirectoryToPath(l10nDirectory);

        l10nDirectoryPath.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);

        loader = new PathLocalizationLoader(this.systemId).from(this.l10nDirectory);

        Files.walkFileTree(l10nDirectoryPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.equals(l10nDirectoryPath)) {
                    return super.preVisitDirectory(dir, attrs);
                }
                return FileVisitResult.SKIP_SUBTREE;
            }

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
        if (l10nDirectory != null) {
            thread.interrupt();
        }
    }

    @Override
    public void run() {

        LOG.info("Watch directory: {}", l10nDirectory);

        while (true) {

            // wait for key to be signaled
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException ie) {
                LOG.warn("WatchLoggingLocalization thread Interrupted.");
                return;
            }

            key.pollEvents().stream().filter(this::checkWatchEvent).filter(this::checkFile).forEach(this::reload);

            // reset the key - this step is critical if you want to receive further watch
            // events. 
            // If the key is no longer valid, the directory is inaccessible so exit the
            // loop.             
            if (!key.reset()) {
                LOG.error("WatchKey is not longer valid!");
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

        String fileName = String.valueOf(((Path) event.context()).getFileName());

        // some temporary files(e.g. rsync) started from ".", -> ignore them
        if (fileName.indexOf('.') == 0 || !fileName.endsWith(".properties")) {
            return false;
        }

        // is it directory or not, size and lastModified we will know only after resolve
        Path resolvedPath = l10nDirectoryPath.resolve((Path) event.context());
        File resolvedFile = resolvedPath.toFile();

        if (resolvedFile.isDirectory()) {
            return false;
        }

        // File with 0(zero) size. Empty file, YES.
        // Events according such files(not directories!) sometimes happens.
        // May be reason is Windows?
        // Anyway - such files must be ignored.
        if (resolvedFile.length() == 0) {
            return false;
        }

        // multiple event because of same file change happens sometimes
        // lets check that lastModified is new to avoid unnecessary work
        Long oldTime = timeStamps.get(resolvedPath);
        long newTime = resolvedFile.lastModified();
        if (oldTime == null) {
            LOG.debug("event for the file: {} not skipped, because oldTime is null", fileName);
            timeStamps.put(resolvedPath, newTime);
        } else if (newTime != oldTime) {
            LOG.debug("event for the file: {} not skipped, because newTime({}) != oldTime({})", fileName, newTime, oldTime);
            timeStamps.put(resolvedPath, newTime);
        } else {
            LOG.debug("event for the file: {} skipped, because lastModified is same as before", fileName);
            return false;
        }

        return true;
    }

    private void reload(WatchEvent<?> event) {

        String fileName = String.valueOf(((Path) event.context()).getFileName());

        Map<Locale, LocalizationKeys> localizationKeys;
        try {
            localizationKeys = loader.loadAsMap();
        } catch (Exception e) {
            LOG.error("reload localization is failed", e);
            return;
        }
        localizationKeys.entrySet().forEach(e -> translations.put(e.getKey(), e.getValue()));
        LOG.info("localization was reloaded because the file {} has been changed", fileName);
    }
}
