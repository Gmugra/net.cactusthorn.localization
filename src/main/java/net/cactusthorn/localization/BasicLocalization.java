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

import java.util.Locale;
import java.util.Map;

import net.cactusthorn.localization.core.LocalizationKeys;

public class BasicLocalization extends AbstractLocalization {

    public BasicLocalization(Map<Locale, LocalizationKeys> translations, String systemId, String l10nDirectory) {
        super(translations, systemId, l10nDirectory);
    }

    @Override
    public String get(Locale locale, String key, boolean withFormatting, Map<String, ?> parameters) {

        Locale exists = findNearest(locale);

        if (exists == null) {

            throw new LocalizationLocaleException(locale, "Unavailable locale");
        }

        return translations.get(exists).get(key, withFormatting, parameters);
    }

    @Override
    public String getDefault(Locale locale, String key) {

        Locale exists = findNearest(locale);

        if (exists == null) {

            throw new LocalizationLocaleException(locale, "Unavailable locale");
        }

        return translations.get(exists).getDefault(key);
    }

    @Override
    public String format(Locale locale, String formatName, Object obj) {

        Locale exists = findNearest(locale);

        if (exists == null) {
            throw new LocalizationLocaleException(locale, "Unavailable locale");
        }

        return translations.get(exists).format(formatName, obj);
    }
}
