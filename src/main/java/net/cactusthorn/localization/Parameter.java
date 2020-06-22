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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

public final class Parameter<T> extends SimpleEntry<String, T> {

    private static final long serialVersionUID = 0L;

    public static final Map<String, Object> EMPTY_PARAM_MAP = Collections.emptyMap();

    private Parameter(String key, T value) {
        super(key, value);
    }

    public static <A> Parameter<A> of(String key, A value) {
        return new Parameter<A>(key, value);
    }

    public static final String COUNT = "count";

    public static Parameter<Integer> count(int value) {
        return new Parameter<Integer>(COUNT, value);
    }

    public static Map<String, Object> asMap(Parameter<?>... parameters) {

        if (parameters == null || parameters.length == 0) {
            return EMPTY_PARAM_MAP;
        }

        Map<String, Object> parametersMap = new HashMap<>(parameters.length + 1, 1);
        for (Parameter<?> parameter : parameters) {
            parametersMap.put(parameter.getKey(), parameter.getValue());
        }

        return parametersMap;
    }
}
