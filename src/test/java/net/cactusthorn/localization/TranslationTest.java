package net.cactusthorn.localization;

import org.junit.BeforeClass;
import org.junit.Test;

import net.cactusthorn.localization.formats.Formats;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

import javax.script.ScriptException;

import static net.cactusthorn.localization.Parameter.of;

public class TranslationTest {
	
	static Sys sysEN;
	static Formats formatsEN;
	
	static Sys sysRU;
	static Formats formatsRU;
	static Translation trRU;
	
	@BeforeClass
	public static void setUpEN() throws URISyntaxException, IOException, ScriptException {
		
		Properties props = load("L10n/en-US.properties");
		sysEN = new Sys(props );
		formatsEN = new Formats(sysEN.getLocale(), props);
	}
		
	@BeforeClass
	public static void setUpRU() throws URISyntaxException, IOException, ScriptException {
		
		Properties props = load("L10n/ru-RU.properties");
		sysRU = new Sys(props );
		formatsRU = new Formats(sysRU.getLocale(), props);	
	
		trRU =
			new Translation("test.key")
			.setDefault("default text", sysRU.isEscapeHtml() )
			.addPluralSpecial(0, "\u0412\u043E\u043E\u0431\u0449\u0435 \u043D\u0435\u0442 \u044F\u0431\u043B\u043E\u043A", sysRU.isEscapeHtml())
			.addPluralSpecial(1, "\u041E\u0434\u043D\u043E \u044F\u0431\u043B\u043E\u043A\u043E", sysRU.isEscapeHtml())
			.addPlural(0, "{{count}} \u044F\u0431\u043B\u043E\u043A\u043E", sysRU.isEscapeHtml())
			.addPlural(1, "{{count}} \u044F\u0431\u043B\u043E\u043A\u0430", sysRU.isEscapeHtml())
			.addPlural(2, "{{count}} \u044F\u0431\u043B\u043E\u043A", sysRU.isEscapeHtml());
	}
	
	private static Properties load(String resourceName) throws URISyntaxException, IOException {
		Properties properties = new Properties();
		Path path = Paths.get(LocalizationTest.class.getClassLoader().getResource(resourceName).toURI());		
		try (BufferedReader buf = Files.newBufferedReader(path, UTF_8 ) ) {
			properties.load(buf);
		}
		return properties;
	}
	
	@Test
	public void testCombineWith_1() throws URISyntaxException, IOException, ScriptException {
		
		Translation trOne = new Translation("testSimple.key").setDefault("default message one", sysEN.isEscapeHtml());
		Translation trTwo = new Translation("testSimple.key").setDefault("default message two", sysEN.isEscapeHtml());
		
		trOne.combineWith(trTwo);
		
		assertEquals("default message two", trOne.get(sysEN, formatsEN) );
	}
	
	@Test
	public void testCombineWith_2() throws URISyntaxException, IOException, ScriptException {
		
		Translation trOne = new Translation("testSimple.key").addPlural(0, "single", sysEN.isEscapeHtml());
		Translation trTwo = new Translation("testSimple.key").setDefault("default message two", sysEN.isEscapeHtml());
		
		trOne.combineWith(trTwo);
		
		assertEquals("single", trOne.get(sysEN, formatsEN, of("count", 1)) );
		
		trOne = new Translation("testSimple.key").setDefault("default message two", sysEN.isEscapeHtml());
		trTwo = new Translation("testSimple.key").addPlural(0, "single", sysEN.isEscapeHtml());
		
		trOne.combineWith(trTwo);
		
		assertEquals("single", trOne.get(sysEN, formatsEN, of("count", 1)) );
	}
	
	@Test
	public void testCombineWith_3() throws URISyntaxException, IOException, ScriptException {
		
		Translation trOne = new Translation("testSimple.key").addPluralSpecial(0, "single", sysEN.isEscapeHtml());
		Translation trTwo = new Translation("testSimple.key").setDefault("default message two", sysEN.isEscapeHtml());
		
		trOne.combineWith(trTwo);
		
		assertEquals("single", trOne.get(sysEN, formatsEN, of("count", 0)) );
		
		trOne = new Translation("testSimple.key").setDefault("default message two", sysEN.isEscapeHtml());
		trTwo = new Translation("testSimple.key").addPluralSpecial(0, "single", sysEN.isEscapeHtml());
		
		trOne.combineWith(trTwo);
		
		assertEquals("single", trOne.get(sysEN, formatsEN, of("count", 0)) );
	}
	
	@Test
	public void testSimple() throws ScriptException {
		
		Translation tr = new Translation("testSimple.key").setDefault("default message", sysEN.isEscapeHtml());
		
		assertEquals("default message", tr.get(sysEN, formatsEN) );
	}
	
	@Test
	public void testEscapeHtml() throws ScriptException {
		
		Translation tr = new Translation("testSimple.key").setDefault("default <strong>&</strong> <br/> message", sysEN.isEscapeHtml());
		
		assertEquals("default &lt;strong&gt;&amp;&lt;/strong&gt; &lt;br/&gt; message", tr.get(sysEN, formatsEN) );
	}
	
