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
        Locale locale = new Locale("en", "US");
        Plural plural = Plural.of(locale);
        assertEquals(Plural.EN.name(), plural.name());
    }

    @Test
    public void testOfUnknown() {
        Locale locale = new Locale("xx", "US");
        assertThrows(IllegalArgumentException.class, () -> Plural.of(locale));
    }

    // @formatter:off
    private static Stream<Arguments> provideArguments() {
        return Stream.of(
            Arguments.of(Plural.EN, 0, 1),
            Arguments.of(Plural.EN, 1, 4),

            Arguments.of(Plural.RU, 0, 1),
            Arguments.of(Plural.RU, 1, 3),
            Arguments.of(Plural.RU, 2, 10),
            Arguments.of(Plural.RU, 2, 0)
        );
    }
    // @formatter:on

    @ParameterizedTest
    @MethodSource("provideArguments")
    public void evalPlural(Plural plural, int expected, int count) {
        assertEquals(expected, plural.evalPlural(count));
    }
}
