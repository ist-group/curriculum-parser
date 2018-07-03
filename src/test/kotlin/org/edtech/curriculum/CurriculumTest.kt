package org.edtech.curriculum

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class CurriculumTest {
    private val dataDir = File("./src/test/resources/opendata/")

    @Test
    fun testGetSubjectsHtmlGR() {
        testGetSubjectsHtml(SchoolType.GR)
    }
    @Test
    fun testGetSubjectsHtmlGRS() {
        testGetSubjectsHtml(SchoolType.GRS)
    }
    @Test
    fun testGetSubjectsHtmlGRSAM() {
        testGetSubjectsHtml(SchoolType.GRSAM)
    }
    @Test
    fun testGetSubjectsHtmlGRSPEC() {
        testGetSubjectsHtml(SchoolType.GRSPEC)
    }
    @Test
    fun testGetSubjectsHtmlGY() {
        testGetSubjectsHtml(SchoolType.GY)
    }
    @Test
    fun testGetSubjectsHtmlGYS() {
        testGetSubjectsHtml(SchoolType.GYS)
    }
    @Test
    fun testGetSubjectsHtmlVUXGR() {
        testGetSubjectsHtml(SchoolType.VUXGR)
    }
/*    @Test
    fun testGetSubjectsHtmlSFI() {
        testGetSubjectsHtml(org.edtech.curriculum.SchoolType.SFI)
    }*/
    @Test
    fun testGetSubjectsGR() {
        testGetSubjects(SchoolType.GR)
    }
    @Test
    fun testGetSubjectsGRS() {
        testGetSubjects(SchoolType.GRS)
    }
    @Test
    fun testGetSubjectsGY() {
        testGetSubjects(SchoolType.GY)
    }
    @Test
    fun testGetSubjectsGYS() {
        testGetSubjects(SchoolType.GYS)
    }
    @Test
    fun testGetSubjectsVUXGR() {
        testGetSubjects(SchoolType.VUXGR)
    }
/*    @Test
    fun testGetSubjectsSFI() {
        testGetSubjects(org.edtech.curriculum.SchoolType.SFI)
    }
*/
    private fun testGetSubjectsHtml(schoolType: SchoolType) {
        dataDir.listFiles()
            .filter{ it.isDirectory }
            .forEach {
                Curriculum(schoolType, it).subjectHtml.forEach {
                    assertTrue("${schoolType.name}/${it.code} has no name", it.name.isNotEmpty())
                    assertTrue("${schoolType.name}/${it.name} has no code", it.code.isNotEmpty())
                    assertTrue("${schoolType.name}/${it.skolfsId} has no skolfsId", it.skolfsId.isNotEmpty())
                    assertTrue("${schoolType.name}/${it.name} has no courses", it.courses.isNotEmpty())
                    assertTrue("${schoolType.name}/${it.name} has no purposes", it.purposes.isNotEmpty())

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
                    Curriculum(SchoolType.GR, it).subjectHtml.forEach {
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
                Curriculum(SchoolType.GRS, it).subjectHtml.forEach {
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
    }

    private fun testGetSubjects(schoolType: SchoolType) {
        dataDir.listFiles()
                .filter{ it.isDirectory }
                .forEach {
                    Curriculum(schoolType, it).getSubjects().forEach {
                        assertTrue("${schoolType.name}/${it.name} has no name", it.name.isNotEmpty())
                        assertTrue("${schoolType.name}/${it.name} has no skolfsId", it.skolfsId.isNotEmpty())
                        assertTrue("${schoolType.name}/${it.name} has no code", it.code.isNotEmpty())
                        assertTrue("${schoolType.name}/${it.name} has no courses", it.courses.isNotEmpty())
                        testPurpose("${schoolType.name}/${it.name}", it.purposes)

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
