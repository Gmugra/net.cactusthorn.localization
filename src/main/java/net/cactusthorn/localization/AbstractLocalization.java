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
package net.cactusthorn.localization;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import net.cactusthorn.localization.core.LocalizationKeys;

public abstract class AbstractLocalization implements Localization {

	protected final Map<Locale, LocalizationKeys> translations;
	protected final String systemId;
	protected final Path l10nDirectory;
	protected final Charset charset;
	
	public AbstractLocalization(Map<Locale, LocalizationKeys> translations, String systemId, Path l10nDirectory, Charset charset) {
		this.translations = translations;
		this.systemId = systemId;
		this.l10nDirectory = l10nDirectory;
		this.charset = charset;
	}
	
	public Locale findNearest(Locale locale) {
		
		if (translations.containsKey(locale) ) return locale;
		
		if (!"".equals(locale.getVariant() ) ) {
			
			Optional<Locale> found = 
				translations.keySet().stream()
					.filter(l -> l.getLanguage().equals(locale.getLanguage() ) && l.getCountry().equals(locale.getLanguage() ) )
					.findAny();
			
			if (found.isPresent() ) return found.get();
		}
		
		Optional<Locale> found = 
			translations.keySet().stream()
				.filter(l -> l.getLanguage().equals(locale.getLanguage() ) )
				.findAny();
			
		if (found.isPresent() ) return found.get();
		
		return null;
	}
	
	@Override
	public String get(Locale locale, String key) {
		return get(locale, key, true, (Map<String, ?>)null);
	}
	
	@Override
	public String get(Locale locale, String key, Parameter<?>... parameters) {
		return get(locale, key, true, Parameter.asMap(parameters));
	}
	
	@Override
	public String get(Locale locale, String key, boolean withFormatting, Parameter<?>... parameters ) {
		return get(locale, key, withFormatting, Parameter.asMap(parameters));
	}
	
	@Override
	public String get(Locale locale, String key, Map<String, ?> parameters) {
		return get(locale, key, true, parameters);
	}
}
