package net.cactusthorn;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import net.cactusthorn.localization.BasicLocalizationTest;
import net.cactusthorn.localization.L10n;
import net.cactusthorn.localization.LoggingLocalization;

public class L10nExample {
	
	static Locale en_US = Locale.forLanguageTag("en-US");
	
	static { 
		try {
			Path path = Paths.get(BasicLocalizationTest.class.getClassLoader().getResource("L10n").toURI());
			L10n.theOnlyAttemptToInitInstance("test-app", path, LoggingLocalization.class);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String... args) {
		System.out.println(L10n.instance().get(en_US, "super.key"));
	}

}
