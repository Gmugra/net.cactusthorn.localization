package net.cactusthorn.localization;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.script.ScriptException;

public class TranslationTest {
	
	Sys sysEN;
	Sys sysRU;
	
	Translation trRU;
	
	@Before
	public void setUp() throws ScriptException {
		
		sysEN = new Sys("id", Locale.forLanguageTag("en-US"), 2, "(count!=1);", true);
		
		sysRU = new Sys("id", Locale.forLanguageTag("ru-RU"), 3, "(count%10==1 && count%100!=11 ? 0 : count%10>=2 && count%10<=4 && (count%100<10 || count%100>=20) ? 1 : 2);", true);
		
		trRU =
			new Translation(sysRU, "test.key")
			.setDefault("default text")
			.addPluralSpecial(0, "\u0412\u043E\u043E\u0431\u0449\u0435 \u043D\u0435\u0442 \u044F\u0431\u043B\u043E\u043A")
			.addPluralSpecial(1, "\u041E\u0434\u043D\u043E \u044F\u0431\u043B\u043E\u043A\u043E")
			.addPlural(0, "{{count}} \u044F\u0431\u043B\u043E\u043A\u043E")
			.addPlural(1, "{{count}} \u044F\u0431\u043B\u043E\u043A\u0430")
			.addPlural(2, "{{count}} \u044F\u0431\u043B\u043E\u043A");
	}
	
	@Test
	public void testSimple() throws ScriptException {
		
		Translation tr = new Translation(sysEN, "testSimple.key").setDefault("default message");
		
		assertEquals("default message", tr.get() );
	}
	
	@Test
	public void testEscapeHtml() throws ScriptException {
		
		Translation tr = new Translation(sysEN, "testSimple.key").setDefault("default <strong>&</strong> <br/> message");
		
		assertEquals("default &lt;strong&gt;&amp;&lt;/strong&gt; &lt;br/&gt; message", tr.get() );
	}
	
	@Test
	public void testNotEscapeHtml() throws ScriptException {
		
		Translation tr = new Translation(sysEN, "testSimple.key").setDefault("default <strong>&</strong> <br/> message", false);
		
		assertEquals("default <strong>&</strong> <br/> message", tr.get() );
	}

	@Test
	public void testSimpleMissingParam() throws ScriptException {
		
		Translation tr = new Translation(sysEN, "testSimpleMissingParam.key").setDefault("defaultX {{param1}} message {{param2}} ");
		
		Map<String,String> params = new HashMap<>();
		
		assertEquals("defaultX {{param1}} message {{param2}} ", tr.get(params) );
	}
	
	@Test
	public void testSimpleParam() throws ScriptException {
		
		Translation tr = new Translation(sysEN, "testSimpleParam.key").setDefault("default {{param1}} message {{param2}}");
		
		Map<String,String> params = new HashMap<>();
		params.put("param1", "AAA");
		params.put("param2", "BBB");
		
		assertEquals("default AAA message BBB", tr.get(params) );
	}
	
	@Test
	public void testSimpleSpecials() throws ScriptException {
		
		Translation tr = 
			new Translation(sysEN, "testSimpleSpecials.key")
			.setDefault("default text")
			.addPluralSpecial(7, "special text {{boom}} for 7!");
		
		Map<String,String> params = new HashMap<>();
		params.put("count", "7");
		
		assertEquals("special text {{boom}} for 7!", tr.get(params) );
		
		params.remove("count");
		
		assertEquals("default text", tr.get(params) );
	}
	
	@Test
	public void testSimplePlural() throws ScriptException {
		
		Translation tr = 
			new Translation(sysEN, "testSimplePlural.key")
			.setDefault("default text")
			.addPluralSpecial(7, "special text {{boom}} for 7!")
			.addPlural(0, "single")
			.addPlural(1, "not {{kaboom}} single");
		
		Map<String,String> params = new HashMap<>();
		params.put("count", "1");
		
		assertEquals("single", tr.get(params) );
		
		params.put("count", "3");
		
		assertEquals("not {{kaboom}} single", tr.get(params) );
		
		params.put("count", "7");
		params.put("boom", "BOOM");
		
		assertEquals("special text BOOM for 7!", tr.get(params) );
	}
	
	@Test
	public void testPluralRU() throws ScriptException {
		
		Map<String,String> params = new HashMap<>();
		params.put("count", "47563");
		
		assertEquals("47563 \u044F\u0431\u043B\u043E\u043A\u0430", trRU.get(params) );
	}
}
