## Simple Java Analyzer

A rather simple analyzer for Java programming language. It can detect the following code smells:

1. Identical "then" and "else" branches in if-then-else statements
2. Synchronization on a non-final field
3. Syncrhonization on a local variable (i.e. defined in a method's scope)

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
Range: (line 19,col 7)-(line 21,col 7)
Code: synchronized (a) {
    System.out.println("test");
}
Verdict: Synchronizing on a local variable a
File: src/test/java/Test.java
Class: Test
Method: public static void main3(String[] args)
Range: (line 27,col 7)-(line 29,col 7)
Code: synchronized (b) {
    System.out.println("test");
}
Verdict: Synchronizing on a non-final variable b
```
