package net.cactusthorn.localization.formats;

import static net.cactusthorn.localization.formats.FormatType.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.Locale;

public class PercentFormatBuilder {

	private PercentFormatBuilder( ) {
		throw new UnsupportedOperationException("No chance to instantiate me.");
	}
	
	public static Format build(Locale locale) {
		FormatProperties formatProperties = new FormatProperties(PERCENT.toString().toLowerCase() );
		formatProperties.type = PERCENT;
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
		if (formatProperties.percent != '\u0000') {
			dfs.setPercent(formatProperties.percent );
		}
		
		DecimalFormat df;
		if (formatProperties.pattern == null) {
			df = (DecimalFormat) java.text.NumberFormat.getPercentInstance(locale );
		} else {
			df = new DecimalFormat(formatProperties.pattern);
		}
		
		df.setDecimalFormatSymbols(dfs);
		return df;
	}
}
