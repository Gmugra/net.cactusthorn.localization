package net.cactusthorn.localization;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.script.ScriptException;

import static net.cactusthorn.localization.Parameter.of;

public class LocalizationTest {
	
	static Localization localization;
	static Locale en_US = Locale.forLanguageTag("en-US");
	static Locale ru_RU = Locale.forLanguageTag("ru-RU");
	
	@BeforeClass
	public static void loadL10n() throws URISyntaxException, IOException, ScriptException {
		
		Path l10nDirectory = Paths.get(LocalizationTest.class.getClassLoader().getResource("L10n").toURI());
		localization = Localization.load("test-app", l10nDirectory);
	}
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void testNotDirectory() throws URISyntaxException, IOException, ScriptException {
		
		expectedException.expect(IOException.class);
		expectedException.expectMessage("l10nDirectory Path is not Directory.");
		
		Path path = Paths.get(LocalizationTest.class.getClassLoader().getResource("L10n/ru-RU.properties").toURI());
		Localization.load("test-app", path);
	}
	
	@Test
	public void testWrongLanguageTag() throws URISyntaxException, IOException, ScriptException {
		
		expectedException.expect(LocalizationException.class);
		expectedException.expectMessage("Localization file \"fr-CA.properties\", the file name do not fit _system.languageTag=en-US");
		
		Path path = Paths.get(LocalizationTest.class.getClassLoader().getResource("WrongLanguageTag").toURI());
		Localization.load("test-app", path);
	}
	
	@Test
	public void testWrongSystemId() throws URISyntaxException, IOException, ScriptException {
		
		expectedException.expect(LocalizationException.class);
		expectedException.expectMessage("Localization file \"fr-CA.properties\", wrong _system.id=test-app, expected: _system.id=my-super-app");
		
		Path path = Paths.get(LocalizationTest.class.getClassLoader().getResource("WrongSystemId").toURI());
		Localization.load("my-super-app", path);
	}
	
	@Test
	public void testKeys() throws ScriptException {
		
		assertEquals("short key",localization.getTranslation(en_US, "super") );
		assertEquals("dotkey",localization.getTranslation(en_US, "super.") );
		assertEquals("dot dollar key",localization.getTranslation(en_US, "super.$") );
		assertEquals("dot <br/> value",localization.getTranslation(en_US, "superH.") );
		assertEquals("dollar &lt;br/&gt; but not plural",localization.getTranslation(en_US, "superT.$test") );
	}
	
	@Test
	public void testParametrs() throws ScriptException {
		
		Map<String,String> params = new HashMap<>();
		params.put("first", "AAA");
		params.put("second", "BBB");
		
		assertEquals("first: AAA, second:BBB&lt;br/&gt;",localization.getTranslation(en_US, "test.param.first", params) );
		assertEquals("BBB, BBB, BBB; <strong>AAA, AAA, AAA;</strong>",localization.getTranslation(en_US, "test.param.second", params) );
	}
	
	@Test
	public void testParametrs2() throws ScriptException {
		
		Map<String,String> params = new HashMap<>();
		params.put("first", "AAA");
		params.put("second", "BBB");
		
		assertEquals("first: AAA, second:BBB&lt;br/&gt;",localization.getTranslation(en_US, "test.param.first", params) );
		assertEquals("BBB, BBB, BBB; <strong>AAA, AAA, AAA;</strong>",localization.getTranslation(en_US, "test.param.second", params) );
	}
	
	@Test
	public void testApple() throws ScriptException {
		
		assertEquals("apples by default", localization.getTranslation(en_US, "x.y.z.apple" ) );
		assertEquals("no any apples", localization.getTranslation(en_US, "x.y.z.apple", of("count", 0) ) );
		assertEquals("one apple", localization.getTranslation(en_US, "x.y.z.apple", of("count", 1) ) );
		assertEquals("special case:<br/> 22 apples", localization.getTranslation(en_US, "x.y.z.apple", of("count", 22) ) );
		assertEquals("33<br/> apples", localization.getTranslation(en_US, "x.y.z.apple", of("count", 33) ) );
	}
	
	@Test
	public void testFormat() throws ScriptException {
		
		assertEquals("2,000.22", localization.getFormats(en_US).format("number", 2000.22f ));
		assertEquals("2 000,22", localization.getFormats(ru_RU).format("number", 2000.22f ));
	}
}
