package org.edtech.myapp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.edtech.curriculum.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class CliTool {

    public static void main (String[] args) throws IOException {
        if (args.length > 0) {
            SubjectParser sp = new SubjectParser(new File(args[0]));
            Subject subject = sp.getSubject();
            ObjectMapper mapper = new ObjectMapper();
            if (args.length > 1 && Objects.equals(args[1], "-c")) {
                System.out.println(
                        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sp.getCourses())
                );
            } else {
                System.out.println(
                        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(subject)
                );
            }

        } else {
            System.out.println("Not enough arguments, supply a filename for the .xml file from http://opendata.skolverket.se");
            System.out.println("Params: <file.xml> [-c]");
            System.out.println("-c\t Export a course json instead of a subject json file");
        }
    }
}
