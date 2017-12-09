package net.cactusthorn.localization;

import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.BeforeClass;
import org.junit.Rule;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import javax.script.ScriptException;

import static net.cactusthorn.localization.Parameter.of;

public class LoggingLocalizationTest {

	static LoggingLocalization localization;
	
	static Locale en_US = Locale.forLanguageTag("en-US");
	static Locale ru_RU = Locale.forLanguageTag("ru-RU");
	static Locale fr_FR = Locale.forLanguageTag("fr-FR");
	
	@BeforeClass
	public static void loadL10n() throws URISyntaxException, IOException, ScriptException {
		
		Path l10nDirectory = Paths.get(LocalizationTest.class.getClassLoader().getResource("L10n").toURI());
		localization = LoggingLocalization.load("test-app", l10nDirectory);
	}
	
	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests();
	
	@Test
	public void testWrongLanguageTag() throws URISyntaxException, IOException, ScriptException {
		
		String text = localization.get(fr_FR, "x.y.z.apple", of("count", 0) );
		
		assertThat(systemOutRule.getLog(), containsString("Locale: fr-FR, Unavailable locale"));
		
		assertEquals("Locale: fr-FR, Unavailable locale", text);
	}
}
