package net.cactusthorn.localization.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static net.cactusthorn.localization.core.Parameter.*;

public class ParameterTest {

    @Test
    public void ofString() {
        Parameter<String> p = of("param1", "AAA");
        assertEquals("param1", p.getKey());
        assertEquals("AAA", p.getValue());
    }

    @Test
    public void ofCount() {
        Parameter<Integer> p = count(100);
        assertEquals(COUNT, p.getKey());
        assertEquals(100, p.getValue());
    }

    @Test
    public void ofMapNothing() {
        Map<String, Object> m = asMap();
        assertTrue(m.isEmpty());
    }

    @Test
    public void ofMapNull() {
        Map<String, Object> m = asMap((Parameter<?>[]) null);
        assertTrue(m.isEmpty());
    }

    @Test
    public void ofMapEmpty() {
        Map<String, Object> m = asMap(new Parameter<?>[0]);
        assertTrue(m.isEmpty());
    }

    @Test
    public void ofMap() {
        Map<String, Object> m = asMap(count(100), of("param1", "AAA"));
        assertEquals(2, m.size());
    }

}
