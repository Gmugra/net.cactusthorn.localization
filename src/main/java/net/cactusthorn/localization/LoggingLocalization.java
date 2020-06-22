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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.cactusthorn.localization.core.LocalizationKey;
import net.cactusthorn.localization.core.LocalizationKeys;

public class LoggingLocalization extends BasicLocalization {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LoggingLocalization.class);

    public LoggingLocalization(Map<Locale, LocalizationKeys> translations, String systemId, String l10nDirectory, Charset charset) {
        super(translations, systemId, l10nDirectory, charset);
    }

    @Override
    public String get(Locale locale, String key, boolean withFormatting, Map<String, ?> params) {

        try {

            String text = super.get(locale, key, withFormatting, params);
            logMissingParameters(locale, key, text);
            return text;
        } catch (LocalizationKeyException | LocalizationLocaleException e) {

            LOG.error("", e);
            return e.getMessage();
        } catch (LocalizationFormatException e) {

            LOG.error("", e);

            // LocalizationFormatException mean that correct key has bean found, but logic failed to format some parameter.
            // So, lets return found value without formatted parameters. Must work without exception.
            String text = super.get(locale, key, false, params);
            logMissingParameters(locale, key, text);
            return text;
        } catch (LocalizationException e) {

            LOG.error("", e);

            // LocalizationException at this moment mean that, before formatting, something wrong with parameters
            // So, lets return default message ASIS
            String text = super.getDefault(locale, key);
            return text.isEmpty() ? key + " : default text is undefined" : text;
        }
    }

    @Override
    public String format(Locale locale, String formatName, Object obj) {

        try {
            return super.format(locale, formatName, obj);
        } catch (LocalizationLocaleException e) {

            LOG.error("", e);
            return e.getMessage();
        } catch (LocalizationFormatException e) {

            LOG.error("", e);
            return obj.toString();
        }
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
