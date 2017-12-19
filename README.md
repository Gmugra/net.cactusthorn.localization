
# net.cactusthorn.localization

Java library for texts localization

## Why?

The localization possibilities which present in Java (java.text.*, java.util.Formatter and so on) are powerful but...
Not always useful ASIS because of several issues:
* pluralization based on java.text.ChoiceFormat is too primitive for a lot of languages. Need plural forms support.
  * FYI: https://github.com/translate/l10n-guide/blob/master/docs/l10n/pluralforms.rst
* parameters based on index-number are not convenient. Need "named" parameters.
* java.text.Format subclasses (and, as result, everything what are using them) are not thread safe.
  * Since Java 8, for date/time we have thread safe java.time.format.DateTimeFormatter, but numbers are still problem.
* formats which you can use to format parameters are not flexible enough.
  * You need "format symbols" there. At least.

# Files
* The library required path to directory with .properties files (UTF-8 encoded, since Java 6 java.util.Properties support that). 
* One "main" file per "locale". File names follow naming convention: languageTag.properties (e.g. **en-US.properties**, **ru-RU.properties** )
  * FYI: https://docs.oracle.com/javase/8/docs/api/java/util/Locale.html#forLanguageTag-java.lang.String-

Note I: .properties file are not trendy but *imho* convenient: lightweight, native for java, support comments & line-breaking.
Note II: to support any other file-format need to implement specific *LocalizationLoader* class. Not a big deal actually. 

## Default Files
Each localе can have optional "default" file. Content of "default" file(if it exists) load first, and than "overrided" by content from "main" file.
Overriding is working key by key, including system settings and formats. 
It gives possible to override, for example, only one plural version of language key, one system setting or one property of specific format. 

Name convension for default "files": default.languageTag.properties (e.g. **default.en-US.properties**, **default.ru-RU.properties** )

Default files is simple way to share same languages settings/formats/texts between multiple applications. 
Or run several instance of same application with minor changes in specificic "main" language file.

Note I: Actually both "default" and "main" files are optional. It's fine to have at least one of these two.\
Note II: only one difference between "default" and "main" files is *_system.id*. Default files ignore it. 

## Files Syntax
The file consist of three groups of properties.
### system properties
* _system.id - any string, need to validate, that the file belong to the application which try to load it. 
* _system.languageTag - language tag same with the file name, need for paranoid validation, **REQUIRED**
* _system.nplurals - amount of plural forma in the language
  * https://github.com/translate/l10n-guide/blob/master/docs/l10n/pluralforms.rst
* _system.plural - java script expression to get index of plural form
  * https://github.com/translate/l10n-guide/blob/master/docs/l10n/pluralforms.rst
* _system.escapeHtml - escape or not HTML in the texts by default

Example:
```
_system.id = test-app
_system.languageTag = en-US
_system.nplurals = 2
_system.plural = (count != 1);
_system.escapeHtml = true
```

### format properties

Sytax: _format.*format-name*.*format-property*

Examples:
```
_format.curr.type = currency
_format.curr.monetaryDecimalSeparator = *
_format.curr.currencySymbol = $$

_format.dt1.type = datetime
_format.dt1.dateStyle = full
_format.dt1.timeStyle = short

_format.iso8601.type = datetime
_format.iso8601.pattern = yyyy-MM-dd'T'HH:mm:ssXXX

_format.numb.type = number
_format.numb.groupingUsed = false

_format.percent.type = percent
_format.percent.percentSymbol = +
```

Only property *type* is required. Possible values: **number**, **integer**, **percent**, **currency**, **date**, **time**, **datetime**

Seven formats( same with supported types names) are always available, even if they clearly not present in the file.
* with default for the locale properties
* but, it is allowed to override them (e.g. *percent* in the example before)

Format properties:

