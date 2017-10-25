package net.cactusthorn.localization;

import java.util.Locale;
import java.util.Properties;

import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.Compilable;
import javax.script.Bindings;

public class Sys {
	
	private CompiledScript pluralScript;

	private String id;
	private Locale locale;
	private int nplurals;
	private String pluralExpression;
	private boolean escapeHtml;
	
	Sys(Properties properties ) throws ScriptException {
		
		this(
			properties.getProperty("_system.id"),
			Locale.forLanguageTag(properties.getProperty("_system.languageTag") ),
			Integer.parseInt(properties.getProperty("_system.nplurals")),
			properties.getProperty("_system.plural"),
			Boolean.parseBoolean(properties.getProperty("_system.escapeHtml" ) ) );
	}
	
	Sys(String id, Locale locale, int nplurals, String pluralExpression, boolean escapeHtml) throws ScriptException {
	
		this.id = id;
		this.locale = locale;
		this.nplurals = nplurals;
		this.escapeHtml = escapeHtml;
		this.pluralExpression = pluralExpression;
		
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		pluralScript = ((Compilable)engine).compile(pluralExpression);
	}
	
	int evalPlural(int count) throws ScriptException {
		
		Bindings bindings = new SimpleBindings();
	    bindings.put("count", count);
		
	    Object res = pluralScript.eval(bindings);
	    if (res instanceof Boolean) {
	    	return (Boolean)res?1:0;
	    }
		return (int)res;
	}
	
	public String getId() {
		return id;
	}

	public Locale getLocale() {
		return locale;
	}
	
	public String localeToLanguageTag() {
		return locale.toLanguageTag();
	}

	public boolean isEscapeHtml() {
		return escapeHtml;
	}

	@Override
	public String toString() {
		return "{ id="+id+", locale="+locale.toLanguageTag()+", escapeHtml"+escapeHtml+", nplurals="+nplurals+", pluralExpression="+pluralExpression+" }";
	}
}
