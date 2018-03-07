package org.edtech.curriculum

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.edtech.curriculum.internal.fixCurriculumErrors
import org.edtech.curriculum.internal.getTextWithoutBoldWords
import org.edtech.curriculum.internal.textMatches
import org.jsoup.Jsoup
import org.junit.Assert.*
import org.junit.Test


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

    private fun textCourseCode(code: String, subjectName: String) {
        val classloader = Thread.currentThread().contextClassLoader
        val referenceObject:Course =  jacksonObjectMapper()
                .readValue(classloader.getResourceAsStream("GY/courses/$code.json"))
        val course = SkolverketFile.GY.openSubject(subjectName).getCourse(code)
        if (course != null) {
            assertEquals(referenceObject.name, course.name)
            assertEquals(referenceObject.code, course.code)
            assertEquals(referenceObject.centralContent, course.centralContent)
            assertArrayEquals(referenceObject.knowledgeRequirement?.toTypedArray(), course.knowledgeRequirement?.toTypedArray())
        } else {
            assertNotNull(course)
        }
    }

    @Test
    fun testSubject() {
        val classloader = Thread.currentThread().contextClassLoader
        val referenceObject:Subject =  jacksonObjectMapper()
                .readValue(classloader.getResourceAsStream("GY/subjects/Dansteknik.json"))
        assertEquals(SkolverketFile.GY.openSubject("Dansteknik").getSubject(), referenceObject)

    }

    @Test
    fun testDansgestaltning() {
        textCourseCode("DAGDAS0", "Dansgestaltning for yrkesdansare")
    }
    @Test
    fun testDansteknik() {
        textCourseCode("DAKKLA02", "Dansteknik for yrkesdansare")
    }

    @Test
    fun testTESPRO01() {
        textCourseCode("TESPRO01", "Tekniska system - VVS")
    }

    @Test
    fun testTravkunskap() {
        textCourseCode("TRVTRA01", "Travkunskap")
    }
    @Test
    fun testManniskansSprak() {
        textCourseCode("MÄKMÄK02", "Manniskans sprak")
    }

    @Test
    fun testMath() {
        textCourseCode("MATMAT00S", "Matematik")
        textCourseCode("MATMAT01b", "Matematik")
        textCourseCode("MATMAT01c", "Matematik")
        textCourseCode("MATMAT02a", "Matematik")
        textCourseCode("MATMAT02b", "Matematik")
    }
    @Test
    fun testMatlagningskunskap() {
        textCourseCode("MALMAL04", "Matlagningskunskap")
    }

    @Test
    fun testBildteori() {
        textCourseCode("BIDBIT0", "Bildteori")
    }

    @Test
    fun testSparfordon() {
        textCourseCode("SPOSPA0", "Sparfordon")
    }

    @Test
    fun textByggproduktionsledning() {
        textCourseCode("BYPRIT0", "Byggproduktionsledning")
    }

    @Test
    fun parseAllOpenDataStructures() {
         val skolverketFile = SkolverketFile.GY
         for (subjectName in skolverketFile.subjectNames()) {
             val subject = skolverketFile.openSubject(subjectName)
             for (course in subject.courses) {
                 // Get the fully parsed course
                 val fullCourse = subject.getCourse(course.code)
                 if (fullCourse != null) {
                     val knList = fullCourse.knowledgeRequirement
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
                     val cp = subject.getCourseParser(course.code)
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
                     }
                 }
             }
         }
    }
}
