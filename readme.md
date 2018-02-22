# Curriculum Parser  

This is a tool for parsing the public open data from [opendata.skolverket.se](http://opendata.skolverket.se/)
 
The parser works by parsing the HTML contained in the XML files from skolverket. 
It tries to match the HTML structure into usable types that can be imported into other applications. 
Non trivial parts is the knowledge requirement parsing, where the parser matches the requirement between the grade levels into logical blocks. 
The parser works by comparing each line at the E leverl against the other levels, when a matching line is found all unmatched lines will be assigned to the previous block. 

```
 E     C   A       E    C    A
1111 1111 1111    1111 1111 1111  
2222 YYYY YYYY         YYYY YYYY
3333 2222 2222    --------------
4444 3333 ZZZZ    2222 2222 2222
     4444 3333 =>           ZZZZ
          4444    --------------
                  3333 3333 3333
                  --------------
                  4444 4444 4444
```
Y is a line that extends the first requirement line in the in C and A grade levels. 
Z is a line that extends the second requirement line and exists only the A grade level.

## Platform and dependencies
This library is based on [Kotlin](https://kotlinlang.org/) so it needs to be downloaded and installed. 

### Build dependencies
[Gralde](https://gradle.org/) is used for dependency/build management.  
You can download released versions and nightly build artifacts from: https://gradle.org/downloads

Other dependencies used for building the parser is: 
-  [jsoup](https://jsoup.org/)
-  [java-string-similarity](https://github.com/tdebatty/java-string-similarity)

## Usage
The library is not yet published to maven central or similar, to be able to include it in your project, first add it to a local maven repository by typing:
``gradle publishToMavenLocal``

To the project to be using the library add 
```
repositories {
    ...
    mavenLocal()
}
dependencies {
    ...
    compile("org.edtech:curriculum-parser:0.0.1")
```

When the library is included in your dependencies use it by loading a file for skolverkets opendata and extract the information with the `SubjectParser`
```$java
package com.company.xxx
import org.edtech.curriculum.*;

import java.io.File;
import java.util.List;

public class MyApp {

    public static void main (String[] args) {
        if (args.length > 0) {
            SubjectParser sp = new SubjectParser(new File(args[0]));
            Subject subject = sp.getSubject();

            List<Course> courses = sp.getCourses();
            System.out.println("Subject name: " + subject.getName() );
            if (courses != null) {
                for (Course course: courses) {
                    System.out.println("Course: " + course.getName());

                    course.getKnowledgeRequirement().forEach(kn -> {
                        System.out.println(String.format("Knowledge requirement(%d): %s", kn.getNo(), kn.getText()));
                        kn.getKnowledgeRequirementChoice().forEach((gradeStep, text) -> {
                            System.out.println(String.format("[%s]: %s", gradeStep.name(), text));
                        });
                    });
                }
            }
        } else {
            System.out.println("You have to supply an subject xml file as the first parameter");
        }
    }
}

```
### Generating to java docs
Javadoc is generated with dokka, to build your local version of the api dokumentation just run:
``gradle dokka``
### Running the unittests
To run the included tests: `gradle test`
