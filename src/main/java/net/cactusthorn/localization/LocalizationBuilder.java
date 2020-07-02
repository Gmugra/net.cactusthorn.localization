package net.cactusthorn.localization;

import java.util.Locale;
import java.util.Map;

import net.cactusthorn.localization.core.LocalizationKeys;

public interface LocalizationBuilder<T extends Localization> {

    LocalizationBuilder<T> withTranslations(Map<Locale, LocalizationKeys> translations);

    LocalizationBuilder<T> withSytsemId(String systemId);

    LocalizationBuilder<T> withL10nDirectory(String l10nDirectory);

    T build();
}
