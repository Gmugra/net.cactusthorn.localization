package net.cactusthorn.localization.formats;

public enum FormatType {
	NUMBER,
	INTEGER,
	PERCENT,
	CURRENCY,
	DATE,
	TIME,
	DATETIME;
	
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
