package net.cactusthorn.localization;

import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.BeforeClass;
import org.junit.Rule;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Locale;

import static net.cactusthorn.localization.Parameter.of;

public class LoggingLocalizationTest {

	static Localization localization;
	
	static Locale en_US = Locale.forLanguageTag("en-US");
	static Locale fr_FR = Locale.forLanguageTag("fr-FR");
	
	@BeforeClass
	public static void loadL10n() throws IOException {
		
		localization = new LocalizationLoader("test-app").setClass(LoggingLocalization.class).load();
	}
	
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();
	
	@Test
	public void testUnavailableLocale() throws IOException {
		
		String text = localization.get(fr_FR, "x.y.z.apple", of("count", 0) );
		
		assertThat(systemOutRule.getLog(), containsString("Locale: fr-FR, Unavailable locale"));
		
		assertEquals("Locale: fr-FR, Unavailable locale", text);
	}
	
	@Test
	public void testUnavailableKey() {
		
		String text = localization.get(en_US, "x.m.z.apple", of("count", 0) );
		
		assertThat(systemOutRule.getLog(), containsString("Locale: en-US, unavailable key: x.m.z.apple"));
		
		assertEquals("Locale: en-US, unavailable key: x.m.z.apple", text);
	}
	
	@Test
	public void testWrongCount() {
		
		String text = localization.get(en_US, "x.y.z.apple", of("count", "xxxx") );
		
		assertThat(systemOutRule.getLog(), containsString("Locale: en-US, wrong value \"xxxx\" of {{count}} parameter for the key: x.y.z.apple"));
		
		assertEquals("apples by default", text);
	}
	
	@Test
	public void testWrongFormatNumber() {
		
		String text = localization.format(en_US, "number", fr_FR );
		
		assertThat(systemOutRule.getLog(), containsString("Locale: en-US, format: \"number\", Unknown class for number formatting: java.util.Locale"));
		
		assertEquals("fr_FR", text);
	}
	
	@Test
	public void testNull() {
		
		String text = localization.format(en_US, "number", null );
		
		assertEquals("null", text);
	}
}