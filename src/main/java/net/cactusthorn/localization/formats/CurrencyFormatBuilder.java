package net.cactusthorn.localization.formats;

import static net.cactusthorn.localization.formats.FormatType.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.Locale;

public class CurrencyFormatBuilder {

	private CurrencyFormatBuilder( ) {
		throw new UnsupportedOperationException("No chance to instantiate me.");
	}
	
	public static Format build(Locale locale) {
		FormatProperties formatProperties = new FormatProperties(CURRENCY.toString().toLowerCase() );
		formatProperties.type = CURRENCY;
		return build(locale, formatProperties);
	}
	
	public static Format build(Locale locale, FormatProperties formatProperties) {
		
		DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(locale);
		
		if (formatProperties.groupingSeparator != '\u0000') {
			dfs.setGroupingSeparator(formatProperties.groupingSeparator );
		}
		if (formatProperties.monetaryDecimalSeparator != '\u0000') {
			dfs.setMonetaryDecimalSeparator(formatProperties.monetaryDecimalSeparator );
		}
		if (formatProperties.currencySymbol != null) {
			dfs.setCurrencySymbol(formatProperties.currencySymbol );
		}
		
		DecimalFormat df;
		if (formatProperties.pattern == null) {
			df = (DecimalFormat) java.text.NumberFormat.getCurrencyInstance(locale );
		} else {
			df = new DecimalFormat(formatProperties.pattern);
		}
		
		df.setDecimalFormatSymbols(dfs);
		return df;
	}
}
