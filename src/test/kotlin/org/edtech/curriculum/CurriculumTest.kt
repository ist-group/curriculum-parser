package org.edtech.curriculum

import org.edtech.curriculum.SubjectCategory.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertAll
import java.io.File

class CurriculumTest {
    private val dataDir = File("./src/test/resources/opendata/")

    @TestFactory
    fun testGetSubjects() = SchoolType.values().map { schoolType ->
        DynamicTest.dynamicTest(schoolType.name) { testGetSubjects(schoolType) }
    }

    @TestFactory
    fun testGetSubjectsHtml() = SchoolType.values().map { schoolType ->
        DynamicTest.dynamicTest(schoolType.name) { testGetSubjectsHtml(schoolType) }
    }

    /**
     * Gradelevel 9 has requirements
     * Gradelevel 6 has requirements except in GRS
     * No gradelevel => has requirements
     */
    private fun hasRequirements(year: String, schoolType: SchoolType): Boolean {
        return (year.isEmpty() || year.contains("6") &&  (schoolType != SchoolType.GRS && schoolType != SchoolType.GRSSPEC) || year.contains("9"))
    }

    private fun testGetSubjectsHtml(schoolType: SchoolType) {
        dataDir.listFiles()
            .filter{ it.isDirectory }
            .forEach {
                Curriculum(schoolType, it).subjectHtml.forEach { subjectHtml ->
                    assertAll(
                            {assertTrue(subjectHtml.name.isNotEmpty()) { "${schoolType.name}/${subjectHtml.code} has no name"} },
                            {assertTrue(subjectHtml.code.isNotEmpty()) { "${schoolType.name}/${subjectHtml.name} has no code"} },
                            {assertTrue(subjectHtml.skolfsId.isNotEmpty()) { "${schoolType.name}/${subjectHtml.skolfsId} has no skolfsId"} },
                            {assertTrue(subjectHtml.courses.isNotEmpty()) { "${schoolType.name}/${subjectHtml.name} has no courses"} },
                            {assertTrue(subjectHtml.purposes.isNotEmpty()) { "${schoolType.name}/${subjectHtml.name} has no purposes" } }
                    )

                    subjectHtml.courses.forEach { courseHtml ->
                        // Only require kr when passed the lowest grades
                        if (hasRequirements(courseHtml.year, schoolType)) {
                            assertTrue(courseHtml.knowledgeRequirementGroups.isNotEmpty()) { "${courseHtml.code}/${courseHtml.name} has no knowledgeRequirements" }
                        }
                        assertAll(
                                {assertTrue(courseHtml.centralContent.isNotEmpty()) { "${courseHtml.code}/${courseHtml.name} has no centralContents" } },
                                {assertTrue(courseHtml.centralContent.contains(Regex("<li>|<p>–|<p>•"))) { "${courseHtml.code}/${courseHtml.name} has malformed centralContents:\n ${courseHtml.centralContent}" } },
                                {assertTrue(courseHtml.code.isNotEmpty()) { "${courseHtml.code}/${courseHtml.name} has no code" } },
                                {assertTrue(courseHtml.name.isNotEmpty()) { "${schoolType.name}/${subjectHtml.name} has no courses"} }
                        )

                        // Only check real courses
                        if (courseHtml.year.isEmpty() && schoolType != SchoolType.GYS_SUBJECT_AREA) {
                            if (courseHtml.point.isEmpty()) {
                                fail<Unit>("${courseHtml.code}/${courseHtml.name} has no points/year group")
                            }
                            assertTrue(courseHtml.description.isNotEmpty()) { "${courseHtml.code}/${courseHtml.name} has no description" }
                        }
                    }
                }
            }
    }

