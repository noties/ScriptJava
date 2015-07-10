# ScriptJava
ScriptJava is a simple command line utility that helps to evaluate simple Java statements at runtime (aka scripting).
It may be used for educational purposes or just for fun of it as long as it offers a simple Java playground.

## Howto
Get a hold of a `ScriptJava.jar` from a `binary` folder (or build it from source). Make sure that `java` & `javac` are in the system's PATH. Run in the terminal:
```bash
java -jar ScriptJava.jar
```

## What can be done
*(note, that output for each statement is left out, but it's there :), it's executed right after you hit enter)*
```java
"Hello world!"
1 + 2
1 << 2
Integer.MAX_VALUE
```

```java
int max = Integer.MIN_VALUE
int min = Integer.MIN_VALUE
max - min
```
Note that each statement **must** return something (it cannot be void). Also note that for a single line statements semicolons are optional

```java
int getInt() {
	return 1
}
getInt()
```

```java
int getOtherInt(int what) {
	for (int i = 0; i < 10; i++) {
    	if ((i % what) == 0) return i
    }
    return -1
}
getOtherInt(3)
```
Note, that semicolon is required if line contains brakets

### Language level
The language level depends on `javac` from system's PATH. If it points to jdk1.8, then it's Java8, and so on.

### Custom JARs
If you wish to add a custom jar or jars as a dependency for a running script, create a folder named `lib` in ScriptJava.jar execution folder and place there your jars

### Utility methods

```java
print(Object o);
printf(String s, Object... args);
typeof(Object o); // return human readable representation of object's type
set(Object who, String fieldName, Object value);
get(Object who, String fieldName);
methods(Object who) // or a class (String.class) will print all object's class methods
dict(Object who) // will print object's fields and it's values (without recursion)
```

For a singleline statements that want to print an object `print` & `printf` commands are the best
```java
print(obj)
```

If you wish to print somthing to console during some operation use `out.*` (which is `System.out.*`)
```java
Runnable makeRunnable() {
	return new Runnable() {
    	public void run() { out.println(Thread.currentThread()); }
    };
}
```
Note that in this case semicolons are required

`set` method will use reflection. It's possible to change `final` fields:
```java
String s = "Hello World!"
void set(s, "value", new char[] { '!' }
s // will print !
```

### Methods
All generated methods are static, so you must **not** define a `static` modifier for your methods.

### Imports
By default each script has these in import section:
```java
import java.util.*;
import static java.lang.System.out;
import java.io.*;
import java.lang.reflect.*;
```

To add a custom import start your command with `import`, like this:
```java
import java.util.concurrent.*
```
You **must** import before accessing classes


### Void
if you want to execute a method that returns nothing (and thus cannot be printed) start your command with `void`, for example:
```java
void start()
```

### Clear
You might clear a certain scope of current script by evaluating a `clear()` command:
```java
clear() // no arguments - clears all
clear(var) // clears variables
clear(void) // clears voids
clear(met) // clears methods
clear(imp) // clears imports
```
You may composite clear scopes:
```java
clear(var void) // clears variables & voids
clear(imp met) // clears imports & methods
```

### Quit
To exit execution evaluate a `quit()` command

### Caveats
* No classes (although they could be defined like `void class MyClass {private int i = 1; void someMethod() {} };` aka one liner with `void` at the beginning
* If Threads are used they have to be set as daemon, otherwise script execution will freeze
* Methods might be with any modifiers except for `static` as long as script will add `static` keyword at the beginning no matter what
* No IDE support, if your are mistaken or this script fails you will have to write the last statement again (and again...)

## License

```
  Copyright 2015 Dimitry Ivanov (mail@dimitryivanov.ru)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
```