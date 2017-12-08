package net.cactusthorn.localization;

import java.util.Locale;

public class LocalizationFormatException  extends LocalizationException {

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
