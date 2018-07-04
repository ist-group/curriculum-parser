package org.edtech.curriculum

import org.junit.Assert.*
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.opentest4j.TestAbortedException
import java.io.File

class CurriculumTest {
    private val dataDir = File("./src/test/resources/opendata/")

    private val schoolTypesToTest = SchoolType.values().filter {
        if (it != SchoolType.SFI) true else {
            println("Skipping SFI test")
            false
        }
    }

    @TestFactory
    fun testGetSubjects() = schoolTypesToTest.map { schoolType ->
        DynamicTest.dynamicTest(schoolType.name) {testGetSubjects(schoolType)}
    }

    @TestFactory
    fun testGetSubjectsHtml() = schoolTypesToTest.map { schoolType ->
        DynamicTest.dynamicTest(schoolType.name) {testGetSubjectsHtml(schoolType)}
    }

    /**
     * Gradelevel 9 has requirements
     * Gradelevel 6 has requirements except in GRS
     * No gradelevel => has requirements
     */
    private fun hasRequirements(year: String, schoolType: SchoolType): Boolean {
        return (year.isEmpty() || year.contains("6") && schoolType != SchoolType.GRS || year.contains("9"))
    }

    private fun hasRequirements(yearGroup: YearGroup?, schoolType: SchoolType): Boolean {
        return (yearGroup == null || yearGroup.end == 6 && schoolType != SchoolType.GRS || yearGroup.end == 9)
    }

    /**
     * Gradelevel 9 has requirements
     * Gradelevel 6 has requirements except in GRS
     * No gradelevel => has requirements
     */
    private fun hasRequirements(year: String, schoolType: SchoolType): Boolean {
        return (year.isEmpty() || year.contains("6") && schoolType != SchoolType.GRS || year.contains("9"))
    }

    private fun hasRequirements(yearGroup: YearGroup?, schoolType: SchoolType): Boolean {
        return (yearGroup == null || yearGroup.end == 6 && schoolType != SchoolType.GRS || yearGroup.end == 9)
    }

    private fun testGetSubjectsHtml(schoolType: SchoolType) {
        dataDir.listFiles()
            .filter{ it.isDirectory }
            .forEach {
                Curriculum(schoolType, it).subjectHtml.forEach { subjectHtml ->
                    assertTrue("${schoolType.name}/${subjectHtml.code} has no name", subjectHtml.name.isNotEmpty())
                    assertTrue("${schoolType.name}/${subjectHtml.name} has no code", subjectHtml.code.isNotEmpty())
                    assertTrue("${schoolType.name}/${subjectHtml.skolfsId} has no skolfsId", subjectHtml.skolfsId.isNotEmpty())
                    assertTrue("${schoolType.name}/${subjectHtml.name} has no courses", subjectHtml.courses.isNotEmpty())
                    assertTrue("${schoolType.name}/${subjectHtml.name} has no purposes", subjectHtml.purposes.isNotEmpty())

                    subjectHtml.courses.forEach { courseHtml ->
                        // Only require kr when passed the lowest grades
                        if (hasRequirements(courseHtml.year, schoolType)) {
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

       assertTrue( "all central contents are empty $name", centralContents.any{ it.lines.isNotEmpty()} )
    }

    private fun testGetSubjects(schoolType: SchoolType) {
        dataDir.listFiles()
                .filter{ it.isDirectory }
                .forEach {
                    Curriculum(schoolType, it).getSubjects().forEach { subject ->
                        assertTrue("${schoolType.name}/${subject.name} has no name", subject.name.isNotEmpty())
                        assertTrue("${schoolType.name}/${subject.name} has no skolfsId", subject.skolfsId.isNotEmpty())
                        assertTrue("${schoolType.name}/${subject.name} has no code", subject.code.isNotEmpty())
                        assertTrue("${schoolType.name}/${subject.name} has no courses", subject.courses.isNotEmpty())
                        testPurpose("${schoolType.name}/${subject.name}", subject.purposes)

                        subject.courses.forEach { course ->
                            testCentralContent("${course.code}/${course.name}", course.centralContent)
                            if (hasRequirements(course.year, schoolType)) {
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
