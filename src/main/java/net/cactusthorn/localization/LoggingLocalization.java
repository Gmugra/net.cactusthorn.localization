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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.cactusthorn.localization.core.LocalizationKey;

public final class LoggingLocalization implements Localization {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LoggingLocalization.class);

    private Localization $localization;

    private LoggingLocalization(Localization localization) {
        $localization = localization;
    }

    public static class Builder extends AbstractLocalizationBuilder<LoggingLocalization> {

        private LocalizationBuilder<? extends Localization> $localizationBuilder;

        public Builder(LocalizationBuilder<? extends Localization> localizationBuilder) {
            $localizationBuilder = localizationBuilder;
        }

        @Override
        public LoggingLocalization build() {
            Localization localization = $localizationBuilder.withTranslations($translations).withSytsemId($systemId)
                    .withL10nDirectory($l10nDirectory).build();
            return new LoggingLocalization(localization);
        }

    }

    @Override
    public String get(Locale locale, String key, boolean withFormatting, Map<String, ?> parameters) {
        try {
            String text = $localization.get(locale, key, withFormatting, parameters);
            logMissingParameters(locale, key, text);
            return text;
        } catch (LocalizationFormatException e) {

            LOG.error("", e);

            // LocalizationFormatException mean that correct key has bean found, but logic failed to format some parameter.
            // So, lets return found value without formatted parameters. It must work without exception.
            String text = $localization.get(locale, key, false, parameters);
            logMissingParameters(locale, key, text);
            throw e;
        } catch (LocalizationException e) {
            LOG.error("", e);
            throw e;
        }
    }

    @Override
    public String getDefault(Locale locale, String key) {
        try {
            return $localization.getDefault(locale, key);
        } catch (LocalizationException e) {
            LOG.error("", e);
            throw e;
        }
    }

    @Override
    public String format(Locale locale, String formatName, Object obj) {
        try {
            return $localization.format(locale, formatName, obj);
        } catch (LocalizationException e) {
            LOG.error("", e);
            throw e;
        }
    }

    @Override
    public Locale findNearest(Locale locale) {
        return $localization.findNearest(locale);
    }

    private void logMissingParameters(Locale locale, String key, String text) {

        if (!LOG.isWarnEnabled()) {
            return;
        }

        List<String> missing = null;
        for (int startIndex = text.indexOf(LocalizationKey.PS); startIndex != -1;) {
            int endIndex = text.indexOf(LocalizationKey.PE, startIndex);
            if (endIndex == -1) {
                break;
            }
            if (missing == null) {
                missing = new ArrayList<>();
            }
            missing.add(text.substring(startIndex + LocalizationKey.PSL, endIndex));
            startIndex = text.indexOf(LocalizationKey.PS, endIndex + LocalizationKey.PEL);
        }

        if (missing != null) {
            LOG.warn("Locale: {}, not all parameters provided for key \"{}\", missing parameters: {}", locale.toLanguageTag(), key,
                    missing);
        }
    }
}
