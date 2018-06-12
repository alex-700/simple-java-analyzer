## Simple Java Analyzer

A rather simple analyzer for Java programming language. It can detect the following code smells:

1. Identical "then" and "else" branches in if-then-else statements
2. Synchronization on a non-final field
3. Synchronization on a local variable (i.e. defined in a method's scope)
4. Identical import in file
5. [Experimental] Unused parameters

### Usage instructions
You must have a reasonably recent version of [Maven](https://maven.apache.org/) and JDK 10 installed. Make sure that your `JAVA_HOME` environment variable points to JDK 10 in case you have multiple JDKs installed on your system. Your Java version from the `mvn --version` report should look like `1.10.*`.

To build the tool from existing sources, go to the project's home folder using the terminal and run:
```bash
  $ mvn clean compile assembly:single
```

Maven will generate a fatjar (i.e. a single jar file containing the tool along with all dependencies) under the `target` folder. You can use it from the terminal in the following fashion:
```bash
  $ java -jar target/analyzer-1.0-SNAPSHOT-jar-with-dependencies.jar src/test/java
```

To better analyze maven projects run it in your project folder, before start analyzing  
```bash
  $ mvn dependency:copy-dependencies
``` 

The tool will generate a report and print to the standard output. It gives the following output for Java sources from `src/test/java`.
```
File: src/test/java/Test.java
Verdict: Same import java.util.Scanner in (line 1,col 1)-(line 1,col 25) and (line 2,col 1)-(line 2,col 25)
File: src/test/java/Test.java
Class: Test
Method: public static void main(String[] args)
Range: (line 9,col 9)-(line 13,col 9)
Code: if (a == 0) {
    b = in.nextInt();
} else {
    b = in.nextInt();
}
Verdict: Equivalent if branches
File: src/test/java/Test.java
Class: Test
Method: public static void main2(String[] args)
Range: (line 19,col 9)-(line 21,col 9)
Code: synchronized (a) {
    System.out.println("test");
}
Verdict: Synchronizing on a local variable a
File: src/test/java/Test.java
Class: Test
Method: public static void main3(String[] args)
Range: (line 27,col 9)-(line 29,col 9)
Code: synchronized (b) {
    System.out.println("test");
}
Verdict: Synchronizing on a non-final variable b
File: src/test/java/Test.java
Class: Test
Method: public static int unused(int a)
Range: (line 32,col 30)-(line 32,col 34)
Code: int a
Verdict: a is unused
File: src/test/java/Test.java
Class: Test
Method: public static int unused(int a, int b)
Range: (line 36,col 37)-(line 36,col 41)
Code: int b
Verdict: b is unused
File: src/test/java/Test.java
Class: Test
Method: int bar(int a, int b)
Range: (line 43,col 26)-(line 43,col 30)
Code: int b
Verdict: b is unused
```
### Examples 
The report of static analysis of [guava](https://github.com/google/guava) 
can be found in [examples/guava-report.txt](examples/guava-report.txt)