package net.cactusthorn.localization.formats;

public enum FormatProperty {

    // @formatter:off
    TYPE("type"),
    PATTERN("pattern"),
    CURRENCY_SYMBOL("currencySymbol"),
    GROUPING_USED("groupingUsed"),
    GROUPING_SEPARATOR("groupingSeparator"),
    DECIMAL_SEPARATOR("decimalSeparator"),
    MONETARY_DECIMAL_SEPARATOR("monetaryDecimalSeparator"),
    PERCENT_SYMBOL("percentSymbol"),
    DATE_STYLE("dateStyle"),
    TIME_STYLE("timeStyle");
    // @formatter:on

    private final String property;

    FormatProperty(String property) {
        this.property = property;
    }

    public String property() {
        return property;
    }

    public static FormatProperty of(String property) {
        for (FormatProperty fp : FormatProperty.values()) {
            if (fp.property.equals(property)) {
                return fp;
            }
        }
        throw new IllegalArgumentException("No enum const " + FormatProperty.class + "@property." + property);
    }
}
