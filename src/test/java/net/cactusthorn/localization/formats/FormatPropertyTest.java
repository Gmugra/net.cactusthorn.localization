package net.cactusthorn.localization.formats;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class FormatPropertyTest {

    private static Stream<Arguments> provideArguments() {
        return Stream.of(
                Arguments.of(FormatProperty.TYPE, FormatProperty.TYPE.property()),
                Arguments.of(FormatProperty.PATTERN, FormatProperty.PATTERN.property()),
                Arguments.of(FormatProperty.CURRENCY_SYMBOL, FormatProperty.CURRENCY_SYMBOL.property()),
                Arguments.of(FormatProperty.GROUPING_USED, FormatProperty.GROUPING_USED.property()),
                Arguments.of(FormatProperty.GROUPING_SEPARATOR, FormatProperty.GROUPING_SEPARATOR.property()),
                Arguments.of(FormatProperty.DECIMAL_SEPARATOR, FormatProperty.DECIMAL_SEPARATOR.property()),
                Arguments.of(FormatProperty.MONETARY_DECIMAL_SEPARATOR, FormatProperty.MONETARY_DECIMAL_SEPARATOR.property()),
                Arguments.of(FormatProperty.PERCENT_SYMBOL, FormatProperty.PERCENT_SYMBOL.property()),
                Arguments.of(FormatProperty.DATE_STYLE, FormatProperty.DATE_STYLE.property()),
                Arguments.of(FormatProperty.TIME_STYLE, FormatProperty.TIME_STYLE.property())
        );
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    public void propertyOf(FormatProperty propertyEnum, String propertyStr) {
        FormatProperty result = FormatProperty.of(propertyStr);
        assertEquals(propertyEnum, result);
    }

    @Test
    public void wrongPropertyOf() {
        assertThrows(IllegalArgumentException.class, () -> FormatProperty.of("wrong"));
    }
}
