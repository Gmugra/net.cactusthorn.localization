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
import java.util.Properties;
import java.util.TimeZone;

import javax.script.ScriptException;

public class FormatsTest {
	
	static Sys sysEN;
	static Formats formatsEN;
	static LocalDateTime localDateTimeEN;
	static ZonedDateTime zonedDateTimeEN;
	
	static java.util.Date date = new java.util.Date(1508570828338L);
	
	static Sys sysRU;
	static Formats formatsRU;
	
	@BeforeClass
	public static void setupEN() throws URISyntaxException, IOException, ScriptException {
		
		Path path = Paths.get(LocalizationTest.class.getClassLoader().getResource("L10n/en-US.properties").toURI());
		
		Properties props = new Properties();
		try (BufferedReader buf = Files.newBufferedReader(path, UTF_8 ) ) {
			props.load(buf);
		}
		sysEN = new Sys(props );
		
		formatsEN = new Formats(sysEN, props);
		
		{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", sysEN.getLocale());
			localDateTimeEN = LocalDateTime.parse("2017-09-17T11:16:50", formatter);
		}
		
		{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", sysEN.getLocale());
			zonedDateTimeEN = ZonedDateTime.parse("2017-09-17T11:16:50+01:00", formatter);
		}
	}
	
	@BeforeClass
	public static void setupRU() throws URISyntaxException, IOException, ScriptException {
		
		Path path = Paths.get(LocalizationTest.class.getClassLoader().getResource("L10n/ru-RU.properties").toURI());
		
		Properties props = new Properties();
		try (BufferedReader buf = Files.newBufferedReader(path, UTF_8 ) ) {
			props.load(buf);
		}
		sysRU = new Sys(props );
		
		formatsRU = new Formats(sysRU, props );
	}
	
	@Test
	public void testFormats() {
		
		assertEquals("2,000|2", formatsEN.format("special", 2000.20 ));
		assertEquals("$$2;000*20", formatsEN.format("curr", 2000.20 ));
		assertEquals("200", formatsEN.format("integer", 200.2 ));
		assertEquals("2,000.22", formatsEN.format("number", 2000.22f ));
		assertEquals("113,120&", formatsEN.format("percent", 1131.20 ));
	}
	
	@Test
	public void testWrongValue() {
		
		assertEquals("en_US", formatsEN.format("special", sysEN.getLocale()));
		assertEquals("rfrfrfrf", formatsEN.format("special", "rfrfrfrf"));
		assertEquals("null", formatsEN.format("special", null));
	}
	
	@Test
	public void testPattern() {
		
		assertEquals("9876.0", formatsEN.format("np1", 9876));
	}

	@Test
	public void testLocalDateTime() {
		
		assertEquals("Sep 17, 2017 11:16:50 AM",formatsEN.format("datetime", localDateTimeEN));
		assertEquals("Sep 17, 2017",formatsEN.format("date", localDateTimeEN));
		assertEquals("11:16:50 AM",formatsEN.format("time", localDateTimeEN));
	}
	
	@Test
	public void testZonedDateTime() {
		
		assertEquals("Sep 17, 2017 11:16:50 AM",formatsEN.format("datetime", zonedDateTimeEN));
		assertEquals("Sep 17, 2017",formatsEN.format("date", zonedDateTimeEN));
		assertEquals("11:16:50 AM",formatsEN.format("time", zonedDateTimeEN));
	}
	
	@Test
	public void testUtilDate() {
		
		assertEquals("Oct 21, 2017 9:27:08 AM",formatsEN.format("datetime", date));	
	}
	
	@Test
	public void testCalendar() {
		
		java.util.Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
		
		assertEquals("Oct 21, 2017 6:27:08 PM",formatsEN.format("datetime", calendar));
	}
	
	@Test
	public void testStyleFormat() {
		
		java.util.Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
		
		assertEquals("Saturday, October 21, 2017 6:27 PM",formatsEN.format("dt1", calendar));
	}

	@Test
	public void testPatternFormat() {
		
		java.util.Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
		
		assertEquals("2017-10-21T18:27:08+11:00",formatsEN.format("dt2", calendar));
	}
	
	@Test
	public void testLocalDateTimePattern() {
		
		assertEquals("2017-09-17T11:16:50+02:00",formatsEN.format("dt2", localDateTimeEN));
	}
	
	@Test
	public void testLocalDate() {
		
		assertEquals("2017-09-17T00:00:00+02:00",formatsEN.format("dt2", localDateTimeEN.toLocalDate()));
	}
	
	@Test
	public void testWrongDateTimeObject() {
		
		assertEquals("22",formatsEN.format("dt2", 22L));
	}
	
	@Test
	public void testTime() {
		
		assertEquals("11:16:50 AM",formatsEN.format("time", localDateTimeEN));
		assertEquals("11:16:50 AM",formatsEN.format("time", localDateTimeEN.toLocalTime()));
	}
	
	@Test
	public void testGroupingUsed() {
		
		assertEquals("2 000,22", formatsRU.format("number", 2000.22f ));
		assertEquals("2000,22", formatsRU.format("numb", 2000.22f ));
	}
}
