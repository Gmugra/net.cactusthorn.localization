package net.cactusthorn.localization;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static net.cactusthorn.localization.Parameter.of;

public class LocalizationTest {
	
	static Localization localization; 
	static Locale en_US = Locale.forLanguageTag("en-US");
	static Locale ru_RU = Locale.forLanguageTag("ru-RU");
	static Locale fr_FR = Locale.forLanguageTag("fr-FR");
	
	@BeforeClass
	public static void load() throws IOException {
		localization = new LocalizationLoader("test-app").load(); 
	}
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void testNotDirectory() throws URISyntaxException, IOException {
		
		expectedException.expect(IOException.class);
		expectedException.expectMessage(containsString("is not directory"));

		
		Path path = Paths.get(LocalizationTest.class.getClassLoader().getResource("L10n/ru-RU.properties").toURI());
		new LocalizationLoader("test-app").setL10nDirectory(path).load();
	}
	
	@Test
	public void testWrongLanguageTag() throws URISyntaxException, IOException {
		
		expectedException.expect(IOException.class);
		expectedException.expectMessage("Something wrong with file \"fr-CA.properties\"");
		expectedException.expectCause(
			allOf(
				isA(LocalizationException.class),
				hasProperty("message", is("Wrong value of _system.languageTag=en-US, expected: _system.languageTag=fr-CA"))
			)
		);
		
		Path path = Paths.get(LocalizationTest.class.getClassLoader().getResource("WrongLanguageTag").toURI());
		new LocalizationLoader("test-app").setL10nDirectory(path).load();
	}
	
	@Test
	public void testWrongSystemId() throws URISyntaxException, IOException {
		
		expectedException.expect(IOException.class);
		expectedException.expectMessage("Something wrong with file \"fr-CA.properties\"");
		expectedException.expectCause(
			allOf(
				isA(LocalizationException.class),
				hasProperty("message", is("Wrong _system.id=test-app, expected: _system.id=my-super-app"))
			)
		);
		
		Path path = Paths.get(LocalizationTest.class.getClassLoader().getResource("WrongSystemId").toURI());
		new LocalizationLoader("my-super-app").setL10nDirectory(path).load();
	}
	
	@Test
	public void testKeys() {
		
		assertEquals("short key",localization.get(en_US, "super") );
		assertEquals("dotkey",localization.get(en_US, "super.") );
		assertEquals("dot dollar key",localization.get(en_US, "super.$") );
		assertEquals("dot <br/> value",localization.get(en_US, "superH.") );
		assertEquals("dollar &lt;br/&gt; but not plural",localization.get(en_US, "superT.$test") );
	}
	
	@Test
	public void testParametrs() {
		
		Map<String,String> params = new HashMap<>();
		params.put("first", "AAA");
		params.put("second", "BBB");
		
		assertEquals("first: AAA, second:BBB&lt;br/&gt;",localization.get(en_US, "test.param.first", params) );
		assertEquals("BBB, BBB, BBB; <strong>AAA, AAA, AAA;</strong>",localization.get(en_US, "test.param.second", params) );
	}
	
	@Test
	public void testParametrs2() {
		
		Map<String,String> params = new HashMap<>();
		params.put("first", "AAA");
		params.put("second", "BBB");
		
		assertEquals("first: AAA, second:BBB&lt;br/&gt;",localization.get(en_US, "test.param.first", params) );
		assertEquals("BBB, BBB, BBB; <strong>AAA, AAA, AAA;</strong>",localization.get(en_US, "test.param.second", params) );
	}
	
	@Test
	public void testApple() {
		
		assertEquals("apples by default", localization.get(en_US, "x.y.z.apple" ) );
		assertEquals("no any apples", localization.get(en_US, "x.y.z.apple", of("count", 0) ) );
		assertEquals("one apple", localization.get(en_US, "x.y.z.apple", of("count", 1) ) );
		assertEquals("special case:<br/> 22 apples", localization.get(en_US, "x.y.z.apple", of("count", 22) ) );
		assertEquals("33<br/> apples", localization.get(en_US, "x.y.z.apple", of("count", 33) ) );
	}
	
	@Test
	public void testFormat() {
		
		assertEquals("2,000.22", localization.format(en_US, "number", 2000.22f ));
		assertEquals("2 000,22", localization.format(ru_RU, "number", 2000.22f ));
	}
	
	@Test
	public void testWrongFormatNumber() {
		
		expectedException.expect(LocalizationFormatException.class);
		expectedException.expectMessage("Locale: en-US, format: \"number\", Unknown class for number formatting: java.util.Locale");
		
		localization.format(en_US, "number", fr_FR );
	}
	
	@Test
	public void testUnavailableLocale() {
		
		expectedException.expect(LocalizationLocaleException.class);
		expectedException.expectMessage("Locale: fr-FR, Unavailable locale");
		
		localization.get(fr_FR, "x.y.z.apple", of("count", 0) );
	}
	
	@Test
	public void testUnavailableKey() {
		
		expectedException.expect(LocalizationKeyException.class);
		expectedException.expectMessage("Locale: en-US, unavailable key: x.m.z.apple");
		
		localization.get(en_US, "x.m.z.apple", of("count", 0) );
	}
	
	@Test
	public void testWrongCount() {
		
		expectedException.expect(LocalizationException.class);
		expectedException.expectMessage("Locale: en-US, wrong value \"xxxx\" of {{count}} parameter for the key: x.y.z.apple");
		
		localization.get(en_US, "x.y.z.apple", of("count", "xxxx") );
	}
}
