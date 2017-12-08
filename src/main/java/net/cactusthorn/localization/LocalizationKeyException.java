package net.cactusthorn.localization;

import java.util.Locale;

public class LocalizationKeyException extends LocalizationException {

	private static final long serialVersionUID = 0L;
	
	public LocalizationKeyException(Locale locale, String message, Throwable e) {
		super(locale, message, e);
	}

	public LocalizationKeyException(Locale locale, String message) {
		super(locale, message);
	}

	public LocalizationKeyException(String message, Throwable e) {
		super(message, e);
	}

	public LocalizationKeyException(String message) {
		super(message);
	}

}
