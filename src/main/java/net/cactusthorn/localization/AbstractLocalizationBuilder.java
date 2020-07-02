package net.cactusthorn.localization;

import java.util.Locale;
import java.util.Map;

import net.cactusthorn.localization.core.LocalizationKeys;

public abstract class AbstractLocalizationBuilder<T extends Localization> implements LocalizationBuilder<T> {

    protected Map<Locale, LocalizationKeys> $translations;
    protected String $systemId;
    protected String $l10nDirectory;

    @Override
    public LocalizationBuilder<T> withTranslations(Map<Locale, LocalizationKeys> translations) {
        if (translations == null) {
            throw new IllegalArgumentException();
        }
        $translations = translations;
        return this;
    }

    @Override
    public LocalizationBuilder<T> withSytsemId(String systemId) {
        if (systemId == null) {
            throw new IllegalArgumentException();
        }
        $systemId = systemId;
        return this;
    }

    @Override
    public LocalizationBuilder<T> withL10nDirectory(String l10nDirectory) {
        if (l10nDirectory == null) {
            throw new IllegalArgumentException();
        }
        $l10nDirectory = l10nDirectory;
        return this;
    }
}
