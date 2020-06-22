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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

/**
 * Variation of initialization-on-demand holder idiom
 * https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
 *
 * Not good according to true OOD. Singletons are evil. In any variations
 * ("Double checked locking" pattern is not better).
 *
 * On top of that, keep in mind that construction of InstanceHolder CAN throw
 * exception. So, not clean "initialization-on-demand".
 *
 * However it is working.
 *
 * Thus, usage on your own risk. but better avoid it and use LocalizationLoader directly.
 */

public final class L10n implements Localization {

    private Localization localization;

    private L10n(String systemId, String l10nDirectory, Charset charset, Class<? extends AbstractLocalization> localizationClass)
            throws IOException, URISyntaxException {

        localization = new PathLocalizationLoader(systemId).from(l10nDirectory).encoded(charset).instanceOf(localizationClass).load();
    }

    private static String $systemId;
    private static String $l10nDirectory;
    private static Charset $charset;
    private static Class<? extends AbstractLocalization> $localizationClass;

    private static final class InstanceHolder {

        private static final L10n INSTANCE = initLocalizationHolder();

        private InstanceHolder() {
        }

        private static L10n initLocalizationHolder() {

            try {
                return new L10n($systemId, $l10nDirectory, $charset, $localizationClass);
            } catch (URISyntaxException | IOException e) {

                // a static initializer cannot throw exceptions but it can throw an ExceptionInInitializerError
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    /**
     * The method theOnlyAttemptToInitInstance must be called before anything else
     * from this class. Compromise for practical usage...
     */
    public static L10n theOnlyAttemptToInitInstance(String systemId, String l10nDirectory,
            Class<? extends AbstractLocalization> localizationClass) {
        return L10n.theOnlyAttemptToInitInstance(systemId, l10nDirectory, localizationClass, UTF_8);
    }

    public static L10n theOnlyAttemptToInitInstance(String systemId, String l10nDirectory,
            Class<? extends AbstractLocalization> localizationClass, Charset charset) {

        L10n.$systemId = systemId;
        L10n.$l10nDirectory = l10nDirectory;
        L10n.$charset = charset;
        L10n.$localizationClass = localizationClass;

        L10n instance;

        try {
            instance = InstanceHolder.INSTANCE;
        } catch (ExceptionInInitializerError e) {
            Throwable exceptionInInit = e.getCause();
            throw new RuntimeException(new IOException(exceptionInInit.getMessage(), exceptionInInit.getCause()));
        }

        return instance;
    }

    /**
     * This method must be NEVER called before theOnlyAttemptToInitInstance call...
     * compromise for practical usage...
     */
    public static L10n instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String get(Locale locale, String key, Parameter<?>... parameters) {
        return localization.get(locale, key, parameters);
    }

    @Override
    public String get(Locale locale, String key, boolean withFormatting, Parameter<?>... parameters) {
        return localization.get(locale, key, withFormatting, parameters);
    }

    @Override
    public String get(Locale locale, String key, Map<String, ?> parameters) {
        return localization.get(locale, key, parameters);
    }

    @Override
    public String get(Locale locale, String key, boolean withFormatting, Map<String, ?> parameters) {
        return localization.get(locale, key, withFormatting, parameters);
    }

    @Override
    public String getDefault(Locale locale, String key) {
        return localization.getDefault(locale, key);
    }

    @Override
    public String format(Locale locale, String formatName, Object obj) {
        return localization.format(locale, formatName, obj);
    }

    @Override
    public Locale findNearest(Locale locale) {
        return localization.findNearest(locale);
    }
}
