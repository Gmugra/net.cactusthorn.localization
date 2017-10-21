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
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

public class FormatsTest {
	
	static Path enUSPath;
	static Properties enUSProps;
	static Locale locale = Locale.forLanguageTag("us-US");
	static Formats formats;
	static LocalDateTime localDateTime;
	static ZonedDateTime zonedDateTime;
	static java.util.Date date = new java.util.Date(1508570828338L);
	
	@BeforeClass
	public static void globalSetUp() throws URISyntaxException, IOException {
		
		enUSPath = Paths.get(LocalizationTest.class.getClassLoader().getResource("L10n/en-US.properties").toURI());
		
		enUSProps = new Properties();
		try (BufferedReader buf = Files.newBufferedReader(enUSPath, UTF_8 ) ) {
			enUSProps.load(buf);
		}
		
		formats = new Formats(locale, enUSProps);
		
		{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", locale);
			localDateTime = LocalDateTime.parse("2017-09-17T11:16:50", formatter);
		}
		
		{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", locale);
			zonedDateTime = ZonedDateTime.parse("2017-09-17T11:16:50+01:00", formatter);
		}
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
		
		assertEquals("us_US", formats.format("special", locale));
		assertEquals("rfrfrfrf", formats.format("special", "rfrfrfrf"));
		assertEquals("null", formats.format("special", null));
	}
	
	@Test
	public void testPattern() {
		
		assertEquals("9876.0", formats.format("np1", 9876));
	}

	@Test
	public void testLocalDateTime() {
		
		assertEquals("Sep 17, 2017 11:16:50 AM",formats.format("datetime", localDateTime));
		assertEquals("Sep 17, 2017",formats.format("date", localDateTime));
		assertEquals("11:16:50 AM",formats.format("time", localDateTime));
	}
	
	@Test
	public void testZonedDateTime() {
		
		assertEquals("Sep 17, 2017 11:16:50 AM",formats.format("datetime", zonedDateTime));
		assertEquals("Sep 17, 2017",formats.format("date", zonedDateTime));
		assertEquals("11:16:50 AM",formats.format("time", zonedDateTime));
	}
	
	@Test
	public void testUtilDate() {
		
		assertEquals("Oct 21, 2017 9:27:08 AM",formats.format("datetime", date));	
	}
	
	@Test
	public void testCalendar() {
		
		java.util.Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
		
		assertEquals("Oct 21, 2017 6:27:08 PM",formats.format("datetime", calendar));
	}

}
