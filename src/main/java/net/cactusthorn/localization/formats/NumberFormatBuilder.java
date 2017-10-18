package net.cactusthorn.localization.formats;

import static net.cactusthorn.localization.formats.FormatType.NUMBER;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.Locale;

public class NumberFormatBuilder {
	
	private NumberFormatBuilder( ) {
		throw new UnsupportedOperationException("No chance to instantiate me.");
	}
	
	public static Format build(Locale locale) {
		FormatProperties formatProperties = new FormatProperties(NUMBER.toString().toLowerCase() );
		formatProperties.type = NUMBER;
		return build(locale, formatProperties);
	}
	
	public static Format build(Locale locale, FormatProperties formatProperties) {
		
		DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(locale);
		
		if (formatProperties.decimalSeparator != '\u0000') {
			dfs.setDecimalSeparator(formatProperties.decimalSeparator );
		}
		if (formatProperties.groupingSeparator != '\u0000') {
			dfs.setGroupingSeparator(formatProperties.groupingSeparator );
		}
		
		DecimalFormat df;
		if (formatProperties.pattern == null) {
			df = (DecimalFormat) java.text.NumberFormat.getNumberInstance(locale );
		} else {
			df = new DecimalFormat(formatProperties.pattern);
		}
		
		df.setDecimalFormatSymbols(dfs);
		return df;
	}

}
