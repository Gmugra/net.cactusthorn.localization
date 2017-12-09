package net.cactusthorn.localization;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LocalizationLoaderTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void testNotDirectory() throws URISyntaxException, IOException {
		
		expectedException.expect(IOException.class);
		expectedException.expectMessage(containsString("is not directory"));

		Path path = Paths.get(BasicLocalizationTest.class.getClassLoader().getResource("L10n/ru-RU.properties").toURI());
		new LocalizationLoader("test-app").setL10nDirectory(path).load();
	}
	
	@Test
	public void testWrongSystemId() throws URISyntaxException, IOException {
		
		expectedException.expect(IOException.class);
		expectedException.expectMessage("Something wrong with file \"fr-CA.properties\"");
		expectedException.expectCause(
			allOf(
				isA(LocalizationException.class),
				hasProperty("message", is("Wrong _system.id=test-app, expected: _system.id=my-super-app"))
			)
		);
		
		Path path = Paths.get(BasicLocalizationTest.class.getClassLoader().getResource("WrongSystemId").toURI());
		new LocalizationLoader("my-super-app").setL10nDirectory(path).setClass(LoggingLocalization.class).load();
	}
	
	@Test
	public void testWrongLanguageTag() throws URISyntaxException, IOException {
		
		expectedException.expect(IOException.class);
		expectedException.expectMessage("Something wrong with file \"fr-CA.properties\"");
		expectedException.expectCause(
			allOf(
				isA(LocalizationException.class),
				hasProperty("message", is("Wrong value of _system.languageTag=en-US, expected: _system.languageTag=fr-CA"))
			)
		);
		
		Path path = Paths.get(BasicLocalizationTest.class.getClassLoader().getResource("WrongLanguageTag").toURI());
		new LocalizationLoader("test-app").setL10nDirectory(path).load();
	}
}
