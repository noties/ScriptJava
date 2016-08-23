# ScriptJava
Java REPL today


There are a lot of benefits for a programming language to have a [REPL](https://en.wikipedia.org/wiki/Read%E2%80%93eval%E2%80%93print_loop). It helps one learn and explore the language features, gives ability to execute fast a code in question without launching IDE or modifiying existing project.



Writing *whole* Java code in command line seems like an overkill. Creating a class, then main method, then call to `System.out.print()` to execute such simple as `Integer.MAX_VALUE` command seems like a long way. What's why ScriptJava is *not* completely Java REPL. For example, semicolons(`;`) are *mostly* optional. There are a lot of *build-ins* methods, that can save a coder a few keyboard strokes. So, yeah, to know the value of `Integer.MAX_VALUE` you just input `Integer.MAX_VALUE`.

![](https://raw.githubusercontent.com/noties/ScriptJava/master/gif/1.gif)
![](https://raw.githubusercontent.com/noties/ScriptJava/master/gif/2.gif)

![](https://raw.githubusercontent.com/noties/ScriptJava/master/gif/3.gif)
![](https://raw.githubusercontent.com/noties/ScriptJava/master/gif/4.gif)

![](https://raw.githubusercontent.com/noties/ScriptJava/master/gif/5.gif)
![](https://raw.githubusercontent.com/noties/ScriptJava/master/gif/6.gif)

## Build-in functions
* **`bool()`** -> inspired by python's function. In short: `null`, `0`, or `""`(empty string) returns `false`. Defined in `Bool.java`
* **`len()`** - > inspired by python's function. Unifies calls to get length of different objects. Defined in `Length.java`
* **`str()`** - > returns *good* object representation as a String. Defined in `Str.java`. In case of a file (if exists) returns file contents as a String
* **`print()`** > do as expected. Accepts multiple objects: `print(1, 2, 3, 4)`. Every object's string representation will be obtained via `str()` call
* **`printf()`** -> Helper method to call `System.out.printf()`. Won't change the objects representation
* **`list()`** -> creates a list from supplied arguments, `list(1, 2, 3, 4)`
* **`map()`** -> original signature of this method is a bit scary: `map(String[] keys, Object[] values)`, but with the help of the substitution command becomes more friendly: `map(key1: 1, key2: 2)`
* **`bin()`** -> returns binary representation of an integer value
* **`hex()`** -> return hex representation of an integer value
* **`date()`** -> helper method to call `new Date()`
* **`now()`** -> helper method to call `System.currentTimeMillis()`
* **`exec()`** -> executes supplied command as a String in a different process, returns String (the process output): `exec("java -version")`
* **`file()`** -> this method has different signatures. Called without parameters returns current execution folder. Called with a String as a parameter -> constructs File object with specified path
* **`del()`** -> removes file, takes a File object or a path
* **`write()`** -> takes a File object and it's contents as a String. Will write contents to a file
* **`json()`** -> Super primitive json parser that returns `Element` with the following methods: `key(String)` -> retrieves an JSON object by the key; `at(int)` -> retrieves a value from JSON array at give index; `get()` -> returns JSON primitive. Please note that exception will be thrown if method called on wrong JSON type (for example `at` will be called on JSON object)
* **`get()`** -> executes GET http request, returns response as a String
* **`post()`** -> executes POST http request, returns response as a String
* **`range()`** -> creates a *range* of integers to be easily interated, `for (int i: range(10))`. Returns `int[]`. has 3 signatures: `range(end)`, `range(start, end)`, `range(start, end, step)`
* **`dict()`** -> returns object's meta info (class name, parent class name, implements), fields and values. Has ability to return also info about methods
* **`type()`** -> returns human readable Type info (redirects call to `str()`)
* **`toMap()`** -> converts an object into a `Map<String, Object>` (no recursive calls)
* **`fromMap()`** -> constructs an object from supplied `Map<String, Object>`
* **`set()`** -> tries to modify objects field given the name and value. Can modify `final` fields
* **`ni()`** -> simple call for `Class.newInstance()`
* **`store()`** -> stores a variable between script recompilations (actually can be useful only when removing previously retrieved value with `ret` call)
* **`ret()`** -> retirievs previously stored valiable or tries to obtain it if not present
* **`copy()`** -> copies supplied argument to the system clipboard and returns the value unmodified. `copy(exec("echo HelloThere | cowsay -e --"))` will copy result value of the `exec` call and print it.

Also, by default `java.util.Math` is statically imported, so **`pow()`**, **`sin()`**, **`cos()`**, etc are available

## Special commands
* **`import `** -> adds an import statement for a script, for example `import java.text.*`
* **`clean`** -> completely cleans script
* **`quit`** -> exits from REPL
* **`bytecode`** -> prints bytecode for current script (if `javap` is present in PATH)


## Substitutions
* **`map(key1: 1, key2: 2, key3: "some string")`** this (invalid in Java syntax) will be substituted new a call to build-in function map: `map(new String[] { "key1", "key2", "key3" }, new Object[] { 1, 2, "some string" })`
* **`ret("key", someCommand())`** -> will be sustituted with: `ret("key", new Provider() { Object provide() { return someCommand(); } })`. This is done to skip initialization command if value is already defined, else the Provider object will be called to retrieve the value

## Default imports
Except build-in functions these packages are imported by default:
* **`java.util.*`**
* **`java.text.*`**
* **`java.io.*`**

## Custom libraries
Right now if there is a folder called `libs` inside execution directory, all jars that are inside it will be added to the script class path. Anyway you still need to import desired class first (here is no surprices).

## How it's done
It's very naive implementation of parsing, but so far it strangely works :)

## Drawbacks
As we cannot achive completely *scripting* behavior from Java, there are certain things to be aware of. First, everytime you hit `Enter` the whole script is compiled from scratch. That's why all the variables will be set anew. For example:
```java
Date d = date() // hit Enter
d // will print the *current* date, not the date value, that we obtained earlier
```
There is a woraround for this: build-in function `ret`:
```java
Date d = ret("d", date())
d // will print the first obtained value
```

## Installation
// javac in classpath
// javap in classpath for bytecode


## Building
The main project has no dependencies, so it's a straight-forward process. However, building from IDE (when project is created from the source code)
won't work, as current functionality requires the project's jar in the execution directory (due to build-in imports & IScript)

Tests require `Gson` (for json testing)


## License

```
  Copyright 2016 Dimitry Ivanov (cr@dimitryivanov.ru)

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