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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.cactusthorn.localization.core.*;
import net.cactusthorn.localization.formats.FormatsTest;

@RunWith(Suite.class)
@SuiteClasses({
	SysTest.class,
	FormatsTest.class,
	LocalizationKeyTest.class,
	LocalizationKeysTest.class,
	LocalizationLoaderTest.class,
	BasicLocalizationTest.class,
	LoggingLocalizationTest.class,
	WithDefaultsTest.class
})
public class AllLocalizationTests {
}
