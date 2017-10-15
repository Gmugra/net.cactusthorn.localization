package net.cactusthorn.localization;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Locale;

import javax.script.ScriptException;

public class SysTest {

	@Test
	public void testPluralExpressionEN() throws ScriptException  {
		
		Sys sys = new Sys("id", Locale.ENGLISH, 2, "(count!=1);", true);
		
		assertEquals(1, sys.evalPlural(0));
		assertEquals(0, sys.evalPlural(1));
		assertEquals(1, sys.evalPlural(22));
	}
	
	@Test
	public void testPluralExpressionRU() throws ScriptException  {
		
		Sys sys = new Sys("id", new Locale("ru","ru"), 3, 
				"(count%10==1 && count%100!=11 ? 0 : count%10>=2 && count%10<=4 && (count%100<10 || count%100>=20) ? 1 : 2);", true);
		
		assertEquals(2, sys.evalPlural(0));
		assertEquals(0, sys.evalPlural(1));
		assertEquals(2, sys.evalPlural(100));
		assertEquals(1, sys.evalPlural(3));
	}
}
