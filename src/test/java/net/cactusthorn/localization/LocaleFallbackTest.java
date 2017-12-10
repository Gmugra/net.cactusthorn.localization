package net.cactusthorn.localization;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Locale;

public class LocaleFallbackTest {

	static Localization localization;
	
	@BeforeClass
	public static void load() throws IOException {
		localization = new LocalizationLoader("test-app").load(); 
	}
	
	@Test
	public void checkOnlyLanguage() {
		
		Locale locale = new Locale("ru");
		
		assertEquals("ru_RU",localization.findNearest(locale).toString() );
	}
	
	@Test
	public void checkVariant() {
		
		Locale locale = new Locale("ru", "RU", "xyz");
		
		assertEquals("ru_RU",localization.findNearest(locale).toString() );
	}
	
	@Test
	public void checkExact() {
		
		Locale locale = new Locale("ru", "RU");
		
		assertEquals("ru_RU",localization.findNearest(locale).toString() );
	}
	
	@Test
	public void checkNotExists() {
		
		Locale locale = new Locale("fr");
		
		assertNull(localization.findNearest(locale));
	}
}
