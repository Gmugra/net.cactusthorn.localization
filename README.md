
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

## License
Released under the BSD 2-Clause License
```
Copyright (C) 2017, Alexei Khatskevich
All rights reserved.

Licensed under the BSD 2-clause (Simplified) License (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
http://opensource.org/licenses/BSD-2-Clause
