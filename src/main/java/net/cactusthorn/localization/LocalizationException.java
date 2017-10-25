package net.cactusthorn.localization;

import java.util.Locale;

public class LocalizationException extends RuntimeException {
	
	private static final long serialVersionUID = 0L;

	private Locale locale;
	
	private Exception rootСause; 
	
	private static String LS = System.getProperty("line.separator");
	
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
