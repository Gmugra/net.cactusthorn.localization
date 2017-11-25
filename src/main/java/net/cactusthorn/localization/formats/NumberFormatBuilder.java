package net.cactusthorn.localization.formats;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.Locale;

public class NumberFormatBuilder {
	
	private NumberFormatBuilder( ) {
		throw new UnsupportedOperationException("No chance to instantiate me.");
	}
	
	private static DecimalFormatSymbols createSymbols(Locale locale, FormatProperties formatProperties ) {
		
		DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(locale);
		if (formatProperties != null) {
			if (formatProperties.decimalSeparator != '\u0000') {
				dfs.setDecimalSeparator(formatProperties.decimalSeparator );
			}
			if (formatProperties.groupingSeparator != '\u0000') {
				dfs.setGroupingSeparator(formatProperties.groupingSeparator );
			}
			if (formatProperties.monetaryDecimalSeparator != '\u0000') {
				dfs.setMonetaryDecimalSeparator(formatProperties.monetaryDecimalSeparator );
			}
			if (formatProperties.currencySymbol != null) {
				dfs.setCurrencySymbol(formatProperties.currencySymbol );
			}
			if (formatProperties.percentSymbol != '\u0000') {
				dfs.setPercent(formatProperties.percentSymbol );
			}
		}
		return dfs;
	}
	
	private static void setGroupingUsed(DecimalFormat df, FormatProperties formatProperties ) {
		if (formatProperties == null) return;
		df.setGroupingUsed(formatProperties.groupingUsed);
	}
	
	private static DecimalFormat createByPattern(FormatProperties formatProperties ) {
		
		if (formatProperties != null && formatProperties.pattern != null) {
			return new DecimalFormat(formatProperties.pattern);
		}
		return null;
	}
	
	public static Format buildNumber(Locale locale, FormatProperties formatProperties) {
		
		DecimalFormat df = createByPattern(formatProperties );
		if (df == null) {
			df = (DecimalFormat) NumberFormat.getNumberInstance(locale );
		} 
		df.setDecimalFormatSymbols(createSymbols(locale, formatProperties ) );
		setGroupingUsed(df, formatProperties);
		return df;
	}
	
	public static Format buildInteger(Locale locale, FormatProperties formatProperties) {
		
		DecimalFormat df = createByPattern(formatProperties );
		if (df == null) {
			df = (DecimalFormat) NumberFormat.getIntegerInstance(locale );
		} 
		df.setDecimalFormatSymbols(createSymbols(locale, formatProperties ) );
		setGroupingUsed(df, formatProperties);
		return df;
	}
	
	public static Format buildCurrency(Locale locale, FormatProperties formatProperties) {
		
		DecimalFormat df = createByPattern(formatProperties );
		if (df == null) {
			df = (DecimalFormat) NumberFormat.getCurrencyInstance(locale );
		} 
		df.setDecimalFormatSymbols(createSymbols(locale, formatProperties ) );
		setGroupingUsed(df, formatProperties);
		return df;
	}
	
	public static Format buildPercent(Locale locale, FormatProperties formatProperties) {
		
		DecimalFormat df = createByPattern(formatProperties );
		if (df == null) {
			df = (DecimalFormat) NumberFormat.getPercentInstance(locale );
		} 
		df.setDecimalFormatSymbols(createSymbols(locale, formatProperties ) );
		setGroupingUsed(df, formatProperties);
		return df;
	}

}
