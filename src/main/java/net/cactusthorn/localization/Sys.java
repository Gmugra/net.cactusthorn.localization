package net.cactusthorn.localization;

import java.util.Locale;
import java.util.Properties;

import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import lombok.ToString;

import javax.script.Compilable;
import javax.script.Bindings;

@ToString(exclude="pluralScript")
public class Sys {
	
	private CompiledScript pluralScript;

	private String id;
	private Locale locale;
	private Integer nplurals;
	private String pluralExpression;
	private Boolean escapeHtml;
	
	Sys(Properties properties ) throws LocalizationException, ScriptException {
		
		this(
			properties.getProperty("_system.id"),
			properties.getProperty("_system.languageTag") == null ? null : Locale.forLanguageTag(properties.getProperty("_system.languageTag") ),
			properties.getProperty("_system.nplurals") == null ? null :new Integer(properties.getProperty("_system.nplurals")),
			properties.getProperty("_system.plural"),
			new Boolean(properties.getProperty("_system.escapeHtml" ) ) );
	}
	
	Sys(String id, Locale locale, Integer nplurals, String pluralExpression, Boolean escapeHtml) throws LocalizationException, ScriptException {
	
		if (locale == null ) {
			throw new LocalizationException("_system.languageTag is required" );
		}
		
		this.locale = locale;
		this.id = id;
		this.nplurals = nplurals;
		this.escapeHtml = escapeHtml;
		this.pluralExpression = pluralExpression;
		
		if (pluralExpression != null) {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
			pluralScript = ((Compilable)engine).compile(pluralExpression);
		}
	}
	
	void combineWith(Sys sys) {
		
		if (sys.id != null ) this.id = sys.id;
		if (sys.locale != null ) this.locale = sys.locale;
		if (sys.nplurals != null ) this.nplurals = sys.nplurals;
		if (sys.escapeHtml != null ) this.escapeHtml = sys.escapeHtml;
		if (sys.pluralExpression != null ) {
			this.pluralExpression = sys.pluralExpression;
			this.pluralScript = sys.pluralScript;
		}
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
}
