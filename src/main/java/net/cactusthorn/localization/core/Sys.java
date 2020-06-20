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

import net.cactusthorn.localization.LocalizationException;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;
import jakarta.el.ValueExpression;

class Sys {

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

	Sys(Map<String, String> properties) {
		// @formatter:off
		this(
			properties.get(ID),
			properties.get(TAG) == null ? null : Locale.forLanguageTag(properties.get(TAG)),
			properties.get(NPLURALS) == null ? null :Integer.valueOf(properties.get(NPLURALS)),
			properties.get(PLURALS),
			Boolean.valueOf(properties.get(ESCAPE_HTML)) );
		// @formatter:on
	}

	Sys(String id, Locale locale, Integer nplurals, String pluralExpression, Boolean escapeHtml) {

		if (locale == null) {
			throw new LocalizationException(TAG + " is required");
		}

		this.locale = locale;
		this.id = id;
		this.nplurals = nplurals;
		this.escapeHtml = escapeHtml;
		this.pluralExpression = pluralExpression;
	}

	void combineWith(Sys sys) {

		if (sys.id != null)
			this.id = sys.id;
		if (sys.locale != null)
			this.locale = sys.locale;
		if (sys.nplurals != null)
			this.nplurals = sys.nplurals;
		if (sys.escapeHtml != null)
			this.escapeHtml = sys.escapeHtml;
		if (sys.pluralExpression != null)
			this.pluralExpression = sys.pluralExpression;
	}

	private static final ExpressionFactory EXPRESSION_FACTORY = ExpressionFactory.newInstance();

	int evalPlural(int count) {

		ELContext context = new StandardELContext(EXPRESSION_FACTORY);

		ValueExpression countVar = EXPRESSION_FACTORY.createValueExpression(count, Integer.class);
		context.getVariableMapper().setVariable("count", countVar);

		ValueExpression expression = EXPRESSION_FACTORY.createValueExpression(context, pluralExpression, Integer.class);

		return (Integer) expression.getValue(context);
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
		return "Sys(id=" + id + ", locale=" + locale + ", nplurals=" + nplurals + ", pluralExpression=" + pluralExpression + ", escapeHtml="
				+ escapeHtml + ")";
	}

}
