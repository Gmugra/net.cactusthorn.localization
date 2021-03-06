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
import java.util.Locale;
import java.util.Map;

import net.cactusthorn.localization.core.LocalizationKeys;
import net.cactusthorn.localization.fileloader.FileLoader;

public interface LocalizationLoader {

    String DEFAULT_FILE_PREFIX = "default.";

    String DEFAULT_DIRECTORY = "L10n";

    LocalizationLoader withLocalizationBuilder(LocalizationBuilder<? extends Localization> localizationBuilder);

    LocalizationLoader from(String l10nDirectory);

    LocalizationLoader withFileLoader(FileLoader fileLoader);

    Localization load() throws URISyntaxException, IOException;

    Map<Locale, LocalizationKeys> loadAsMap() throws URISyntaxException, IOException;
}
