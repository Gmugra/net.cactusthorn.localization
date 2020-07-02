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
import java.util.Optional;

import net.cactusthorn.localization.core.LocalizationKeys;

public abstract class AbstractLocalization implements Localization {

    protected final Map<Locale, LocalizationKeys> translations;
    protected final String systemId;
    protected final String l10nDirectory;

    protected AbstractLocalization(Map<Locale, LocalizationKeys> translations, String systemId, String l10nDirectory) {
        this.translations = translations;
        this.systemId = systemId;
        this.l10nDirectory = l10nDirectory;
    }

    @Override
    public Locale findNearest(Locale locale) {

        if (translations.containsKey(locale)) {
            return locale;
        }
        if (!"".equals(locale.getVariant())) {

            Optional<Locale> found = translations.keySet().stream()
                    .filter(l -> l.getLanguage().equals(locale.getLanguage()) && l.getCountry().equals(locale.getCountry())).findAny();

            if (found.isPresent()) {
                return found.get();
            }
        }

        Optional<Locale> found = translations.keySet().stream().filter(l -> l.getLanguage().equals(locale.getLanguage())).findAny();

        if (found.isPresent()) {
            return found.get();
        }

        return null;
    }
}
