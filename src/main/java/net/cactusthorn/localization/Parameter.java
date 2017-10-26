package net.cactusthorn.localization;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

public class Parameter<T> extends SimpleEntry<String,T> {

	private static final long serialVersionUID = 0L;
	
	public static final Map<String, Object> EMPTY_PARAM_MAP = Collections.emptyMap();

	private Parameter(String key, T value) {
		super(key,value);
	}
	
	public static <A> Parameter<A> of(String key, A value) {
		return new Parameter<A>(key, value);
	}
	
	public static Map<String, Object> asMap(Parameter<?>... parameters) {
		
		if (parameters == null || parameters.length == 0 ) {
			return EMPTY_PARAM_MAP;
		}
		
		Map<String, Object> parametersMap = new HashMap<>(parameters.length + 1, 1);
		for (Parameter<?> parameter : parameters ) {
			parametersMap.put(parameter.getKey(), parameter.getValue());
		}
		
		return parametersMap;
	}
}
