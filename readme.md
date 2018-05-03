# Curriculum Parser  

This is a tool for parsing the public open data from [opendata.skolverket.se](http://opendata.skolverket.se/)
 
If you just want to see the parsed result, use the [curriculum-parser-service](https://github.com/stefan-jonasson/curriculum-parser-service) directly.  
 
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

Other dependencies used in the parser is: 
- [jsoup](https://jsoup.org/)
- [Apache Commons Compress](http://commons.apache.org/proper/commons-compress/)

## Usage
This project is not yet published to maven central or similar. 
To include it to your project use [Jitpack ](https://jitpack.io/) to include the latest release directly from GitHub. 
```
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
dependencies {
    ...
    compile 'com.github.stefan-jonasson:curriculum-parser:<release>'    
```

The entry point to the parser is the Syllabus class.
The class takes two constructor parameters, a syllabus type and a directory reference (java.io.File).
The if no directory is supplied the current temporary directory will be used and the necessary files will automatically be downloaded from skolverket. 

If you would like to use a fixed version of skolverkets files just put the opendata files in a directory and supply a reference to that directory.

Get the representation of the parsed subjects and courses by invoking the getSubjects() method.  
 ```$kotlin
    val syllabus = Syllabus(<SyllabusType>, [<workdir, defaults to system temp dir>])
    // Get all subjects in for the loaded syllabys type
    sullabus.getSubjects()
 ```
 
Example code in Java for loading the parsed representation for "Grundskolan": 
```$java
package com.company.xxx
import org.edtech.curriculum.*;

import java.util.List;

public class MyApp {

    public static void main (String[] args) {
        List<Subject> subjects = new Syllabus(syllabusType.GR).getSubjects()
        subjects.forEach(subject -> {                 
            System.out.println("Subject name: " + subject.getName() );                
            subject.getCourses().forEach(course -> {
                System.out.println("Course: " + course.getName());
                course.getKnowledgeRequirement().forEach(kn -> {
                    System.out.println(String.format("Knowledge requirement(%d): %s", kn.getNo(), kn.getText()));
                    kn.getKnowledgeRequirementChoice().forEach((gradeStep, text) -> {
                        System.out.println(String.format("[%s]: %s", gradeStep.name(), text));
                    });
                });                    
            });
        });        
    }
}
```
### Generating to java docs
Javadoc is generated with dokka, to build your local version of the api documentation just run:
``gradle dokka``

### Running the unittests
To run the included tests: `gradle test`
