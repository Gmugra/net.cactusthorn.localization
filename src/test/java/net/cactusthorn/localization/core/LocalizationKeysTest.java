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

import org.junit.jupiter.api.Test;

import static net.cactusthorn.localization.core.Parameter.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class LocalizationKeysTest {

    private Map<String, String> initProperties() {

        Map<String, String> properties = new HashMap<>();

        properties.put("_system.id", "test-app");
        properties.put("_system.languageTag", "en-US");
        properties.put("_system.nplurals", "2");
        properties.put("_system.plural", "${count!=1?1:0}");
        properties.put("_system.escapeHtml", "true");

        properties.put("_format.dt2.type", "datetime");
        properties.put("_format.dt2.pattern", "yyyy-MM-dd'T'HH:mm:ss");

        properties.put("x.y.z.apple", "apples by default");
        properties.put("x.y.z.apple.0", "no any apples");
        properties.put("x.y.z.apple.1", "one apple");
        properties.put("x.y.z.apple.22$html", "special case:<br/> 22 apples");
        properties.put("x.y.z.apple.$1$html", "{{count}}<br/> apples");

        properties.put("datetest", "datetime: {{dt,dt2}}");

        return properties;
    }

    @Test
    public void testCombineWithNew() {

        Map<String, String> propertiesOne = initProperties();
        LocalizationKeys keysOne = new LocalizationKeys("test-app", "en-US", propertiesOne);

        Map<String, String> propertiesTwo = initProperties();
        propertiesTwo.put("x.y.z.apple.33", "33! 33!");
        LocalizationKeys keysTwo = new LocalizationKeys("test-app", "en-US", propertiesTwo);

        assertEquals("33<br/> apples", keysOne.get("x.y.z.apple", count(33)));

        keysOne.combineWith(keysTwo);

        assertEquals("33! 33!", keysOne.get("x.y.z.apple", count(33)));
    }

    @Test
    public void testCombineWithSubstitute() {

        Map<String, String> propertiesOne = initProperties();
        LocalizationKeys keysOne = new LocalizationKeys("test-app", "en-US", propertiesOne);

        Map<String, String> propertiesTwo = initProperties();
        propertiesTwo.put("x.y.z.apple.22$html", "Update!");
        LocalizationKeys keysTwo = new LocalizationKeys("test-app", "en-US", propertiesTwo);

        assertEquals("special case:<br/> 22 apples", keysOne.get("x.y.z.apple", count(22)));

        keysOne.combineWith(keysTwo);

        assertEquals("Update!", keysOne.get("x.y.z.apple", count(22)));
    }

    @Test
    public void testCombineWithVeryNew() {

        Map<String, String> propertiesOne = initProperties();
        LocalizationKeys keysOne = new LocalizationKeys("test-app", "en-US", propertiesOne);

        Map<String, String> propertiesTwo = initProperties();
        propertiesTwo.put("new.key", "new key");
        LocalizationKeys keysTwo = new LocalizationKeys("test-app", "en-US", propertiesTwo);

        keysOne.combineWith(keysTwo);

        assertEquals("new key", keysOne.get("new.key"));
    }

    private static java.util.Date date = new java.util.Date(1508570828338L);

    @Test
    public void testCombineWithFormat() {

        Map<String, String> propertiesOne = initProperties();
        LocalizationKeys keysOne = new LocalizationKeys("test-app", "en-US", propertiesOne);

        Map<String, String> propertiesTwo = initProperties();
        propertiesTwo.put("_format.dt2.pattern", "yyyy-MM-dd'T'HH");
        propertiesTwo.put("datetest", "{{dt,dt2}} -> datetime");
        LocalizationKeys keysTwo = new LocalizationKeys("test-app", "en-US", propertiesTwo);

        assertEquals("datetime: 2017-10-21T09:27:08", keysOne.get("datetest", of("dt", date)));

        keysOne.combineWith(keysTwo);

        assertEquals("2017-10-21T09 -&gt; datetime", keysOne.get("datetest", of("dt", date)));
    }

}
