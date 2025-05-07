# The evolution of Java

![](img/gosling.jpg)

## Type system

> There are **two kinds of types** in the Java programming language:
> - primitive types
> - reference types
>
> There are, correspondingly, **two kinds of data values**:
> - primitive values
> - reference values
>
> that can be:
> - stored in variables,
> - passed as arguments,
> - returned by methods,
> - and operated on
> [JLS §4.1](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html#jls-4.1)

### Primitive types

|      Type | Bytes |              Minimum value |              Maximum value |
| --------: | :---: | -------------------------: | -------------------------: |
| `boolean` |   1   |                      false |                       true |
|    `byte` |   1   |                       -128 |                       +127 |
|   `short` |   2   |                    -32_768 |                    +32_767 |
|    `char` |   2   |                          0 |                     65_535 |
|     `int` |   4   |             -2_147_483_648 |             +2_147_483_647 |
|    `long` |   8   | -9_223_372_036_854_775_808 | +9_223_372_036_854_775_807 |
|   `float` |   4   |     1.4 * 10<sup>-45</sup> |     3.4 * 10<sup>+38</sup> |
|  `double` |   8   |    4.9 * 10<sup>-324</sup> |    1.8 * 10<sup>+308</sup> |

### Reference types

> The reference values (often just **references) are pointers to objects**,  
> and a special `null` reference, which refers to no object.
> [JLS §4.3.1](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html#jls-4.3.1)

```java
class Box {
    int content;
}

class Pass {
    public static void main(String[] args) {
        Box a = new Box();
        pass(a);
        System.out.println(a.content); // What does this print? 0, 1, 2, ...?
    }

    static void pass(Box x) {
        x.content = 1;
        x = new Box();
        x.content = 2;
    }
}
```

- Perhaps more obvious after inlining the method call:

```java
class Box {
    int content;
}

class Pass {
    public static void main(String[] args) {
        Box a = new Box();

        Box x = a;     // x and a point to 1 object
        x.content = 1;

        x = new Box(); // x and a point to 2 objects
        x.content = 2;

        System.out.println(a.content); // prints 1
    }
}
```

> The Java programming language does *not* pass objects by reference;
> it passes object references by value.  
> 
> There is exactly one parameter passing mode — pass by value —
> and that helps keep things simple.
>
> **“The Java Programming Language”**  
> (Ken Arnold, **James Gosling**, David Holmes)

#### Pass object reference by value (Java `class`, C# `class`)

![](img/pass1.svg)

#### Pass object by reference (C++ `&`, C# `ref struct`)

![](img/pass2.svg)

#### Pass object reference by reference (C# `ref class`)

![](img/pass3.svg)

## Annotations

```java
public class CompactDate {
    private final int yearMonthDay;

    public CompactDate(int year, Month month, int day) {
        // TODO validate constructor parameters

        this.yearMonthDay = year << 9 | month.ordinal() << 5 | day & 31;
    }

    public int year() {
        return yearMonthDay >> 9;
    }

    public Month month() {
        return Month.values()[(yearMonthDay >> 5) & 15];
    }

    public int day() {
        return yearMonthDay & 31;
    }

    public boolean equals(CompactDate that) {
        return this.yearMonthDay == that.yearMonthDay;
    }

    public int hashcode() {
        return yearMonthDay;
    }

    public String ToString() {
        return String.format("%02d. %s %d", day(), month(), year());
    }
}
```

- There are 3 bugs; how many can you spot?

```java
public class CompactDate {
    private final int yearMonthDay;

    public CompactDate(int year, Month month, int day) {
        // TODO validate constructor parameters

        this.yearMonthDay = year << 9 | month.ordinal() << 5 | day & 31;
    }

    public int year() {
        return yearMonthDay >> 9;
    }

    public Month month() {
        return Month.values()[(yearMonthDay >> 5) & 15];
    }

    public int day() {
        return yearMonthDay & 31;
    }

    @Override
    // error: method does not override or implement a method from a supertype
    public boolean equals(CompactDate that) {
        return this.yearMonthDay == that.yearMonthDay;
    }

    @Override
    // error: method does not override or implement a method from a supertype
    public int hashcode() {
        return yearMonthDay;
    }

    @Override
    // error: method does not override or implement a method from a supertype
    public String ToString() {
        return String.format("%02d. %s %d", day(), month(), year());
    }
}
```

- `@Override` is *not* required for method overriding
- Rather prevents compilation if *no* overriding happens:

```java
/**
 * Indicates that a method declaration is intended to override a
 * method declaration in a supertype. If a method is annotated with
 * this annotation type compilers are required to generate an error
 * message unless at least one of the following conditions hold:
 *
 * The method does override or implement a method declared in a supertype.
 *
 * The method has a signature that is override-equivalent to that of
 * any public method declared in Object.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Override {
}
```

- Similary, `@FunctionalInterface` is *not* required for functional interfaces
- Rather prevents compilation for *non*-functional interfaces:

```java
/**
 * An informative annotation type used to indicate that an interface
 * type declaration is intended to be a functional interface as
 * defined by the Java Language Specification.
 *
 * Conceptually, a functional interface has exactly one abstract method.
 *
 * If a type is annotated with this annotation type, compilers are
 * required to generate an error message unless
 * the annotated type satisfies the requirements of a functional interface.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionalInterface {
}
```

- Note the empty bodies `{}`
- Annotations themselves are merely metadata on declarations
- Require external code to give meaning
- For example, find all deprecated methods of `java.lang.Object`:

```java
for (Method method : Object.class.getDeclaredMethods()) {

    Deprecated deprecated = method.getAnnotation(Deprecated.class);
    if (deprecated != null) {
        System.out.println(method + " has been deprecated since Java " + deprecated.since());
    }
}
```

```java
package java.lang;

public class Object {

    public Object() {}

    // ...

    @Deprecated(since="9")
    protected void finalize() throws Throwable { }
}
```

> **Exercise:**
> 1. Implement `Anno.printSafeVarargsMethodsOfList` and print its return value
> 2. Implement `Anno.missesFunctionalInterfaceAnnotation` and test it with sensible inputs

## Enumerations

### Java 1.4

```java
public final class Month {
    private final String name;
    private final int ordinal;

    private final double days;

    public Month(String name, int ordinal, double days) {
        this.name = name;
        this.ordinal = ordinal;

        this.days = days;
    }

    public String name() {
        return name;
    }

    public int ordinal() {
        return ordinal;
    }

    public double days() {
        return days;
    }

    @Override
    public String toString() {
        return name;
    }

    public static final Month JAN = new Month("JAN",  0, 31);
    public static final Month FEB = new Month("FEB",  1, 28.2425);
    public static final Month MAR = new Month("MAR",  2, 31);
    public static final Month APR = new Month("APR",  3, 30);
    public static final Month MAY = new Month("MAY",  4, 31);
    public static final Month JUN = new Month("JUN",  5, 30);
    public static final Month JUL = new Month("JUL",  6, 31);
    public static final Month AUG = new Month("AUG",  7, 31);
    public static final Month SEP = new Month("SEP",  8, 30);
    public static final Month OCT = new Month("OCT",  9, 31);
    public static final Month NOV = new Month("NOV", 10, 30);
    public static final Month DEC = new Month("DEC", 11, 31);

    private static final Month[] values;
    private static final Map<String, Month> monthsByName;

    static {
        values = new Month[]{JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC};
        monthsByName = new HashMap<>(16);
        for (Month month : values) {
            monthsByName.put(month.name, month);
        }
    }

    public static Month[] values() {
        return values.clone();
    }

    public static Month valueOf(String name) {
        Month month = monthsByName.get(name);
        if (month == null) throw new IllegalArgumentException(name);
        return month;
    }

    public double days_alternative() {
        switch (this.ordinal + 1) {
            case 2:
                return 28.2425;

            case 4:
            case 6:
            case 9:
            case 11:
                return 30;

            default:
                return 31;
        }
    }

    public Month plus(int months) {
        return Month.values()[Math.floorMod(super.ordinal() + months, 12)];
    }
}
```

### Java 5

```java
public enum Month /* extends java.lang.Enum<Month> */ {
    JAN(31),      // public static final Month JAN = new Month("JAN",  0, 31);
    FEB(28.2425), // public static final Month FEB = new Month("FEB",  1, 28.2425);
    MAR(31),      // public static final Month MAR = new Month("MAR",  2, 31);
    APR(30),      // public static final Month APR = new Month("APR",  3, 30);
    MAY(31),      // public static final Month MAY = new Month("MAY",  4, 31);
    JUN(30),      // public static final Month JUN = new Month("JUN",  5, 30);
    JUL(31),      // public static final Month JUL = new Month("JUL",  6, 31);
    AUG(31),      // public static final Month AUG = new Month("AUG",  7, 31);
    SEP(30),      // public static final Month SEP = new Month("SEP",  8, 30);
    OCT(31),      // public static final Month OCT = new Month("OCT",  9, 31);
    NOV(30),      // public static final Month NOV = new Month("NOV", 10, 30);
    DEC(31);      // public static final Month DEC = new Month("DEC", 11, 31);

    /*private*/ Month(/* String name, int ordinal, */ double days) {
        /* super(name, ordinal); */
        this.days = days;
    }

    private final double days;

    public double days() {
        return days;
    }

    public double days_alternative() {
        switch (this) {
            case FEB:
                return 28.2425;

            case APR:
            case JUN:
            case SEP:
            case NOV:
                return 30;

            default:
                return 31;
        }
    }

    public Month plus(int months) {
        return Month.values()[Math.floorMod(super.ordinal() + months, 12)];
    }
}
```

## Resource management

### Java 6

What is wrong with the following database query?

```java
Connection connection = dataSource.getConnection();
PreparedStatement statement = connection.prepareStatement("select * from user where id = ?");
statement.setLong(1, id);
ResultSet resultSet = statement.executeQuery();
if (resultSet.next()) {
    return resultSet.getString("name");
} else {
    return null;
}
```

Solution #1

```java
Connection connection = null;
PreparedStatement statement = null;
ResultSet resultSet = null;
try {
    connection = dataSource.getConnection();
    statement = connection.prepareStatement("select * from user where id = ?");
    statement.setLong(1, id);
    resultSet = statement.executeQuery();
    if (resultSet.next()) {
        return resultSet.getString("name");
    } else {
        return null;
    }
} finally {
    if (resultSet != null) {
        try {
            resultSet.close();
        } catch (SQLException ignored) {
        }
    }
    if (statement != null) {
        try {
            statement.close();
        } catch (SQLException ignored) {
        }
    }
    if (connection != null) {
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }
}
```

Solution #2

```java
Connection connection = dataSource.getConnection();
try {
    PreparedStatement statement = connection.prepareStatement("select * from user where id = ?");
    try {
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        try {
            if (resultSet.next()) {
                return resultSet.getString("name");
            } else {
                return null;
            }
        } finally {
            resultSet.close();
        }
    } finally {
        statement.close();
    }
} finally {
    connection.close();
}
```

### Java 7

try-with-resources

```java
try (Connection connection = dataSource.getConnection();
     PreparedStatement statement = connection.prepareStatement("select * from user where id = ?")) {
    statement.setLong(1, id);
    try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
            return resultSet.getString("name");
        } else {
            return null;
        }
    }
}
```

## LoggerFactory.getLogger

### Java 6

```java
public class Foo {
    private static final Logger log = LoggerFactory.getLogger(Foo.class);

    // ...
}

public class Bar {
    private static final Logger log = LoggerFactory.getLogger(Bar.class);

    // ...
}

public class Baz {
    private static final Logger log = LoggerFactory.getLogger(Bar.class);

    // ...
}
```

Do you see the problem?

### Java 7

```java
public class Foo {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // ...
}

public class Bar {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // ...
}

public class Baz {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // ...
}
```

> **Exercise:** Search for `LoggerFactory.getLogger` in your project:
> - How many `Wrong.class` bugs exist?
> - How many declarations are missing `private`, `static` or `final`?
> - How many variable spellings (`LOG`, `log`, `LOGGER`, `logger`...) are used?

## Case study: GUI code

### Java 1.0 (1996)

```java
public class GUI {
    public static void main(String[] args) {
        Frame frame = new Frame("Close me!");
        frame.addWindowListener(new FrameCloser(frame));

        Button button = new Button("Click me to see the current date!");
        button.addActionListener(new ButtonUpdater(button));
        frame.add(button);

        frame.pack();
        frame.show();
    }
}

class FrameCloser extends WindowAdapter {
    private final Frame frame;

    FrameCloser(Frame frame) {
        this.frame = frame;
    }

    public void windowClosing(WindowEvent event) {
        frame.dispose();
    }
}

class ButtonUpdater implements ActionListener {
    private final Button button;

    ButtonUpdater(Button button) {
        this.button = button;
    }

    public void actionPerformed(ActionEvent event) {
        button.setLabel(new Date().toString());
    }
}
```

- `GUI.java`
  - `GUI.class`
- `FrameCloser.java`
  - `FrameCloser.class`
- `ButtonUpdater.java`
  - `ButtonUpdater.class`

### Java 1.1 (1997)

```java
public class GUI {
    public static void main(String[] args) {
        final Frame frame = new Frame("Close me!");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                frame.dispose();
            }
        });

        final Button button = new Button("Click me to see the current date!");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                button.setLabel(new Date().toString());
            }
        });
        frame.add(button);

        frame.pack();
        frame.show();
    }
}
```

- `GUI.java`
  - `GUI.class`
  - `GUI$1.class`
  - `GUI$2.class`

## Java 1.2 (1998)

```java
public class GUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Close me!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JButton button = new JButton("Click me to see the current date!");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                button.setText(new Date().toString());
            }
        });
        frame.add(button);

        frame.pack();
        frame.show();
    }
}
```

- `GUI.java`
  - `GUI.class`
  - `GUI$1.class`

## Java 8 (2014)

Lambdas are more concise than anonymous inner classes:

```java
button.addActionListener((ActionEvent event) -> {
    button.setText(new Date().toString());
});
```

Lambda parameter types can be inferred by the compiler:

```java
button.addActionListener((event) -> {
    button.setText(new Date().toString());
});
```

Parentheses around a single lambda parameter name are optional:

```java
button.addActionListener(event -> {
    button.setText(new Date().toString());
});
```

The curly braces around a single statement are optional:

```java
button.addActionListener(event -> button.setText(new Date().toString()));
```

- `GUI.java`
  - `GUI.class`
- 📺 [Lambdas in Java: A Peek under the Hood](https://www.youtube.com/watch?v=MLksirK9nnE)

## Case study: Collections

### Java 1.4

```java
public static List adultDomains(List persons) {
    Set domains = new HashSet();
    for (Iterator it = persons.iterator(); it.hasNext(); ) {
        Person person = (Person) it.next();
        if (person.isAdult()) {
            domains.add(person.getEmail().getDomain());
        }
    }
    List list = new ArrayList(domains);
    Collections.sort(list);
    return list;
}
```

### Java 5

```java
public static List<String> adultDomains(List<Person> persons) {
    Set<String> domains = new HashSet<String>();
    for (Person person : persons) {
        if (person.isAdult()) {
            domains.add(person.getEmail().getDomain());
        }
    }
    List<String> list = new ArrayList<String>(domains);
    Collections.sort(list);
    return list;
}
```

### Java 7

```java
public static List<String> adultDomains(List<Person> persons) {
    Set<String> domains = new HashSet<>();
    for (Person person : persons) {
        if (person.isAdult()) {
            domains.add(person.getEmail().getDomain());
        }
    }
    List<String> list = new ArrayList<>(domains);
    Collections.sort(list);
    return list;
}
```

### Java 8

```java
public static List<String> adultDomains(List<Person> persons) {
                // source:
    return persons.stream()                           // Stream<Person>
                // intermediate operations:
                  .filter(person -> person.isAdult())
                  .map(person -> person.getEmail())   // Stream<Email>
                  .map(email -> person.getDomain())   // Stream<String>
                  .sorted()
                  .distinct()
                // terminal operation:
                  .toList();                          // List<String>
}
```

excerpt from `Stream<T>` Javadoc:

> - A **stream pipeline** consists of:
>   - a **source**
>   - zero or more **intermediate operations** (which transform a stream into another stream)
>   - a **terminal operation** (which produces a result or side-effect)
> - Streams are **lazy**:
>   - computation on the source data is only performed when the terminal operation is initiated
>   - source elements are consumed only as needed

### Streams are lazy

```java
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;


Stream.iterate(ONE, x -> x.add(TWO))  // 1, 3,  5,  7,  9, ...
      .map(x -> x.multiply(x));       // 1, 9, 25, 49, 81, ...
// How often is x.multiply(x) called?


Stream.iterate(ONE, x -> x.add(TWO))
      .map(x -> x.multiply(x))
// How often is x.multiply(x) called?
      .forEach(x -> System.out.println(x) + " should be odd");


Stream.iterate(ONE, x -> x.add(TWO))
      .map(x -> x.multiply(x))
// How often is x.multiply(x) called?
      .anyMatch(x -> x.mod(TWO).equals(ONE));


Stream.iterate(ONE, x -> x.add(TWO))
      .map(x -> x.multiply(x))
// How often is x.multiply(x) called?
      .allMatch(x -> x.mod(TWO).equals(ONE));


Stream.iterate(ONE, x -> x.add(TWO))
      .map(x -> x.multiply(x))
// How often is x.multiply(x) called?
      .limit(1000000)
      .allMatch(x -> x.mod(TWO).equals(ONE));





// 0 ∞ 1 ∞ 1m
```

### Method references

Simple lambdas can often be replaced with method references:

```java
public static List<String> adultDomains(List<Person> persons) {

    return persons.stream()                // Stream<Person>
                  .filter(Person::isAdult)
                  .map(Person::getEmail)   // Stream<Email>
                  .map(Email::getDomain)   // Stream<String>
                  .sorted()
                  .distinct()
                  .toList();               // List<String>
}


public static Map<String, List<Email>> emailsByDomain(List<Person> persons) {
    return persons.stream()
                  .map(Person::getEmail)
                  .collect(Collectors.groupingBy(Email::getDomain));
}
```

> [Effective Java 3rd Edition](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097)
> Item 43: Prefer method references to lambdas
>
> | Method Ref Type   | Example                  | Lambda Equivalent               |
> | ----------------- | ------------------------ | ------------------------------- |
> | Static            | `Integer::parseInt`      | `str -> Integer.parseInt(str)`  |
> | Bound             | `Instant.now()::isAfter` | `Instant then = Instant.now();` |
> |                   |                          | `t -> then.isAfter(t)`          |
> | Unbound           | `String::toLowerCase`    | `str -> str.toLowerCase()`      |
> | Class Constructor | `TreeMap<K, V>::new`     | `() -> new TreeMap<K, V>()`     |
> | Array Constructor | `int[]::new`             | `len -> new int[len]`           |

## more Stream examples

### Freditor

```java
private List<Path> sortedFiles() {
    try (Stream<Path> applicationFiles = Files.list(applicationDirectory)) {
        return applicationFiles
                .filter(Files::isRegularFile)
                .filter(TabbedEditors::fileHasPlausibleSize)  // 0 < size < 100_000
                .filter(file -> !isLegacyBackupFile(file))    // 27base64digits.txt
                .sorted()
                .collect(Collectors.toList());
    } catch (IOException directoryAbsent) {
        return Collections.emptyList();
    }
}
```

### Pangit

```java
public static Stream<GitBlob> findGitBlobs(Path root, Consumer<GitBlob> gitBlobConsumer) throws IOException {
    return Files.walk(root)                   // Stream<Path>
                .filter(GitBlob::isGitObject)
                .map(GitBlob::gitBlobOrNull)  // Stream<GitBlob>
                .filter(Objects::nonNull)
                .peek(gitBlobConsumer)
                .sorted();
}
```

### Moby Dick

```java
Path    path    = Path.of(System.getProperty("user.home"), "Downloads", "2701-0.txt");
String  content = Files.readString(path);

Pattern pattern = Pattern.compile("\\p{IsAlphabetic}+");
Matcher matcher = pattern.matcher(content);

String[] words  = matcher.results()                // Stream<MatchResult>
                         .map(MatchResult::group)  // Stream<String>
                         .toArray();               // String[]
```

### Various

```java
double[] randomNumbers = DoubleStream.generate(new Random()::nextDouble)
                                     .limit(10)
                                     .toArray();
```

```java
int[] powersOfTwo = IntStream.iterate(1, x -> x * 2)
                             .takeWhile(x -> x > 0)
                             .toArray()
```

```java
int[] codePoints = IntStream.rangeClosed('a', 'z')
                            .toArray();                  // { 97, 98, 99, ..., 122}

String s = new String(codePoints, 0, codePoints.length); // "abc...z"
```

> **Exercise:**
> 1. The 10 random numbers should range from -1 to +1
> 2. Instead of 31 powers of two, calculate 64, including `-9223372036854775808`
> 3. The string should contain all 52 letters (upper and lower case)
>    - Hint: `IntStream.concat`

## Case study: Nullable references

![](img/hoare.jpg)

> **Tony Hoare:**
> I call it my billion-dollar mistake.
> It was the invention of the null reference in 1965.  
>
> I was designing the first comprehensive type system for references in an object oriented language (ALGOL W).
> My goal was to ensure that all use of references should be absolutely safe, with checking performed automatically by the compiler.
>
> But I couldn't resist the temptation to put in a null reference, simply because it was so easy to implement.
> This has led to innumerable errors, vulnerabilities, and system crashes, which have probably caused a billion dollars of pain and damage in the last forty years.

### Java 7

```java
public Person findFirstPersonWithAge(int age);
```

What if there is no person with the given age? Should the method...
- ...return null?
- ...return a “default Person”?
- ...throw an exception?

One approach is to return null and let the caller deal with the situation:

```java
public Person findFirstPersonWithAge(int age) {
    for (Person person : persons) {
        if (person.getAge() == age) {
            return person;
        }
    }
    return null;
}
```

But what if the caller is not aware of the potential null reference?

```java
String name = party.findFirstPersonWithAge(42).getName();
                                           // ^ ticking time bomb
```

If the caller is aware of the potential null reference, they can deal with it as they see fit:

```java
Person person = party.findFirstPersonWithAge(42);
String name;
if (person != null) {
    name = person.getName();
} else {
    // null name:
    name = null;

    // default name:
    name = "";

    // exception:
    throw new Exception("...");
}
```

### Java 8

`Optional<Person>` makes it clear that `findPersonWithAge` might return no Person:

```java
public Optional<Person> findFirstPersonWithAge(int age) {
    return persons.stream()                                  // Stream<Person>
                  .filter(person -> person.getAge() == age)
                  .findFirst();                              // Optional<Person>
}
```

Now the caller is forced to deal with the situation:

```java
String name = party.findFirstPersonWithAge(42)  // Optional<Person>
                   .map(Person::getName)        // Optional<String>

                   // null name:
                   .orElse(null);               // String

                   // default name:
                   .orElse("");                 // String

                   // exception:
                   .orElseThrow();              // String
```

excerpt from `Optional<T>` Javadoc:

> - A container object which may or may not contain a non-null value.
> - Optional is primarily intended for use as a **method return type** where there is a clear need to represent “no result”, and where using null is likely to cause errors.
> - A variable whose type is Optional should **never itself be null**; it should always point to an Optional instance.

```java
public final class Optional<T> {

    private final T value;

    // Factory methods

    public static <T> Optional<T> empty();

    public static <T> Optional<T> of(T value);

    public static <T> Optional<T> ofNullable(T value);

    // Intermediate methods

    public Optional<T> filter(Predicate<? super T> predicate);

    public <U> Optional<U> map(Function<? super T, ? extends U> mapper);

    public <U> Optional<U> flatMap(Function<? super T, ? extends Optional<? extends U>> mapper);

    public Optional<T> or(Supplier<? extends Optional<? extends T>> supplier);

    // Conditional methods

    public void ifPresent(Consumer<? super T> action);

    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction);

    // Extractor methods

    public T orElse(T other);

    public T orElseGet(Supplier<? extends T> supplier);

    public T orElseThrow(); // throws NoSuchElementException

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X;

    // Interoperability

    public Stream<T> stream();

    public boolean isEmpty();

    public boolean isPresent();

    public T get(); // throws NoSuchElementException

    // equals, hashCode, toString
}
```

### Freditor

```java
String[] lines = message.split("\n");

int      width = Arrays.stream(lines)             // Stream<String>
                       .mapToInt(String::length)  // IntStream
                       .max()                     // OptionalInt
                       .orElseThrow();            // int
```

## Case study: Comparator

### Java 7

```java
private static final Comparator<Person> compareAgeDescendingNameEmail = new Comparator<>() {
    /**
     * Compares its two arguments for order.
     * Returns a negative integer, zero, or a positive integer
     * as the first argument is less than, equal to, or greater than the second.
     */
    @Override
    public int compare(Person a, Person b) {
        int result = Integer.compare(b.getAge(), a.getAge());
        if (result == 0) {
            result = a.getName().compareTo(b.getName());
            if (result == 0) {
                result = a.getEmail().compareTo(b.getEmail());
            }
        }
        return result;
    }
};

public static void sortAgeDescendingNameEmail(List<Person> persons) {
    Collections.sort(persons, compareAgeDescendingNameEmail);
}
```

### Java 8

```java
private static final Comparator<Person> compareAgeDescendingNameEmail = Comparator
        .comparingInt(Person::getAge).reversed()
        .thenComparing(Person::getName)
        .thenComparing(Person::getEmail);

public static void sortAgeDescendingNameEmail(List<Person> persons) {
    persons.sort(compareAgeDescendingNameEmail);
}
```

> **Exercise:**
> - Search for `new Comparator` and/or `implements Comparator` in your project
> - Identify candidates for replacement with `Comparator.comparing`

## java.time

- Designed around ISO 8601 calendar system
- Heavily inspired by Joda Time
- Replacement for
  - `java.util.Date`
  - `java.util.Calendar`
  - `java.util.TimeZone`
  - `java.util.DateFormat`
- Plethora of immutable types for managing everything time-related:

| Type            | Zone         | Year | Month | Day | Hour | Minute | Second | Nanosecond |
| --------------- | ------------ | ---- | ----- | --- | ---- | ------ | ------ | ---------- |
| `Instant`       | UTC          |      |       |     |      |        | ✓      | ✓          |
| `LocalDate`     |              | ✓    | ✓     | ✓   |      |        |        |            |
| `LocalTime`     |              |      |       |     | ✓    | ✓      | ✓      | ✓          |
| `LocalDateTime` |              | ✓    | ✓     | ✓   | ✓    | ✓      | ✓      | ✓          |
| `OffsetTime`    | `ZoneOffset` |      |       |     | ✓    | ✓      | ✓      | ✓          |
| `OffsetDateTime`| `ZoneOffset` | ✓    | ✓     | ✓   | ✓    | ✓      | ✓      | ✓          |
| `ZonedDateTime` | `ZoneId`     | ✓    | ✓     | ✓   | ✓    | ✓      | ✓      | ✓          |
| `Year`          |              | ✓    |       |     |      |        |        |            |
| `YearMonth`     |              | ✓    | ✓     |     |      |        |        |            |
| `Month`         |              |      | ✓     |     |      |        |        |            |
| `MonthDay`      |              |      | ✓     | ✓   |      |        |        |            |
| `DayOfWeek`     |              |      |       | (✓) |      |        |        |            |
| `Duration`      |              |      |       |     | (✓)  | (✓)    | (✓)    | (✓)        |
| `Period`        |              | (✓)  | (✓)   | (✓) |      |        |        |            |

- (`DayOfWeek` has 7 values instead of 31)
- (`Duration` and `Period` store differences)

### How many days between two dates?

```java
LocalDate java7Release = LocalDate.of(2011, Month.JULY, 28);
LocalDate java8Release = LocalDate.of(2014, 3, 18);

Period pause = java7Release.until(java8Release);
System.out.println(pause); // P2Y7M18D
System.out.printf("There were %d years, %d months and %d days between Java 7 and Java 8.%n",
        pause.getYears(), pause.getMonths(), pause.getDays());

long days = ChronoUnit.DAYS.between(java7Release, java8Release);
System.out.printf("There were %d days between Java 7 and Java 8.%n", days);
```

### What about leap years?

```java
for (int y = LocalDate.now().getYear(), bound = y + 12; y < bound; ++y) {
    Year year = Year.of(y);
    System.out.printf("%d is %s leap year.%n", y, year.isLeap() ? "a" : "not a");

    YearMonth february = year.atMonth(Month.FEBRUARY);
    System.out.printf("February %d has %d days, %d has %d days.%n",
        y, february.lengthOfMonth(), y, february.lengthOfYear());

    System.out.printf("February %d ends at %s.%n%n", y, february.atEndOfMonth());
}
```

### What about daylight saving time?

```java
ZoneId berlin = ZoneId.of("Europe/Berlin");
ZonedDateTime saturday = ZonedDateTime.of(LocalDateTime.of(2018, Month.OCTOBER, 27, 9, 0), berlin);
ZonedDateTime sundayAtEight = saturday.plusHours(24);
ZonedDateTime sundayAtNine = saturday.plusDays(1);
```

> **Exercise:**
> - Write a program that determines the 2 days this year affected by daylight saving time:
>   - Last Sunday in March
>   - Last Sunday in October
> - Write a program that determines your next 5 birthdays that fall on a Friday/Saturday/Sunday
>   - Would it work if you were born February 29th?
> - Write a program that determines the next Christmas holidays perfect for employees:
>   - None of December 24,25,26 fall on a Saturday/Sunday

# Java 9

## [JEP 222: jshell: The Java Shell (Read-Eval-Print Loop)](https://openjdk.org/jeps/222)

```
C:\Users\fred> jshell

|  Welcome to JShell -- Version 9
|  For an introduction type: /help intro

jshell> String s = "Java"
s ==> "Java"

jshell> s.concat("Script")
$2 ==> "JavaScript"

jshell> s
s ==> "Java"

jshell> $2
$2 ==> "JavaScript"
```

## Collection 'literals'

### ArrayList (Java 1.2)

```java
List<Integer> primes = new ArrayList<>();
primes.add(2);
primes.add(3);
primes.add(5);
primes.add(7);

primes.set(0, 1); // ok
primes.add(11);   // ok
```

### Array view (Java 1.2)

```java
List<Integer> primes = Arrays.asList(2, 3, 5, 7);

primes.set(0, 1); // ok
primes.add(11);   // java.lang.UnsupportedOperationException
```

### Read-only view (Java 1.2)

```java
List<Integer> primes = Collections.unmodifiableList(Arrays.asList(2, 3, 5, 7));

primes.set(0, 1); // java.lang.UnsupportedOperationException
primes.add(11);   // java.lang.UnsupportedOperationException
```

### Immutable lists (Java 9)

```java
List<Integer> primes = List.of(2, 3, 5, 7);

primes.set(0, 1); // java.lang.UnsupportedOperationException
primes.add(11);   // java.lang.UnsupportedOperationException
```

- `List.of` is overloaded for up to 10 arguments
  - More arguments are handled via varargs
  - `List.of()` always returns the same empty list
  - 1 or 2 elements do not require a backing array
- `List.copyOf` creates an immutable list from a source collection
  - If the source collection is already an immutable list, no copy is performed

### Immutable sets (Java 9)

```java
Set<Integer> primes = Set.of(2, 3, 5, 7);
```

- All `List.of` and `List.copyOf` bullet points also apply to `Set.of` and `Set.copyOf`

### Immutable maps (Java 9)

- For up to 10 mappings, pass the keys and values directly to `Map.of`:

```java
Map<String, String> capitals = Map.of(
        "China", "Peking",
        "Japan", "Tokio",
        "Kongo", "Kinshasa",
        "Russland", "Moskau",
        "Korea", "Seoul",
        "Indonesion", "Jakarta",
        "Mexiko", "Mexiko-Stadt",
        "Thailand", "Bangkok",
        "Peru", "Lima",
        "Vereinigtes Königreich", "London");
```

- For 11 or more mappings, use `Map.ofEntries`: 

```java
import static java.util.Map.entry;

Map<String, String> capitals = Map.ofEntries(
        entry("China", "Peking"),
        entry("Japan", "Tokio"),
        entry("Kongo", "Kinshasa"),
        entry("Russland", "Moskau"),
        entry("Korea", "Seoul"),
        entry("Indonesion", "Jakarta"),
        entry("Mexiko", "Mexiko-Stadt"),
        entry("Thailand", "Bangkok"),
        entry("Peru", "Lima"),
        entry("Vereinigtes Königreich", "London"),
        entry("Iran", "Teheran"));
```

- All `Set.of` and `Set.copyOf` bullet points also apply to `Map.of` and `Map.copyOf`
- The backing array contains the keys and values directly, not references to entry objects
  - Immutable maps require significantly less memory than `java.util.HashMap`

> **Exercise:**
> - Search for `new HashMap` in your test classes
> - Replace at least 1 HashMap with `Map.of` and make sure the test still works

# Java 10

## [JEP 286: Local-Variable Type Inference](https://openjdk.org/jeps/286)

```java
Connection connection = dataSource.getConnection();
PreparedStatement statement = connection.prepareStatement("select * from user where id = ?");
ResultSet resultSet = statement.executeQuery();
```

```java
var connection = dataSource.getConnection();
var statement = connection.prepareStatement("select * from user where id = ?");
var resultSet = statement.executeQuery();
```

- Local variables can be declared with `var` instead of a manifest type
- In that case, the compiler figures out the type from the initializer:

```java
var name = "Joshua";
    name = 42; // Type mismatch: cannot convert from int to String

var names = new ArrayList<String>();
    names = new LinkedList<String>(); // Type mismatch: cannot convert from LinkedList<String> to ArrayList<String>

var result; // cannot use `var` on variable without initializer
```

- Type inference is *not* dynamic typing!

> Nearly all other popular statically typed “curly-brace” languages, both on the JVM and off,
> already support some form of local-variable type inference:
> - C++ (`auto`),
> - C# (`var`),
> - Scala (`var`/`val`),
> - Go (declaration with `:=`).
>
> Java is nearly the only popular statically typed language that has not embraced local-variable type inference;
> at this point, this should no longer be a controversial feature.
> [JEP 286](https://openjdk.org/jeps/286)

### Forms of type inference before Java 10

```java
// Java 5: generic method parameters
Arrays.asList("hello", "world")
Arrays.<String>asList("hello", "world")

// Java 7: diamond operator
List<String> names = new ArrayList<>();
List<String> names = new ArrayList<String>();

// Java 8: lambda parameters
names.filter(name -> name.isEmpty())
names.filter((String name) -> name.isEmpty())

names.filter((Predicate<String>)((String name) -> name.isEmpty()))
```

# Java 11

## [JEP 323: Local-Variable Syntax for Lambda Parameters](https://openjdk.org/jeps/323)

- Java 10: `(dx, dy) -> dx*dx + dy*dy`
- Java 11: `(var dx, var dy) -> dx*dx + dy*dy`

## Distributions

- Since 2019, some OracleJDK distributions cost money
- This led to an explosion of competing distributions (and lots of confusion)

| Distribution                                             | Notes                          |
| -------------------------------------------------------- | ------------------------------ |
| https://adoptium.net                                     | formerly known as AdoptOpenJDK |
| https://aws.amazon.com/corretto                          |                                |
| https://www.azul.com/downloads                           | optionally with JavaFX         |
| https://bell-sw.com/pages/downloads                      | optionally with JavaFX         |
| https://developer.ibm.com/languages/java/semeru-runtimes |                                |
| https://www.microsoft.com/openjdk                        |                                |
| https://developers.redhat.com/products/openjdk/download  | requires registration          |
| https://sap.github.io/SapMachine                         |                                |
| https://www.oracle.com/java/technologies/downloads       | free of charge since Java 17   |
| https://jdk.java.net                                     | only current & early access    |

- The standard recommendation is https://adoptium.net
- All distributions are built from the same source code https://github.com/openjdk/jdk

## java.net.http

```java
public static String download(String url) throws IOException, InterruptedException {
    var client = HttpClient.newBuilder().build();

    var request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
                                                              // .ofByteArray()
    var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                                                              // .ofLines()
    if (response.statusCode() == HttpURLConnection.HTTP_OK) {
        return response.body();
    } else {
        throw new IOException("HTTP status code " + response.statusCode());
    }
}
```

# Java 14

## [JEP 361: Switch Expressions](https://openjdk.org/jeps/361)

```java
double daysPerMonth = 0;
switch (month) {
    case JANUARY:
    case MARCH:
    case MAY:
    case JULY:
    case AUGUST:
    case OCTOBER:
    case DECEMBER:
        daysPerMonth = 31;
        break;

    case APRIL:
    case JUNE:
    case SEPTEMBER:
    case NOVEMBER:
        daysPerMonth = 30;
        break;

    case FEBRUARY:
        daysPerMonth = 28.2425;
}
```

```java
double daysPerMonth = switch (month) {
    case JANUARY, MARCH, MAY, JULY, AUGUST, OCTOBER, DECEMBER -> 31;

    case APRIL, JUNE, SEPTEMBER, NOVEMBER -> 30;

    case FEBRUARY -> 28.2425;
};
```

# Java 15

## [JEP 378: Text Blocks](https://openjdk.org/jeps/378)

```
String js = "setTimeout(function () {\n" +
            "    console.log(\"Hello JavaScript!\");\n" +
            "}, 1000);\n";
```

```
String js = """
            setTimeout(function () {
                console.log("Hello JavaScript!");
            }, 1000);
            """;
```

# Java 16

## [JEP 395: Records](https://openjdk.org/jeps/395)

```java
public class Gebiet {
    private final int plz;
    private final String ort;

    public Gebiet(int plz, String ort) {
        Contract.checkBetween(0, plz, 99_999, "plz");
        this.plz = plz;
        this.ort = ort;
    }

    public int plz() {
        return plz;
    }

    public String ort() {
        return ort;
    }

    @Override
    public String toString() {
        // ...
    }

    @Override
    public boolean equals(Object obj) {
        // ...
    }

    @Override
    public int hashCode() {
        // ...
    }
}
```

```java
public record Gebiet(int plz, String ort) {
    public Gebiet {
        Contract.checkBetween(0, plz, 99_999, "plz");
    }
}
```

```java
// 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, ...
public static Stream<BigInteger> fibonacci() {

    record Pair(BigInteger a, BigInteger b) {
    }

    return Stream.iterate(
            new Pair(BigInteger.ZERO, BigInteger.ONE),

            old -> new Pair(old.b(), old.a().add(old.b()))
    ).map(Pair::a);            ////     ////        ////
}             ///
```

# Java 21

## [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)

> - Enable server applications written in the simple **thread-per-request** style to scale with near-optimal hardware utilization
> - Enable existing code that uses the `java.lang.Thread` API to adopt virtual threads with minimal change
> - Enable easy troubleshooting, **debugging**, and profiling of virtual threads with existing JDK tools

- Spring Boot 3.2+ `application.properties`:

```
spring.threads.virtual.enabled=true
```

- The following sections assume a single-threaded execution context
  - e.g. Swing GUI application

### before

```java
new Thread(() -> {
    // ...
    var response = client.send(request, HttpResponse.BodyHandlers.ofString());
    // ...
}).start();
```

```java
bigThreadPool.execute(() -> {
    // ...
    var response = client.send(request, HttpResponse.BodyHandlers.ofString());
    // ...
});
```

```java
// ...
var future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

future.thenAccept(response -> {
    // ...
}).exceptionally(throwable -> {
    // ...
});
```

### after

```java
Thread.startVirtualThread(() -> {
    // ...
    var response = client.send(request, HttpResponse.BodyHandlers.ofString());
    // ...
});
```

- Virtual threads are very cheap
- Thread pools no longer necessary
- Async APIs no longer necessary

# Java 25

## [JEP 512: Compact Source Files and Instance Main Methods](https://openjdk.org/jeps/512)

```java
void main() {
    System.out.println("Hello, World!");
}
```

# Preview

## [JEP 401: Value Classes and Objects (Preview)](https://openjdk.org/jeps/401)

> Enhance the Java object model with value objects;
> class instances that
> - have only final instance fields
> - and lack object identity
