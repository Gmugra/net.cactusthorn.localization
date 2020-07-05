package net.cactusthorn.localization;

import static net.cactusthorn.localization.core.Parameter.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

public class ExceptionFreeLocalizationTest {

    private static Localization localization;

    @BeforeAll
    public static void loadL10n() throws IOException, URISyntaxException {

        localization = new PathLocalizationLoader("test-app")
                .withLocalizationBuilder(new ExceptionFreeLocalization.Builder(new BasicLocalization.Builder())).load();
    }

    private static Locale en = Locale.forLanguageTag("en"); // also locale fallback will work
    private static Locale fr_FR = Locale.forLanguageTag("fr-FR");

    @Test
    public void testUnavailableLocale() throws IOException {

        String text = localization.get(fr_FR, "x.y.z.apple", count(0));
        assertEquals("Locale: fr-FR, Unavailable locale", text);
    }

    @Test
    public void testUnavailableKey() {

        String text = localization.get(en, "x.m.z.apple", count(0));
        assertEquals("Locale: en-US, unavailable key: x.m.z.apple", text);
    }

    @Test
    public void testWrongCount() {

        String text = localization.get(en, "x.y.z.apple", of(COUNT, "xxxx"));
        assertEquals("apples by default", text);
    }

    @Test
    public void testWrongFormatNumber() {

        String text = localization.format(en, "number", fr_FR);
        assertEquals("fr_FR", text);
    }

    @Test
    public void testWrongFormatLocale() {

        String text = localization.format(fr_FR, "number", fr_FR);
        assertEquals("Locale: fr-FR, Unavailable locale", text);
    }

    @Test
    public void testGetDefaultExists() {

        String text = localization.getDefault(en, "x.y.z.apple");
        assertEquals("apples by default", text);
    }

    @Test
    public void testGetDefaultWrongKey() {

        assertThrows(LocalizationKeyException.class, () -> localization.getDefault(en, "x.A.z.apple"));
    }

    @Test
    public void testSuccess() {
        String text = localization.get(en, "x.y.z.apple", count(0));
        assertEquals("no any apples", text);
    }

    @Test
    public void testMissingParameters() {
        String text = localization.get(en, "test.param.first");
        assertEquals("first: {{first}}, second:{{second}}&lt;br/&gt;", text);
    }

    @Test
    public void testFormatException() {

        String text = localization.get(en, "formated.param", of("supernumber", new Object()));
        assertTrue(text.startsWith("super number: java.lang.Object@"));
    }

    @Test
    public void testNull() {

        String text = localization.format(en, "number", null);
        assertEquals("null", text);
    }
}
