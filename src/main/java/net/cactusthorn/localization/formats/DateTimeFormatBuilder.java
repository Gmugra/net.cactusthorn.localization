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
package net.cactusthorn.localization.formats;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DateTimeFormatBuilder {

    private static DateTimeFormatter createByPattern(Locale locale, FormatProperties formatProperties) {

        if (formatProperties != null && formatProperties.pattern != null) {
            return DateTimeFormatter.ofPattern(formatProperties.pattern, locale);
        }
        return null;
    }

    private static FormatStyle[] getStyles(FormatProperties formatProperties) {

        FormatStyle[] styles = new FormatStyle[] {FormatStyle.MEDIUM, FormatStyle.MEDIUM};
        if (formatProperties != null && formatProperties.dateStyle != null) {
            styles[0] = formatProperties.dateStyle;
        }
        if (formatProperties != null && formatProperties.timeStyle != null) {
            styles[1] = formatProperties.timeStyle;
        }
        return styles;
    }

    public static DateTimeFormatter buildDate(Locale locale, FormatProperties formatProperties) {

        DateTimeFormatter dtf = createByPattern(locale, formatProperties);
        if (dtf == null) {

            FormatStyle[] styles = getStyles(formatProperties);

            dtf = DateTimeFormatter.ofLocalizedDate(styles[0]).withLocale(locale);
        }
        return dtf;
    }

    public static DateTimeFormatter buildDateTime(Locale locale, FormatProperties formatProperties) {

        DateTimeFormatter dtf = createByPattern(locale, formatProperties);
        if (dtf == null) {

            FormatStyle[] styles = getStyles(formatProperties);

            dtf = DateTimeFormatter.ofLocalizedDateTime(styles[0], styles[1]).withLocale(locale);
        }
        return dtf;
    }

    public static DateTimeFormatter buildTime(Locale locale, FormatProperties formatProperties) {

        DateTimeFormatter dtf = createByPattern(locale, formatProperties);
        if (dtf == null) {

            FormatStyle[] styles = getStyles(formatProperties);

            dtf = DateTimeFormatter.ofLocalizedTime(styles[1]).withLocale(locale);
        }
        return dtf;
    }
}
