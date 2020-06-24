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

import net.cactusthorn.localization.core.Parameter;

public interface Localization {

    String get(Locale locale, String key, Parameter<?>... parameters);

    String get(Locale locale, String key, boolean withFormatting, Parameter<?>... parameters);

    String get(Locale locale, String key, Map<String, ?> parameters);

    String get(Locale locale, String key, boolean withFormatting, Map<String, ?> parameters);

    String getDefault(Locale locale, String key);

    String format(Locale locale, String formatName, Object obj);

    Locale findNearest(Locale locale);
}