	@Test
	public void testNotEscapeHtml() throws ScriptException {
		
		Translation tr = new Translation("testSimple.key").setDefault("default <strong>&</strong> <br/> message", false);
		
		assertEquals("default <strong>&</strong> <br/> message", tr.get(sysEN, formatsEN) );
	}

	@Test
	public void testSimpleMissingParam() throws ScriptException {
		
		Translation tr = new Translation("testSimpleMissingParam.key").setDefault("defaultX {{param1}} message {{param2}} ", sysEN.isEscapeHtml());
		
		assertEquals("defaultX {{param1}} message {{param2}} ", tr.get(sysEN, formatsEN, new HashMap<>() ) );
	}
	
	@Test
	public void testSimpleParam() throws ScriptException {
		
		Translation tr = new Translation("testSimpleParam.key").setDefault("default {{param1}} message {{param2}} XYZ", sysEN.isEscapeHtml());
		
		assertEquals("default AAA message BBB XYZ", tr.get(sysEN, formatsEN, of("param1", "AAA"), of("param2", "BBB") ) );
	}
	
	@Test
	public void testSimpleSpecials() throws ScriptException {
		
		Translation tr = 
			new Translation("testSimpleSpecials.key")
			.setDefault("default text", sysEN.isEscapeHtml() )
			.addPluralSpecial(7, "special text {{boom}} for 7!", sysEN.isEscapeHtml());
		
		assertEquals("special text {{boom}} for 7!", tr.get(sysEN, formatsEN, of("count", 7) ) );
		
		assertEquals("default text", tr.get(sysEN, formatsEN) );
	}
	
	@Test
	public void testSimplePlural() throws ScriptException {
		
		Translation tr = 
			new Translation("testSimplePlural.key")
			.setDefault("default text", sysEN.isEscapeHtml())
			.addPluralSpecial(7, "special text {{boom}} for 7!", sysEN.isEscapeHtml())
			.addPlural(0, "single", sysEN.isEscapeHtml())
			.addPlural(1, "not {{kaboom}} single", sysEN.isEscapeHtml());
		
		assertEquals("single", tr.get(sysEN, formatsEN, of("count", 1) ) );
		
		assertEquals("not {{kaboom}} single", tr.get(sysEN, formatsEN, of("count", 3 ) ) );
		
		assertEquals("special text BOOM for 7!", tr.get(sysEN, formatsEN, of("count", 7 ), of("boom", "BOOM" )  ) );
	}
	
	@Test
	public void testPluralRU() throws ScriptException {
		
		assertEquals("47563 \u044F\u0431\u043B\u043E\u043A\u0430", trRU.get(sysRU, formatsRU, of("count", 47563 ) ) );
	}
	
	@Test
	public void testWrongParameters1() throws ScriptException {
		
		Translation tr = new Translation("testSimpleParam.key").setDefault("default {{param1}} message {{param2 XYZ", sysEN.isEscapeHtml());
		
		assertEquals("default AAA message {{param2 XYZ", tr.get(sysEN, formatsEN, of("param1", "AAA"), of("param2", "BBB") ) );
	}
	
	@Test
	public void testWrongParameters2() throws ScriptException {
		
		Translation tr = new Translation("testSimpleParam.key").setDefault("default param1}} message {{param2}} XYZ", sysEN.isEscapeHtml());
		
		assertEquals("default param1}} message BBB XYZ", tr.get(sysEN, formatsEN, of("param1", "AAA"), of("param2", "BBB") ) );
	}
	
	@Test
	public void testWrongParameters3() throws ScriptException {
		
		Translation tr = new Translation("testWrongParameters3.key").setDefault("default {{param1 message param2}} XYZ", sysEN.isEscapeHtml());
		
		assertEquals("default {{param1 message param2}} XYZ", tr.get(sysEN, formatsEN, of("param1", "AAA"), of("param2", "BBB") ) );
	}
	
	@Test
	public void testWrongParameters4() throws ScriptException {
		
		Translation tr = new Translation("testSimpleParam.key").setDefault("but there are no parameters", sysEN.isEscapeHtml());
		
		assertEquals("but there are no parameters", tr.get(sysEN, formatsEN, of("param1", "AAA"), of("param2", "BBB") ) );
	}
	
	@Test
	public void testWrongParameters5() throws ScriptException {
		
		Translation tr = new Translation("testSimpleParam.key").setDefault("default {{param1 message param2 XYZ", sysEN.isEscapeHtml());
		
		assertEquals("default {{param1 message param2 XYZ", tr.get(sysEN, formatsEN, of("param1", "AAA"), of("param2", "BBB") ) );
	}
	
	@Test
	public void testFormatNumber() throws ScriptException {
		
		Translation tr = new Translation("testSimpleParam.key").setDefault("default {{param1,number}}", sysEN.isEscapeHtml());
		
		assertEquals("default AAA", tr.get(sysEN, formatsEN, of("param1", "AAA") ) );
	}
	
	@Test
	public void testWrongFormatNumber() throws ScriptException {
		
		Translation tr = new Translation("testSimpleParam.key").setDefault("default {{param1,number}}", sysEN.isEscapeHtml());
		
		assertEquals("default Sys(id=test-app, locale=en_US, nplurals=2, pluralExpression=(count != 1);, escapeHtml=true)", tr.get(sysEN, formatsEN, of("param1", sysEN) ) );
	}
}
