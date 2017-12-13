package net.cactusthorn;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import net.cactusthorn.localization.Localization;
import net.cactusthorn.localization.LocalizationLoader;

public class SimpleExample {

	static Locale en_US = Locale.forLanguageTag("en-US");
	
	public static void main(String... args) {
		
		try {
			
			String systemId = args[0];
		
			Path l10nDirectory = Paths.get(args[1]);
			
			Localization localization = new LocalizationLoader(systemId ).fromDirectory(l10nDirectory ).load();
			
			System.out.println(localization.get(en_US, "super.key"));
			
		} catch (IOException e ) {
			e.printStackTrace();
		}
	}
}
