package net.cactusthorn.localization.formats;

import java.util.Locale;

import net.cactusthorn.localization.LocalizationException;

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
