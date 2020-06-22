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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.cactusthorn.localization.LocalizationException;
import net.cactusthorn.localization.LocalizationKeyException;
import net.cactusthorn.localization.Parameter;
import net.cactusthorn.localization.formats.Formats;

public class LocalizationKeys {

    private Sys sys;
    private Formats formats;
    private Map<String, LocalizationKey> $keys = new HashMap<>();

    public LocalizationKeys(String systemId, String languageTag, Map<String, String> properties) throws LocalizationException {

        this.sys = new Sys(properties);

        if (!languageTag.equals(sys.languageTag())) {
            throw new LocalizationException(
                "Wrong value of " + Sys.TAG + "=" + sys.languageTag() + ", expected: " + Sys.TAG + "=" + languageTag);
        }

        if (systemId != null && !systemId.equals(sys.id())) {
            throw new LocalizationException("Wrong " + Sys.ID + "=" + sys.id() + ", expected: " + Sys.ID + "=" + systemId);
        }

        this.formats = new Formats(sys.locale(), properties);
        load(properties);
    }

    public void combineWith(LocalizationKeys keys) {

        sys.combineWith(keys.sys);
        formats.combineWith(keys.formats);

        keys.$keys.entrySet().forEach(e -> {
            if ($keys.containsKey(e.getKey())) {
                $keys.get(e.getKey()).combineWith(e.getValue());
            }
        });
        keys.$keys.entrySet().forEach(e -> $keys.putIfAbsent(e.getKey(), e.getValue()));
    }

    void addDefault(String key, String value, boolean escapeHtml) {
        if (!$keys.containsKey(key)) {
            $keys.put(key, new LocalizationKey(key));
        }
        $keys.get(key).setDefault(value, escapeHtml);
    }

    void addPluralSpecial(String key, int special, String value, boolean escapeHtml) {
        if (!$keys.containsKey(key)) {
            $keys.put(key, new LocalizationKey(key));
        }
        $keys.get(key).addPluralSpecial(special, value, escapeHtml);
    }

    void addPluralSpecial(String key, String special, String value, boolean escapeHtml) {
        addPluralSpecial(key, Integer.parseInt(special), value, escapeHtml);
    }

    void addPlural(String key, int plural, String value, boolean escapeHtml) {
        if (!$keys.containsKey(key)) {
            $keys.put(key, new LocalizationKey(key));
        }
        $keys.get(key).addPlural(plural, value, escapeHtml);
    }

    public void addPlural(String key, String plural, String value, boolean escapeHtml) {
        addPlural(key, Integer.parseInt(plural), value, escapeHtml);
    }

    public String get(String key) {
        return get(key, true, (Map<String, ?>) null);
    }

    public String get(String key, final Map<String, ?> params) {
        return get(key, true, params);
    }

    public String get(String key, Parameter<?>... parameters) {
        return get(key, true, Parameter.asMap(parameters));
    }

    public String get(String key, boolean withFormatting, Parameter<?>... parameters) {
        return get(key, withFormatting, Parameter.asMap(parameters));
    }

    public String get(String key, boolean withFormatting, final Map<String, ?> params) {
        LocalizationKey translation = $keys.get(key);
        if (translation == null) {
            throw new LocalizationKeyException(sys.locale(), "unavailable key: " + key);
        }
        return translation.get(sys, withFormatting ? formats : null, params);
    }

    public String getDefault(String key) {
        LocalizationKey translation = $keys.get(key);
        if (translation == null) {
            throw new LocalizationKeyException(sys.locale(), "unavailable key: " + key);
        }
        return translation.getDefault();
    }

    private static final String HTML_SUFFIX = "$html";

    private void load(Map<String, String> properties) {

        for (Map.Entry<String, String> e : properties.entrySet()) {

            String name = e.getKey();

            if (name.indexOf(Sys.SYSTEM_PREFIX) == 0 || name.indexOf(Formats.FORMAT_PREFIX) == 0) {
                continue;
            }

            String key = name;

            boolean escapeHtml = sys.escapeHtml();

            {
                int index = name.lastIndexOf(HTML_SUFFIX);
                if (index != -1 && index == name.length() - HTML_SUFFIX.length()) {

                    escapeHtml = false;
                    key = name.substring(0, index);
                }
            }

            int lastDot = key.lastIndexOf('.');

            if (lastDot == -1 || lastDot == key.length() - 1 || key.charAt(key.length() - 1) == '$') {

                addDefault(key, properties.get(name), escapeHtml);
                continue;
            }

            String firstPart = key.substring(0, lastDot);
            String lastPart = key.substring(lastDot + 1);

            if (lastPart.charAt(0) != '$' && isPositiveInteger(lastPart)) {

                addPluralSpecial(firstPart, Integer.parseInt(lastPart), properties.get(name), escapeHtml);
                continue;
            }

            if (lastPart.charAt(0) == '$') {

                String tmp = lastPart.substring(1);
                if (isPositiveInteger(tmp)) {
                    addPlural(firstPart, Integer.parseInt(tmp), properties.get(name), escapeHtml);
                } else {
                    addDefault(key, properties.get(name), escapeHtml);
                }
                continue;
            }

            addDefault(key, properties.get(name), escapeHtml);
        }
    }

    private static boolean isPositiveInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public Locale getLocale() {
        return sys.locale();
    }

    public String format(String formatName, Object obj) {
        return formats.format(formatName, obj);
    }
}
