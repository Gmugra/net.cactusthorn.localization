package net.cactusthorn.localization;

import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.BeforeClass;

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
	static Locale enUS = Locale.forLanguageTag("en-US");
	
	@BeforeClass
	public static void globalSetUp() throws URISyntaxException, IOException, ScriptException {
		
		Path l10nDirectory = Paths.get(LocalizationTest.class.getClassLoader().getResource("L10n").toURI());
		localization = Localization.load(l10nDirectory);
	}
	
	@Test
	public void testKeys() throws ScriptException {
		
		assertEquals("short key",localization.getTranslation(enUS, "super") );
		assertEquals("dotkey",localization.getTranslation(enUS, "super.") );
		assertEquals("dot dollar key",localization.getTranslation(enUS, "super.$") );
		assertEquals("dot <br/> value",localization.getTranslation(enUS, "superH.") );
		assertEquals("dollar &lt;br/&gt; but not plural",localization.getTranslation(enUS, "superT.$test") );
	}
	
	@Test
	public void testParametrs() throws ScriptException {
		
		Map<String,String> params = new HashMap<>();
		params.put("first", "AAA");
		params.put("second", "BBB");
		
		assertEquals("first: AAA, second:BBB&lt;br/&gt;",localization.getTranslation(enUS, "test.param.first", params) );
		assertEquals("BBB, BBB, BBB; <strong>AAA, AAA, AAA;</strong>",localization.getTranslation(enUS, "test.param.second", params) );
	}
	
	@Test
	public void testApple() throws ScriptException {
		
		assertEquals("apples by default", localization.getTranslation(enUS, "x.y.z.apple" ) );
		assertEquals("no any apples", localization.getTranslation(enUS, "x.y.z.apple", of("count", 0) ) );
		assertEquals("one apple", localization.getTranslation(enUS, "x.y.z.apple", of("count", 1) ) );
		assertEquals("special case:<br/> 22 apples", localization.getTranslation(enUS, "x.y.z.apple", of("count", 22) ) );
		assertEquals("33<br/> apples", localization.getTranslation(enUS, "x.y.z.apple", of("count", 33) ) );
	}
}
