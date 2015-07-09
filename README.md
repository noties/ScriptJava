# ScriptJava
ScriptJava is a simple command line utility that helps to evaluate simple Java statements at runtime (aka scripting)

## Howto
Get a hold of a `ScriptJava.jar` from a `binary` folder. Make sure that `java` & `javac` are in the system's PATH. Run in the terminal:
```bash
java -jar ScriptJava.jar
```

## What can be done
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

### Utility methods

```java
print(Object o);
printf(String s, Object... args);
typeof(Object o);
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

### Methods
All generated methods are static, so you must **not** define a `static` modifier for your methods.

### Imports
By default each script has these in import section:
```java
import java.util.*;
import static java.lang.System.out;
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