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
package net.cactusthorn.localization.formats;

import java.util.Locale;

public enum FormatType {
	NUMBER, INTEGER, PERCENT, CURRENCY, DATE, TIME, DATETIME;

	@Override
	public String toString() {
		return name().toLowerCase(Locale.ENGLISH);
	}
}
