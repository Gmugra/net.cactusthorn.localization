
# net.cactusthorn.localization

Java library for texts localization

## Why?

The localization possibilities which present in Java (java.text.*, java.util.Formatter and so on) are preatty powerful but...
Not really usefeul ASIS in real big applications because of several issues:
* pluralization based on java.text.ChoiceFormat is too primitive for a lot of languages. Need plural forms support.
  * FYI: https://github.com/translate/l10n-guide/blob/master/docs/l10n/pluralforms.rst
* parameters based on index-number are not convenient. Need "named" parameters.
* java.text.Format subclasses (and, as result, everything what are using them) are not thread safe.
  * Since Java 8, for date/time we have thread safe java.time.format.DateTimeFormatter, but numbers are still problem.
* formats which you can use to format parametes are not flexible enough.
  * You need "format symbols" there. At least.

That all what the net.cactusthorn.localization library is solving.

## Files
* The library required path to directory with UTF-8 encoded .properties files (since Java 6 java.util.Properties support that). 
* One file per "locale". File names follow naming convention: languageTag.proprties (e.g. en-US.properties, ru-RU.properties )
  * FYI: https://docs.oracle.com/javase/8/docs/api/java/util/Locale.html#forLanguageTag-java.lang.String-

Note: .properties file are not trendy but imho convenient: lightweight, native for java, support comments & line-breaking

## Syntax
The file consist of three groups of properties.
### system properties
* _system.id - any string, need to be sure that the file belong to the system 
* _system.languageTag - langauge tag same with the file name
* _system.nplurals - amount of plural forma in the language
  * https://github.com/translate/l10n-guide/blob/master/docs/l10n/pluralforms.rst
* _system.plural - java script expresion to get index of plural form
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
```

Only property *type* is required. Possible values: number, integer, percent, currency, date, time, datetime

By default, seven formats for supported types are available. Names of these formats are same with types.

Format properties (note: format-property anmes are normally same with related java-classes methods):

| Property | Can be use with types | possible values |
| --- | --- | --- |
| type | number, integer, percent, currency, date, time, datetime | number, integer, percent, currency, date, time, datetime |
| pattern | number, integer, percent, currency, date, time, datetime | format pattern |
| groupingSeparator | number, integer, percent, currency | single character |
| decimalSeparator | number, integer, percent, currency | single character |
| groupingUsed | number, integer, percent, currency | true, false |
| monetaryDecimalSeparator | currency | single character |
| currencySymbol | currency | any string |
| percentSymbol | percent | single character |
| dateStyle | date, time, datetime | full, long, meduim, short (https://docs.oracle.com/javase/8/docs/api/java/time/format/FormatStyle.html) |
| timeStyle | date, time, datetime | full, long, meduim, short (https://docs.oracle.com/javase/8/docs/api/java/time/format/FormatStyle.html) |


### translations
Name sytax: any-key[count number|$pluralization form index][$html]

Examples:
```
super.key = Super value
super.htmlkey$html = Super <br/> value

x.y.z.apple = apples by default
x.y.z.apple.0 = no any apples
x.y.z.apple.1 = one apple
x.y.z.apple.22$html = special case:<br/> 22 apples
x.y.z.apple.$1$html = {{count}}<br/> apples
```

## License
Released under the BSD 2-Clause License
```
Copyright (C) 2017, Alexei Khatskevich
All rights reserved.

Licensed under the BSD 2-clause (Simplified) License (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
http://opensource.org/licenses/BSD-2-Clause
