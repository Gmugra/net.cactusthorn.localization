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

public class LocalizationFormatException extends LocalizationException {

    private static final long serialVersionUID = 0L;

    public LocalizationFormatException(Locale locale, String message, Throwable e) {
        super(locale, message, e);
    }

    public LocalizationFormatException(Locale locale, String message) {
        super(locale, message);
    }

    public LocalizationFormatException(String message, Throwable e) {
        super(message, e);
    }

    public LocalizationFormatException(String message) {
        super(message);
    }

}
