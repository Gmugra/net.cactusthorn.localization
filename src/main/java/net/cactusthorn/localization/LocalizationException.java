package net.cactusthorn.localization;

import java.util.Locale;

public class LocalizationException extends RuntimeException {
	
	private static final long serialVersionUID = 0L;

	public LocalizationException(Locale locale, String message, Throwable e) {
		super((locale != null ? "Locale: " + locale.toLanguageTag() + ", " : "" ) + message, e );
	}
	
	public LocalizationException(Locale locale, String message) {
		this(locale, message, null);
	}
	
	public LocalizationException(String message) {
		this(null, message, null);
	}
	
	public LocalizationException(String message, Throwable e) {
		this(null, message, e);
	}

}
