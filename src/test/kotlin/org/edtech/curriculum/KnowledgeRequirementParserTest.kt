package org.edtech.curriculum

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.edtech.curriculum.internal.CourseParser
import org.edtech.curriculum.internal.fixCurriculumErrors
import org.edtech.curriculum.internal.getTextWithoutBoldWords
import org.edtech.curriculum.internal.textMatches
import org.jsoup.Jsoup
import org.junit.Assert.*
import org.junit.Test
import java.io.File


class KnowledgeRequirementParserTest {

    @Test
    fun testTextMatches() {
        assertTrue(textMatches("Eleven förhåller sig konstnärligt till rörelsevokabulär samt varierar och utvecklar rörelseuttryck utifrån",
                               "Eleven förhåller sig konstnärligt till rörelsevokabulär och varierar, och utvecklar rörelseuttryck efter.")
        )
        assertTrue(textMatches("Eleven planerar och organiserar med handledare matlagning.",
                               "Eleven planerar och organiserar matlagning.")
        )
        assertTrue(textMatches("Dessutom beskriver eleven <strong>översiktligt <strong>både enkla och någon mer komplex </strong></strong>språklig struktur i bekanta och mindre bekanta språk samt drar <strong>enkla </strong>slutsatser även i förhållande till sitt modersmål.",
                               "Dessutom beskriver eleven <strong>utförligt både enkla och några mer komplexa </strong>språkliga strukturer i bekanta och mindre bekanta språk samt drar <strong>välgrundade </strong>slutsatser även i förhållande till sitt modersmål.")
        )
        assertFalse(textMatches(" Vid arbete i <strong>bekanta</strong> situationer använder eleven <strong>med säkerhet</strong> instruktioner och utrustningsbeskrivningar.",
                " I arbetet använder eleven i <strong>nya</strong> situationer <strong>med säkerhet </strong>instruktioner och utrustningsbeskrivningar")
        )
    }

    @Test
    fun testGetTextWithoutBoldWords() {
        assertEquals("Eleven förhåller sig konstnärligt till rörelsevokabulär samt varierar och utvecklar rörelseuttryck utifrån ",
                getTextWithoutBoldWords("Eleven förhåller sig <strong>med viss säkerhet </strong>konstnärligt till rörelsevokabulär samt varierar och utvecklar rörelseuttryck utifrån <strong>instruktioner</strong>")
        )
        assertEquals("Eleven förhåller sig konstnärligt till rörelsevokabulär och varierar, och utvecklar rörelseuttryck efter .",
                getTextWithoutBoldWords("Eleven förhåller sig <strong>med god säkerhet </strong>konstnärligt till rörelsevokabulär och varierar, <strong>undersöker </strong>och utvecklar<strong> konsekvent </strong>rörelseuttryck efter <strong>olika krav</strong>.")
        )
    }

    @Test
    fun testAgainstJsonFiles() {
        val mapper = ObjectMapper()

        for (dir in File("./src/test/resources").listFiles()) {
            val skolverketFile = SkolverketFile.valueOf(dir.name)
            val subjectMap: MutableMap<String, Subject> = HashMap()
            val coursesMap: MutableMap<String, Course> = HashMap()

            for (subjectName in skolverketFile.subjectNames()) {
                val subjectParser = skolverketFile.openSubject(subjectName)
                subjectMap[subjectName] = subjectParser.getSubject()
                subjectParser.courses.forEach { coursesMap[it.code] = it }
            }

            val subjectDir = File("./src/test/resources/${skolverketFile.name}/subjects")
            if (!subjectDir.isDirectory) fail("${subjectDir.absolutePath} is not a directory")

            for (file in File("./src/test/resources/${skolverketFile.name}/subjects").listFiles()) {
                if (!file.name.endsWith(".json")) continue
                val subjectName = file.name.split(".").first()
                val parsedSubject = subjectMap[subjectName]
                if (parsedSubject == null) {
                    fail("No subject $subjectName for file ${file.absolutePath}")
                } else {
                    val expected = file.readText()
                    val actual = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedSubject)
                    assertEquals("Difference for subject $subjectName", expected, actual)
                }
            }

            val courseDir = File("./src/test/resources/${skolverketFile.name}/courses")
            if (!courseDir.isDirectory) fail("${courseDir.absolutePath} is not a directory")

            for (file in courseDir.listFiles()) {
                if (!file.name.endsWith(".json")) continue
                val courseCode = file.name.split(".").first()
                val parsedCourse = coursesMap[courseCode]
                if (parsedCourse == null) {
                    fail("No course $courseCode for file ${file.absolutePath}")
                } else {
                    val expected = file.readText()
                    val actual = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedCourse)
                    assertEquals("Difference for course $courseCode", expected, actual)
                }
            }
        }
    }

    @Test
    fun parseAllOpenDataStructures() {
         val skolverketFile = SkolverketFile.GY
         for (subjectName in skolverketFile.subjectNames()) {
             val subject = skolverketFile.openSubject(subjectName)
             for (course in subject.courses) {
                 // Get the fully parsed course
                 val knList = course.knowledgeRequirement
                 assertNotEquals("Knowledge Requirements cannot be empty", 0, knList?.size ?: 0)
                 val combined: MutableMap<GradeStep, StringBuilder> = HashMap()
                 if (knList != null) {
                     for(kn in knList) {
                         for ( (g,s) in kn.knowledgeRequirementChoice) {
                             if (combined.containsKey(g)) {
                                 combined[g]?.append(" ")?.append(s)
                             } else {
                                 combined[g] = StringBuilder(s)
                             }
                         }
                     }
                 }
                 /* TODO Reimplement with new test only logic
                 val cp = CourseParser(course.code)
                 for( (gradestep, text) in combined) {
                     val textExpected = fixCurriculumErrors(Jsoup.parse(cp.extractKnowledgeRequirementForGradeStep(gradestep)).select("p").html())
                             .replace("\n", " ")
                             .replace("  ", " ")
                             .replace("<strong> <italic>  .  </italic></strong>", ". ")
                             .replace(".<strong> ", ". <strong> ")
                             .replace(".</strong>", ". </strong>")
                             .replace(Regex("[.]([^ ])"), ". \$1")
                             .removeSuffix("<strong> </strong>")
                     assertEquals("course: ${subject.name}/${course.name}", textExpected.trim(), text.toString().replace("  ", " ").trim())
                 }*/

             }
         }
    }
}
