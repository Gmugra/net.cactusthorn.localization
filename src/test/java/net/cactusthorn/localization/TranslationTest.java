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
	public static void globalSetUp() throws URISyntaxException, IOException, ScriptException {
		
		Properties enUSProps = load("L10n/en-US.properties");
		sysEN = new Sys(enUSProps );
		formatsEN = new Formats(sysEN, enUSProps);
		
		Properties ruRUProps = load("L10n/ru-RU.properties");
		sysRU = new Sys(ruRUProps );
		formatsRU = new Formats(sysRU, ruRUProps);
		
		trRU =
			new Translation(sysRU, formatsRU, "test.key")
			.setDefault("default text")
			.addPluralSpecial(0, "\u0412\u043E\u043E\u0431\u0449\u0435 \u043D\u0435\u0442 \u044F\u0431\u043B\u043E\u043A")
			.addPluralSpecial(1, "\u041E\u0434\u043D\u043E \u044F\u0431\u043B\u043E\u043A\u043E")
			.addPlural(0, "{{count}} \u044F\u0431\u043B\u043E\u043A\u043E")
			.addPlural(1, "{{count}} \u044F\u0431\u043B\u043E\u043A\u0430")
			.addPlural(2, "{{count}} \u044F\u0431\u043B\u043E\u043A");
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
	public void testSimple() throws ScriptException {
		
		Translation tr = new Translation(sysEN, formatsEN, "testSimple.key").setDefault("default message");
		
		assertEquals("default message", tr.get() );
	}
	
	@Test
	public void testEscapeHtml() throws ScriptException {
		
		Translation tr = new Translation(sysEN, formatsEN, "testSimple.key").setDefault("default <strong>&</strong> <br/> message");
		
		assertEquals("default &lt;strong&gt;&amp;&lt;/strong&gt; &lt;br/&gt; message", tr.get() );
	}
	
	@Test
	public void testNotEscapeHtml() throws ScriptException {
		
		Translation tr = new Translation(sysEN, formatsEN, "testSimple.key").setDefault("default <strong>&</strong> <br/> message", false);
		
		assertEquals("default <strong>&</strong> <br/> message", tr.get() );
	}

	@Test
	public void testSimpleMissingParam() throws ScriptException {
		
		Translation tr = new Translation(sysEN, formatsEN, "testSimpleMissingParam.key").setDefault("defaultX {{param1}} message {{param2}} ");
		
		assertEquals("defaultX {{param1}} message {{param2}} ", tr.get(new HashMap<>() ) );
	}
	
	@Test
	public void testSimpleParam() throws ScriptException {
		
		Translation tr = new Translation(sysEN, formatsEN, "testSimpleParam.key").setDefault("default {{param1}} message {{param2}} XYZ");
		
		assertEquals("default AAA message BBB XYZ", tr.get(of("param1", "AAA"), of("param2", "BBB") ) );
	}
	
	@Test
	public void testSimpleSpecials() throws ScriptException {
		
		Translation tr = 
			new Translation(sysEN, formatsEN, "testSimpleSpecials.key")
			.setDefault("default text")
			.addPluralSpecial(7, "special text {{boom}} for 7!");
		
		assertEquals("special text {{boom}} for 7!", tr.get(of("count", 7) ) );
		
		assertEquals("default text", tr.get() );
	}
	
	@Test
	public void testSimplePlural() throws ScriptException {
		
		Translation tr = 
			new Translation(sysEN, formatsEN, "testSimplePlural.key")
			.setDefault("default text")
			.addPluralSpecial(7, "special text {{boom}} for 7!")
			.addPlural(0, "single")
			.addPlural(1, "not {{kaboom}} single");
		
		assertEquals("single", tr.get(of("count", 1) ) );
		
		assertEquals("not {{kaboom}} single", tr.get(of("count", 3 ) ) );
		
		assertEquals("special text BOOM for 7!", tr.get(of("count", 7 ), of("boom", "BOOM" )  ) );
	}
	
	@Test
	public void testPluralRU() throws ScriptException {
		
		assertEquals("47563 \u044F\u0431\u043B\u043E\u043A\u0430", trRU.get(of("count", 47563 ) ) );
	}
	
	@Test
	public void testWrongParameters1() throws ScriptException {
		
		Translation tr = new Translation(sysEN, formatsEN, "testSimpleParam.key").setDefault("default {{param1}} message {{param2 XYZ");
		
		assertEquals("default AAA message {{param2 XYZ", tr.get(of("param1", "AAA"), of("param2", "BBB") ) );
	}
	
	@Test
	public void testWrongParameters2() throws ScriptException {
		
		Translation tr = new Translation(sysEN, formatsEN, "testSimpleParam.key").setDefault("default param1}} message {{param2}} XYZ");
		
		assertEquals("default param1}} message BBB XYZ", tr.get(of("param1", "AAA"), of("param2", "BBB") ) );
	}
	
	@Test
	public void testWrongParameters3() throws ScriptException {
		
		Translation tr = new Translation(sysEN, formatsEN, "testSimpleParam.key").setDefault("default {{param1 message param2}} XYZ");
		
		assertEquals("default {{param1 message param2}} XYZ", tr.get(of("param1", "AAA"), of("param2", "BBB") ) );
	}
	
	@Test
	public void testWrongParameters4() throws ScriptException {
		
		Translation tr = new Translation(sysEN, formatsEN, "testSimpleParam.key").setDefault("but there are no parameters");
		
		assertEquals("but there are no parameters", tr.get(of("param1", "AAA"), of("param2", "BBB") ) );
	}
	
	@Test
	public void testWrongParameters5() throws ScriptException {
		
		Translation tr = new Translation(sysEN, formatsEN, "testSimpleParam.key").setDefault("default {{param1 message param2 XYZ");
		
		assertEquals("default {{param1 message param2 XYZ", tr.get(of("param1", "AAA"), of("param2", "BBB") ) );
	}
	
	
	@Test
	public void testFormatNumber() throws ScriptException {
		
		Translation tr = new Translation(sysEN, formatsEN, "testSimpleParam.key").setDefault("default {{param1,number}}");
		
		assertEquals("default AAA", tr.get(of("param1", "AAA"), of("param2", "BBB") ) );
	}
}
