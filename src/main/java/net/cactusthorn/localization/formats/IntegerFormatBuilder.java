package net.cactusthorn.localization.formats;

import static net.cactusthorn.localization.formats.FormatType.*;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Locale;

public class IntegerFormatBuilder {
	
	private IntegerFormatBuilder( ) {
		throw new UnsupportedOperationException("No chance to instantiate me.");
	}
	
	public static Format build(Locale locale) {
		FormatProperties formatProperties = new FormatProperties(INTEGER.toString().toLowerCase() );
		formatProperties.type = INTEGER;
		return build(locale, formatProperties);
	}
	
	public static Format build(Locale locale, FormatProperties formatProperties) {
		
		DecimalFormat df;
		if (formatProperties.pattern == null) {
			df = (DecimalFormat) java.text.NumberFormat.getIntegerInstance(locale );
		} else {
			df = new DecimalFormat(formatProperties.pattern);
		}
		
		return df;
	}
}