    private fun testDuplicateRequirements(schoolType: SchoolType) {
        dataDir.listFiles()
                .filter { it.isDirectory }
                .forEach { file ->
                    Curriculum(schoolType, file).subjectHtml.forEach { subjectHtml ->
                        subjectHtml.courses
                                .filterNot {
                                    // These contain duplicates so skip the check
                                    arrayOf(
                                        WITHIN_LANGUAGE_CHOICE.name,
                                        WITHIN_STUDENT_CHOICE.name,
                                        WITHIN_LANGUAGE_CHOICE_CHINESE.name,
                                        WITHIN_STUDENT_CHOICE_CHINESE.name
                                    ).contains(it.category)
                                }
                                .forEach { courseHtml ->
                            courseHtml.knowledgeRequirementGroups
                                    .flatMap { rg -> rg.knowledgeRequirements.entries }
                                    .filter { entry -> entry.key != GradeStep.D && entry.key != GradeStep.B }
                                    .forEach { entry ->
                                        val matchingCourse = subjectHtml.courses
                                                .firstOrNull {
                                                    c -> courseHtml != c && c.knowledgeRequirementGroups.flatMap {
                                                    rg -> rg.knowledgeRequirements.values
                                                }.contains(entry.value)
                                                }
                                        assertNull(matchingCourse) { "duplicate knowledge requirement found in ${subjectHtml.name}[${subjectHtml.code}] ${courseHtml.name}[${courseHtml.code}] => ${matchingCourse?.code}:\n${entry.value}" }
                                    }
                        }
                    }
                }
    }

    @Test
    fun testDuplicateRequirementsGR() {
        testDuplicateRequirements(SchoolType.GR)
    }

    @Test
    fun testDuplicateRequirementsGRS() {
        testDuplicateRequirements(SchoolType.GRS)
    }

    private fun testPurpose(name: String, purposes: List<Purpose>) {
        assertTrue(purposes.isNotEmpty()) { "$name has no purpose" }

        purposes.forEach { purpose ->
            assertTrue( purpose.lines.isNotEmpty() || purpose.heading.isNotEmpty()) { "Found empty purpose in $name" }
            assertNull(  purpose.lines.firstOrNull { it.trim().isEmpty() }) { "Found empty purpose line in $name" }
            if (purpose.type == PurposeType.BULLET) {
                assertTrue(purpose.heading.isNotEmpty()) {"Bullet lists always needs a heading $name" }
            }
        }
    }

    private fun testCentralContent(name: String, centralContents: List<CentralContent>) {
        assertTrue(centralContents.isNotEmpty()) { "$name has no central contents" }

        centralContents.forEach { centralContent ->
            assertTrue( centralContent.lines.isNotEmpty() || centralContent.heading.isNotEmpty()) { "Found empty central contents in $name" }
            assertNull( centralContent.lines.firstOrNull { it.trim().isEmpty() }) { "Found empty central contents line in $name" }
            assertFalse(centralContent.heading.count { it == '.'} > 1) { "Got several lines in the heading: $name => ${centralContent.heading}"}
        }

       assertTrue(centralContents.any{ it.lines.isNotEmpty()}) { "all central contents are empty $name" }
    }

    private fun testGetSubjects(schoolType: SchoolType) {
        dataDir.listFiles()
                .filter{ it.isDirectory }
                .forEach {
                   Curriculum(schoolType, it).subjects.forEach { subject ->
                        assertTrue(subject.name.isNotEmpty(), "${schoolType.name}/${subject.name} has no name")
                        assertTrue(subject.skolfsId.isNotEmpty(), "${schoolType.name}/${subject.name} has no skolfsId")
                        assertTrue(subject.code.isNotEmpty(), "${schoolType.name}/${subject.name} has no code")
                        assertTrue(subject.courses.isNotEmpty(), "${schoolType.name}/${subject.name} has no courses")
                        testPurpose("${schoolType.name}/${subject.name}", subject.purposes)

                        subject.courses.forEach { course ->
                            testCentralContent("${course.code}/${course.name}", course.centralContent)
                            assertTrue(course.knowledgeRequirementParagraphs.isNotEmpty(), "${course.code}/${course.name} has no knowledgeRequirements")
                            assertTrue(course.code.isNotEmpty(), "${course.code}/${course.name} has no code")
                            assertTrue(course.name.isNotEmpty(), "${course.code}/${course.name} has no name")
                            // Only check real courses
                            if (course.year == null && schoolType != SchoolType.GYS_SUBJECT_AREA) {
                                if (course.point == null) {
                                    fail<Unit>("${course.code}/${course.name} has no points/year group")
                                }
                                assertTrue(course.description.isNotEmpty(), "${course.code}/${course.name} has no description")
                                if (course.point != null) {
                                    assertTrue(course.point!! > 0, "${course.code}/${course.name} has no points")
                                } else {
                                    fail<Unit>("${course.code}/${course.name} has no points")
                                }
                            }
                        }
                    }
                }
    }
}
