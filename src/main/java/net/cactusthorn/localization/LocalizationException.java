package net.cactusthorn.localization;

import java.util.Locale;

public class LocalizationException extends RuntimeException {
	
	private static final long serialVersionUID = 0L;

	private static final String LS = System.lineSeparator();
	
	private Locale locale;
	
	private Exception rootСause; 
	
	public LocalizationException(Locale locale, String message, Exception e) {
		super("Locale: " + locale.toLanguageTag() + ", " + message + (e == null?"":LS + '\t' + e.toString() ));
		this.locale = locale;
		if (e != null ) {
			this.setStackTrace(e.getStackTrace());
			rootСause = e;
		}
	}
	
	public LocalizationException(Locale locale, String message) {
		this(locale,message, null);
	}

	public Locale getLocale() {
		return locale;
	}
	
	public Exception getRootСause() {
		return rootСause;
	}

}