| Property | Can be use with types | possible values |
| --- | --- | --- |
| type | number, integer, percent, currency, date, time, datetime | number, integer, percent, currency, date, time, datetime |
| pattern | number, integer, percent, currency, date, time, datetime | format pattern https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html |
| groupingSeparator | number, integer, percent, currency | single character |
| decimalSeparator | number, integer, percent, currency | single character |
| groupingUsed | number, integer, percent, currency | true, false |
| monetaryDecimalSeparator | currency | single character |
| currencySymbol | currency | any string |
| percentSymbol | percent | single character |
| dateStyle | date, time, datetime | full, long, meduim, short (https://docs.oracle.com/javase/8/docs/api/java/time/format/FormatStyle.html) |
| timeStyle | date, time, datetime | full, long, meduim, short (https://docs.oracle.com/javase/8/docs/api/java/time/format/FormatStyle.html) |


### text key
Key syntax: any-name[count number|$pluralization form index][$html]

Examples:
```
verysimple = Very Simple
any_name-actually = Any Any
super.key = Super value
super.htmlkey$html = Super <br/> value

x.y.z.apple = apples by default
x.y.z.apple.0 = no any apples
x.y.z.apple.1 = one apple
x.y.z.apple.22$html = special case:<br/> 22 apples
x.y.z.apple.$1$html = {{count}}<br/> apples
```

#### count number and pluralization form index
The key can have multiple versions with different count and/or pluralization form index postfixes

{{count}} is special, reserved parameter. It must contain integer value and need to support pluralization.
If request to get localization text contain {{count}} parameter system will try to find the most appropriate version of the key:
* First choice: key with postfix, which exactly equals {{count}} parameter value ((e.g. **.1**, **.22**)
* Second choice: key with postfix, which equals to pluralization form index, calculateded by {{count}} parameter value ((e.g. **.$1**, **.$0**)
* Last choice: key without count related postfixes.

#### $html
Very last, optional, key postfix. 
If it present, it is mean that value of key will not HTML encoded, even if _system.escapeHtml = true

### text parameters
Any text-value can contain unbounded amount of parameters.
Parameter syntax: {{parameter-name,[format-name]}}

Examples:
```
key.with.parameters = Super {{param1}} value {{cooldate,iso8601}} xxxx {{prc,percent}}
```

* Positions of parameters in the text play no any role.
* Parameters of the same key, in different language files can have different positions in the text.
* Parameters of the same key, in different language files can have different formats
* Same key, in different language files can have different set of parameters
* All of that true also for different count number/pluralization form index in the same language file

# How to use

## Basic example:
```
package net.cactusthorn;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

import net.cactusthorn.localization.Localization;
import net.cactusthorn.localization.PathLocalizationLoader;

public class SimpleExample {

	static Locale en_US = Locale.forLanguageTag("en-US");
	
	public static void main(String... args) {
		
		try {

			String systemId = args[0];
			String l10nDirectory = args[1];
			
			Localization localization = new PathLocalizationLoader(systemId ).from(l10nDirectory ).load();
			
			System.out.println(localization.get(en_US, "super.key"));

		} catch (URISyntaxException | IOException e ) {
			e.printStackTrace();
		}
	}
}
```

## Loaders
By default loaders create an instance of *net.cactusthorn.localization.BasicLocalization* and load it with files from "L10n" directory, using UTF-8 character set. Only **systemId** is required.

"Full" loader call:
```
new JarLocalizationLoader("my-system-id").instanceOf(LoggingLocalization.class).from("res/L10n" ).encoded(StandardCharsets.UTF_8).load();

```

* PathLocalizationLoader - load from the disk
* JarLocalizationLoader - load from the JAR resources

## Implementations
* BasicLocalization - straightforward implementation, which simple throw exception in any "wrong" situation
* LoggingLocalization - never(except initialization phase) throw exceptions, but right them in the log(Slf4j) instead. Methods calls always return some string, with as much as possible data.
  * https://www.slf4j.org/
* WatchLoggingLocalization - LoggingLocalization which run Thread with WatchService to, on the fly, upload changes from the files
  * sure, senseless to use it with JarLocalizationLoader
  * https://docs.oracle.com/javase/8/docs/api/java/nio/file/WatchService.html

## Parameters
*Localization* interface contain several *get* methods to work with parameters in deferent forms. 
Most simple way is *net.cactusthorn.localization.Parameter* class:
Example:
```
l10n.get(en_US, "some-key", Parameter.count(33), Parameter.of("param-name-1", "value"), Parameter.of("param-name-2", some-object));
```

## L10n Singleton 
Initialization-on-demand holder of any implementation. 
* https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom

Keep in mind, that it's not clean according to true OOD to have and use something like that.
Never the less it's working just fine.

Problems with it: 
* can throw exception during initialization(when something wrong with the files), and this fact breaks initialization-on-demand pattern, actually
  * if it fail at initialization moment, it will be dead: NoClassDefFoundError.

Trade-off: need to organize initial single call of the class's special initialization-method somewhere at very start of your application.
Example: *net.cactusthorn.L10nExample*

# License

Released under the BSD 2-Clause License
```
Copyright (C) 2017, Alexei Khatskevich
All rights reserved.

Licensed under the BSD 2-clause (Simplified) License (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
http://opensource.org/licenses/BSD-2-Clause
```
