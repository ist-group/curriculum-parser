package org.edtech.curriculum

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class SyllabusTest {
    private val dataDir = File("./src/test/resources/opendata/")

    @Test
    fun testGetSubjectsHtmlGR() {
        testGetSubjectsHtml(SyllabusType.GR)
    }
    @Test
    fun testGetSubjectsHtmlGRS() {
        testGetSubjectsHtml(SyllabusType.GRS)
    }
    @Test
    fun testGetSubjectsHtmlGRSAM() {
        testGetSubjectsHtml(SyllabusType.GRSAM)
    }
    @Test
    fun testGetSubjectsHtmlGRSPEC() {
        testGetSubjectsHtml(SyllabusType.GRSPEC)
    }
    @Test
    fun testGetSubjectsHtmlGY() {
        testGetSubjectsHtml(SyllabusType.GY)
    }
    @Test
    fun testGetSubjectsHtmlGYS() {
        testGetSubjectsHtml(SyllabusType.GYS)
    }
    @Test
    fun testGetSubjectsHtmlVUXGR() {
        testGetSubjectsHtml(SyllabusType.VUXGR)
    }
/*    @Test
    fun testGetSubjectsHtmlSFI() {
        testGetSubjectsHtml(SyllabusType.SFI)
    }*/
    @Test
    fun testGetSubjectsGR() {
        testGetSubjects(SyllabusType.GR)
    }
    @Test
    fun testGetSubjectsGRS() {
        testGetSubjects(SyllabusType.GRS)
    }
    @Test
    fun testGetSubjectsGY() {
        testGetSubjects(SyllabusType.GY)
    }
    @Test
    fun testGetSubjectsGYS() {
        testGetSubjects(SyllabusType.GYS)
    }
    @Test
    fun testGetSubjectsVUXGR() {
        testGetSubjects(SyllabusType.VUXGR)
    }
/*    @Test
    fun testGetSubjectsSFI() {
        testGetSubjects(SyllabusType.SFI)
    }
*/
    private fun testGetSubjectsHtml(syllabusType: SyllabusType) {
        dataDir.listFiles()
            .filter{ it.isDirectory }
            .forEach {
                Syllabus(syllabusType, it).subjectHtml.forEach {
                    assertTrue("${syllabusType.name}/${it.code} has no name", it.name.isNotEmpty())
                    assertTrue("${syllabusType.name}/${it.name} has no code", it.code.isNotEmpty())
                    assertTrue("${syllabusType.name}/${it.skolfsId} has no skolfsId", it.skolfsId.isNotEmpty())
                    assertTrue("${syllabusType.name}/${it.name} has no courses", it.courses.isNotEmpty())
                    assertTrue("${syllabusType.name}/${it.name} has no purposes", it.purposes.isNotEmpty())

                    it.courses.forEach { courseHtml ->
                        // Only require kr when passed the lowest grades
                        if (!courseHtml.year.startsWith("1-")) {
                            assertTrue("${courseHtml.code}/${courseHtml.name} has no knowledgeRequirements", courseHtml.knowledgeRequirement.isNotEmpty())
                        }
                        assertTrue("${courseHtml.code}/${courseHtml.name} has no centralContents", courseHtml.centralContent.isNotEmpty())
                        assertTrue("${courseHtml.code}/${courseHtml.name} has malformed centralContents:\n ${courseHtml.centralContent}", courseHtml.centralContent.contains(Regex("<li>|<p>–")))
                        assertTrue("${courseHtml.code}/${courseHtml.name} has no code", courseHtml.code.isNotEmpty())
                        assertTrue("${courseHtml.code}/${courseHtml.name} has no name", courseHtml.name.isNotEmpty())

                        // Only check real courses
                        if (courseHtml.year.isEmpty() ) {
                            if (courseHtml.point.isEmpty()) {
                                fail("${courseHtml.code}/${courseHtml.name} has no points/year group")
                            }
                            assertTrue("${courseHtml.code}/${courseHtml.name} has no description", courseHtml.description.isNotEmpty())
                        }
                    }
                }
            }
    }


    @Test
    fun testDuplicateRequirementsGR() {
        dataDir.listFiles()
                .filter { it.isDirectory }
                .forEach {
                    Syllabus(SyllabusType.GR, it).subjectHtml.forEach {
                        it.courses.forEach { courseHtml ->
                            courseHtml.knowledgeRequirement.filter { entry -> entry.key != GradeStep.D && entry.key != GradeStep.B }.forEach { entry ->
                                val matchingCourse = it.courses.firstOrNull { c -> courseHtml != c && c.knowledgeRequirement[entry.key]?.contains(entry.value) ?: false }
                                assertNull("duplicate knowledge requirement found in ${it.name}[${it.code}] ${courseHtml.name}[${courseHtml.code}] => ${matchingCourse?.code}:\n${entry.value}", matchingCourse)
                            }
                        }
                    }
                }
    }

    @Test
    fun testDuplicateRequirementsGRS() {
        dataDir.listFiles()
            .filter{ it.isDirectory }
            .forEach {
                Syllabus(SyllabusType.GRS, it).subjectHtml.forEach {
                    it.courses.forEach { courseHtml ->
                        courseHtml.knowledgeRequirement.filter { entry -> entry.key != GradeStep.D && entry.key != GradeStep.B }.forEach { entry ->
                            val matchingCourse = it.courses.firstOrNull { c -> courseHtml != c && c.knowledgeRequirement[entry.key]?.contains(entry.value) ?: false }
                            assertNull("duplicate knowledge requirement found in ${it.name}[${it.code}] ${courseHtml.name}[${courseHtml.code}] => ${matchingCourse?.code}:\n${entry.value}" , matchingCourse)
                        }
                    }
                }
            }
    }

    private fun testPurpose(name: String, purposes: List<Purpose>) {
        assertTrue("$name has no purpose", purposes.isNotEmpty() )

        purposes.forEach {
            assertTrue( "Found empty purpose in $name", it.lines.isNotEmpty() || it.heading.isNotEmpty())
            assertNull( "Found empty purpose line in $name", it.lines.firstOrNull { it.trim().isEmpty() })
            if (it.type == PurposeType.BULLET) {
                assertTrue( "Bullet lists always needs a heading $name", it.heading.isNotEmpty())
            }
        }
    }

    private fun testCentralContent(name: String, centralContents: List<CentralContent>) {
        assertTrue("$name has no central contents", centralContents.isNotEmpty() )

        centralContents.forEach {
            assertTrue( "Found empty central contents in $name", it.lines.isNotEmpty() || it.heading.isNotEmpty())
            assertNull( "Found empty central contents line in $name", it.lines.firstOrNull { it.trim().isEmpty() })
        }

       assertTrue( "all central contents are empty $name", centralContents.any{ it.lines.isNotEmpty()} )
    }

    private fun testGetSubjects(syllabusType: SyllabusType) {
        dataDir.listFiles()
                .filter{ it.isDirectory }
                .forEach {
                    Syllabus(syllabusType, it).getSubjects().forEach {
                        assertTrue("${syllabusType.name}/${it.name} has no name", it.name.isNotEmpty())
                        assertTrue("${syllabusType.name}/${it.name} has no skolfsId", it.skolfsId.isNotEmpty())
                        assertTrue("${syllabusType.name}/${it.name} has no code", it.code.isNotEmpty())
                        assertTrue("${syllabusType.name}/${it.name} has no courses", it.courses.isNotEmpty())
                        testPurpose("${syllabusType.name}/${it.name}", it.purposes)

                        it.courses.forEach { course ->
                            testCentralContent("${course.code}/${course.name}", course.centralContent)
                            if (course.year?.end ?: 0 > 3) {
                                assertTrue("${course.code}/${course.name} has no knowledgeRequirements", course.knowledgeRequirementParagraphs.isNotEmpty())
                            }
                            assertTrue("${course.code}/${course.name} has no code", course.code.isNotEmpty())
                            assertTrue("${course.code}/${course.name} has no name", course.name.isNotEmpty())
                            // Only check real courses
                            if (course.year == null) {
                                if (course.point == null) {
                                    fail("${course.code}/${course.name} has no points/year group")
                                }
                                assertTrue("${course.code}/${course.name} has no description", course.description.isNotEmpty())
                                if (course.point != null) {
                                    assertTrue("${course.code}/${course.name} has no points", course.point!! > 0)
                                } else {
                                    fail("${course.code}/${course.name} has no points")
                                }
                            }
                        }
                    }
                }
    }
}
