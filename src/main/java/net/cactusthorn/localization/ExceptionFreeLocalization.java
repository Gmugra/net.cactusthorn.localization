package net.cactusthorn.localization;

import java.util.Locale;
import java.util.Map;

public final class ExceptionFreeLocalization implements Localization {

    private Localization $localization;

    private ExceptionFreeLocalization(Localization localization) {
        $localization = localization;
    }

    public static class Builder extends AbstractLocalizationBuilder<ExceptionFreeLocalization> {

        private LocalizationBuilder<? extends Localization> $localizationBuilder;

        public Builder(LocalizationBuilder<? extends Localization> localizationBuilder) {
            $localizationBuilder = localizationBuilder;
        }

        @Override
        public ExceptionFreeLocalization build() {
            Localization localization = $localizationBuilder.withTranslations($translations).withSytsemId($systemId)
                    .withL10nDirectory($l10nDirectory).build();
            return new ExceptionFreeLocalization(localization);
        }
    }

    @Override
    public String get(Locale locale, String key, boolean withFormatting, Map<String, ?> parameters) {
        try {

            String text = $localization.get(locale, key, withFormatting, parameters);
            return text;
        } catch (LocalizationKeyException | LocalizationLocaleException e) {

            return e.getMessage();
        } catch (LocalizationFormatException e) {

            // LocalizationFormatException mean that correct key has bean found, but logic failed to format some parameter.
            // So, lets return found value without formatted parameters. Must work without exception.
            String text = $localization.get(locale, key, false, parameters);
            return text;
        } catch (LocalizationException e) {

            // LocalizationException at this moment mean that, before formatting, something wrong with parameters
            // So, lets return default message ASIS
            String text = $localization.getDefault(locale, key);
            return text.isEmpty() ? key + " : default text is undefined" : text;
        }
    }

    @Override
    public String getDefault(Locale locale, String key) {
        try {
            return $localization.getDefault(locale, key);
        } catch (LocalizationException e) {
            throw e;
        }
    }

    @Override
    public String format(Locale locale, String formatName, Object obj) {
        try {
            return $localization.format(locale, formatName, obj);
        } catch (LocalizationLocaleException e) {
            return e.getMessage();
        } catch (LocalizationFormatException e) {
            return obj.toString();
        }
    }

    @Override
    public Locale findNearest(Locale locale) {
        return $localization.findNearest(locale);
    }
}
