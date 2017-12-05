package net.cactusthorn.localization;

import java.util.Locale;

public class LocalizationException extends RuntimeException {
	
	private static final long serialVersionUID = 0L;

	private Locale locale;
	
	public LocalizationException(Locale locale, String message, Throwable e) {
		super("Locale: " + locale.toLanguageTag() + ", " + message, e );
		this.locale = (Locale)locale.clone();
	}
	
	public LocalizationException(Locale locale, String message) {
		this(locale, message, null);
	}

	public Locale getLocale() {
		return locale;
	}

}
