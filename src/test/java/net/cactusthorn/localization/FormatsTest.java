package net.cactusthorn.localization;

import org.junit.BeforeClass;
import org.junit.Test;

import net.cactusthorn.localization.formats.Formats;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Locale;
import java.util.Properties;


import javax.script.ScriptException;

public class FormatsTest {
	
	static Path enUSPath;
	static Properties enUSProps;
	static Locale locale = Locale.forLanguageTag("us-US");
	static Formats formats;
	
	@BeforeClass
	public static void globalSetUp() throws URISyntaxException, IOException {
		
		enUSPath = Paths.get(LocalizationTest.class.getClassLoader().getResource("L10n/en-US.properties").toURI());
		
		enUSProps = new Properties();
		try (BufferedReader buf = Files.newBufferedReader(enUSPath, UTF_8 ) ) {
			enUSProps.load(buf);
		}
		
		formats = new Formats(locale, enUSProps);
	}
	
	@Test
	public void testFormats() {
		
		assertEquals("2,000|2", formats.format("special", 2000.20 ));
		assertEquals("$$ 2;000*20", formats.format("curr", 2000.20 ));
		assertEquals("200", formats.format("integer", 200.2 ));
		assertEquals("2,000.22", formats.format("number", 2000.22f ));
		assertEquals("113,120&", formats.format("percent", 1131.20 ));
	}
	
	@Test
	public void testWrongValue() {
		
		assertEquals("rfrfrfrf", formats.format("special", "rfrfrfrf"));
		assertEquals("$$ 2;000*20", formats.format("curr", 2000.20 ));
	}

}
