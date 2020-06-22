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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class WatchLoggingLocalizationTest extends WithLoggerTestAncestor {

    @Override
    protected Logger getLogger() {
        return (Logger) LoggerFactory.getLogger(WatchLoggingLocalization.class);
    }

    static Locale ru_RU = new Locale("ru", "RU");

    @TempDir
    Path l10nDirectory;

    @Test
    public void testCopyNew() throws URISyntaxException, IOException, InterruptedException {

        Localization localization = new PathLocalizationLoader("test-app").from(l10nDirectory.toString())
                .instanceOf(WatchLoggingLocalization.class).load();

        assertEquals("Locale: ru-RU, Unavailable locale", localization.get(ru_RU, "super.key"));

        copy(l10nDirectory, "L10n", "ru-RU.properties");

        Thread.sleep(1000); // give a bit time for WatchLoggingLocalization's thread to reload files

        assertEquals("\u0421\u0443\u043f\u0435\u0440", localization.get(ru_RU, "super.key"));

        ((WatchLoggingLocalization) localization).interrupt();
        ((WatchLoggingLocalization) localization).interrupt();
    }

    @Test
    public void testFail() throws URISyntaxException, IOException, InterruptedException {

        Localization localization = new PathLocalizationLoader("test-app").from(l10nDirectory.toString())
                .instanceOf(WatchLoggingLocalization.class).load();

        copy(l10nDirectory, "WrongLanguageTag", "fr-CA.properties");

        Thread.sleep(1000); // give a bit time for WatchLoggingLocalization's thread to reload files

        assertTrue(isMessageInLog(Level.ERROR, "reload localization is failed"));

        ((WatchLoggingLocalization) localization).interrupt();

    }

    private static void copy(Path l10nDirectory, String resourceDir, String file) throws URISyntaxException, IOException {

        Path pathRU = Paths.get(WatchLoggingLocalizationTest.class.getClassLoader().getResource(resourceDir + "/" + file).toURI());

        Files.copy(pathRU, Paths.get(l10nDirectory.toString(), file), StandardCopyOption.REPLACE_EXISTING);
    }

}
