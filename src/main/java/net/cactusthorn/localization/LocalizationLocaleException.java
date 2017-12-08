package net.cactusthorn.localization;

import java.util.Locale;

public class LocalizationLocaleException extends LocalizationException {

	private static final long serialVersionUID = 0L;
	
	public LocalizationLocaleException(Locale locale, String message, Throwable e) {
		super(locale, message, e);
	}

	public LocalizationLocaleException(Locale locale, String message) {
		super(locale, message);
	}

	public LocalizationLocaleException(String message, Throwable e) {
		super(message, e);
	}

	public LocalizationLocaleException(String message) {
		super(message);
	}

}
