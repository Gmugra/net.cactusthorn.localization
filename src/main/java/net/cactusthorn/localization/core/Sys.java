/*******************************************************************************
 * Copyright (C) 2017, Alexei Khatskevich
 * All rights reserved.
 * 
 * Licensed under the BSD 2-clause (Simplified) License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://opensource.org/licenses/BSD-2-Clause
 ******************************************************************************/
package net.cactusthorn.localization.core;

import java.util.Locale;
import java.util.Map;

import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import net.cactusthorn.localization.LocalizationException;

import javax.script.Compilable;
import javax.script.Bindings;

class Sys {
	
	private CompiledScript pluralScript;

	private String id;
	private Locale locale;
	private Integer nplurals;
	private String pluralExpression;
	private Boolean escapeHtml;
	
	public static final String SYSTEM_PREFIX = "_system.";
	public static final String ID = SYSTEM_PREFIX + "id";
	public static final String TAG = SYSTEM_PREFIX + "languageTag";
	public static final String NPLURALS = SYSTEM_PREFIX + "nplurals";
	public static final String PLURALS = SYSTEM_PREFIX + "plural";
	public static final String ESCAPE_HTML = SYSTEM_PREFIX + "escapeHtml";
	
	Sys(Map<String,String> properties ) throws ScriptException {
		
		this(
			properties.get(ID),
			properties.get(TAG) == null ? null : Locale.forLanguageTag(properties.get(TAG)),
			properties.get(NPLURALS) == null ? null :Integer.valueOf(properties.get(NPLURALS)),
			properties.get(PLURALS),
			Boolean.valueOf(properties.get(ESCAPE_HTML)) );
	}
	
	Sys(String id, Locale locale, Integer nplurals, String pluralExpression, Boolean escapeHtml) throws ScriptException {
	
		if (locale == null ) {
			throw new LocalizationException(TAG +" is required" );
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
	
	String id() {
		return id;
	}

	Locale locale() {
		return locale;
	}
	
	String languageTag() {
		return locale.toLanguageTag();
	}

	boolean escapeHtml() {
		return escapeHtml;
	}

	@Override
	public String toString() {
		return "Sys(id=" + id 
				+ ", locale=" + locale 
				+ ", nplurals=" + nplurals 
				+ ", pluralExpression=" + pluralExpression 
				+ ", escapeHtml=" + escapeHtml + ")";
	}
	
	
}
