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
package net.cactusthorn.localization.core;

import java.util.Locale;
import java.util.Map;

import net.cactusthorn.localization.LocalizationException;

class Sys {

    private String id;
    private Locale locale;
    private Boolean escapeHtml;
    private Plural plural;

    public static final String SYSTEM_PREFIX = "_system.";
    public static final String ID = SYSTEM_PREFIX + "id";
    public static final String TAG = SYSTEM_PREFIX + "languageTag";
    public static final String ESCAPE_HTML = SYSTEM_PREFIX + "escapeHtml";

    Sys(Map<String, String> properties) {
        // @formatter:off
        this(properties.get(ID),
             properties.get(TAG) == null ? null : Locale.forLanguageTag(properties.get(TAG)),
             Boolean.valueOf(properties.get(ESCAPE_HTML)));
        // @formatter:on
    }

    Sys(String id, Locale locale, Boolean escapeHtml) {

        if (locale == null) {
            throw new LocalizationException(TAG + " is required");
        }

        this.locale = locale;
        this.plural = Plural.of(locale);
        this.id = id;
        this.escapeHtml = escapeHtml;
    }

    void combineWith(Sys sys) {

        if (sys.id != null) {
            this.id = sys.id;
        }
        if (sys.locale != null) {
            this.locale = sys.locale;
        }
        if (sys.escapeHtml != null) {
            this.escapeHtml = sys.escapeHtml;
        }
    }

    int evalPlural(int count) {

        return plural.evalPlural(count);
    }

    String id() {
        return id;
    }

    Locale locale() {
        return locale;
    }

    String languageTag() {
        return locale.toLanguageTag();
    }

    boolean escapeHtml() {
        return escapeHtml;
    }

    @Override
    public String toString() {
        return "Sys(id=" + id + ", locale=" + locale + ", escapeHtml=" + escapeHtml + ")";
    }

}
