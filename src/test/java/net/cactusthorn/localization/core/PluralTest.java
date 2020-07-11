package net.cactusthorn.localization.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PluralTest {

    @Test
    public void testOfEN() {
        Locale locale = new Locale("en");
        Plural plural = Plural.of(locale);
        assertEquals(Plural.ENG.name(), plural.name());
    }

    @Test
    public void testOfWrong3LetterLangCode() {
        Locale locale = new Locale("xx");
        assertThrows(java.util.MissingResourceException.class, () -> Plural.of(locale));
    }

    @Test
    public void testOfUnknownLocale() {
        Locale locale = new Locale("xyz");
        assertThrows(IllegalArgumentException.class, () -> Plural.of(locale));
    }

    @Test
    public void testOfNUll() {
        assertThrows(IllegalArgumentException.class, () -> Plural.of(null));
    }

    // @formatter:off
    private static Stream<Arguments> provideArguments() {
        return Stream.of(
            Arguments.of(Plural.ENG, 0, 1),
            Arguments.of(Plural.ENG, 1, 4),

            Arguments.of(Plural.RUS, 0, 1),
            Arguments.of(Plural.RUS, 1, 3),
            Arguments.of(Plural.RUS, 2, 10),
            Arguments.of(Plural.RUS, 2, 0),

            Arguments.of(Plural.JPN, 0, 0),
            Arguments.of(Plural.JPN, 0, 1),
            Arguments.of(Plural.JPN, 0, 20),
            Arguments.of(Plural.JPN, 0, 53)
        );
    }
    // @formatter:on

    @ParameterizedTest
    @MethodSource("provideArguments")
    public void evalPlural(Plural plural, int expected, int count) {
        assertEquals(expected, plural.evalPlural(count));
    }
}
