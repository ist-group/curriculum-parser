package org.edtech.curriculum

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
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
                    assertAll(
                            {assertTrue(subjectHtml.name.isNotEmpty()) { "${schoolType.name}/${subjectHtml.code} has no name"} },
                            {assertTrue(subjectHtml.code.isNotEmpty()) { "${schoolType.name}/${subjectHtml.name} has no code"} },
                            {assertTrue(subjectHtml.skolfsId.isNotEmpty()) { "${schoolType.name}/${subjectHtml.skolfsId} has no skolfsId"} },
                            {assertTrue(subjectHtml.courses.isNotEmpty()) { "${schoolType.name}/${subjectHtml.name} has no courses"} },
                            {assertTrue(subjectHtml.purposes.isNotEmpty()) { "${schoolType.name}/${subjectHtml.name} has no purposes" } },
                            {assertTrue(subjectHtml.typeOfSchooling == null || subjectHtml.originatorTypeOfSchooling == null ) {
                                "${schoolType.name}/${subjectHtml.name} has both typeOfSchooling(${subjectHtml.typeOfSchooling}) and originatorTypeOfSchooling(${subjectHtml.originatorTypeOfSchooling})" }
                            },
                            {assertTrue(subjectHtml.typeOfSchooling != null || subjectHtml.originatorTypeOfSchooling != null ) {
                                "${schoolType.name}/${subjectHtml.name} has either typeOfSchooling or originatorTypeOfSchooling set" }
                            }
                    )

                    subjectHtml.courses.forEach { courseHtml ->
                        // Only require kr when passed the lowest grades
                        if (hasRequirements(courseHtml.year, schoolType)) {
                            assertTrue(courseHtml.knowledgeRequirementGroups.isNotEmpty()) { "${courseHtml.code}/${courseHtml.name} has no knowledgeRequirements" }
                        }
                        assertAll(
                                {assertTrue(courseHtml.centralContent.isNotEmpty()) { "${courseHtml.code}/${courseHtml.name} has no centralContents" } },
                                {assertTrue(courseHtml.centralContent.contains(Regex("<li>|<p>–"))) { "${courseHtml.code}/${courseHtml.name} has malformed centralContents:\n ${courseHtml.centralContent}" } },
                                {assertTrue(courseHtml.code.isNotEmpty()) { "${courseHtml.code}/${courseHtml.name} has no code" } },
                                {assertTrue(courseHtml.name.isNotEmpty()) { "${schoolType.name}/${subjectHtml.name} has no courses"} }
                        )

                        // Only check real courses
                        if (courseHtml.year.isEmpty() ) {
                            if (courseHtml.point.isEmpty()) {
                                fail("${courseHtml.code}/${courseHtml.name} has no points/year group")
                            }
                            assertTrue(courseHtml.description.isNotEmpty()) { "${courseHtml.code}/${courseHtml.name} has no description" }
                        }
                    }
                }
            }
    }

    private fun testDuplicateRequirements(schoolType: SchoolType) {
        // TODO convert to an assert all
        dataDir.listFiles()
                .filter { it.isDirectory }
                .forEach {
                    Curriculum(schoolType, it).subjectHtml.forEach {
                        it.courses.forEach { courseHtml ->
                            courseHtml.knowledgeRequirementGroups
                                    .flatMap { rg -> rg.knowledgeRequirements.entries }
                                    .filter { entry -> entry.key != GradeStep.D && entry.key != GradeStep.B }
                                    .forEach { entry ->
                                        val matchingCourse = it.courses
                                                .firstOrNull {
                                                    c -> courseHtml != c && c.knowledgeRequirementGroups.flatMap {
                                                    rg -> rg.knowledgeRequirements.values
                                                }.contains(entry.value)
                                                }
                                        assertNull(matchingCourse) { "duplicate knowledge requirement found in ${it.name}[${it.code}] ${courseHtml.name}[${courseHtml.code}] => ${matchingCourse?.code}:\n${entry.value}" }
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

        purposes.forEach {
            assertTrue( it.lines.isNotEmpty() || it.heading.isNotEmpty()) { "Found empty purpose in $name" }
            assertNull(  it.lines.firstOrNull { it.trim().isEmpty() }) { "Found empty purpose line in $name" }
            if (it.type == PurposeType.BULLET) {
                assertTrue(it.heading.isNotEmpty()) {"Bullet lists always needs a heading $name" }
            }
        }
    }

    private fun testCentralContent(name: String, centralContents: List<CentralContent>) {
        assertTrue(centralContents.isNotEmpty()) { "$name has no central contents" }

        centralContents.forEach {
            assertTrue( it.lines.isNotEmpty() || it.heading.isNotEmpty()) { "Found empty central contents in $name" }
            assertNull( it.lines.firstOrNull { it.trim().isEmpty() }) { "Found empty central contents line in $name" }
        }

       assertTrue(centralContents.any{ it.lines.isNotEmpty()}) { "all central contents are empty $name" }
    }

    private fun testGetSubjects(schoolType: SchoolType) {
        dataDir.listFiles()
                .filter{ it.isDirectory }
                .forEach {
                    Curriculum(schoolType, it).getSubjects().forEach { subject ->
                        assertTrue(subject.name.isNotEmpty(), "${schoolType.name}/${subject.name} has no name")
                        assertTrue(subject.skolfsId.isNotEmpty(), "${schoolType.name}/${subject.name} has no skolfsId")
                        assertTrue(subject.code.isNotEmpty(), "${schoolType.name}/${subject.name} has no code")
                        assertTrue(subject.courses.isNotEmpty(), "${schoolType.name}/${subject.name} has no courses")
                        testPurpose("${schoolType.name}/${subject.name}", subject.purposes)

                        subject.courses.forEach { course ->
                            testCentralContent("${course.code}/${course.name}", course.centralContent)
                            if (hasRequirements(course.year, schoolType)) {
                                assertTrue(course.knowledgeRequirementParagraphs.isNotEmpty(), "${course.code}/${course.name} has no knowledgeRequirements")
                            }
                            assertTrue(course.code.isNotEmpty(), "${course.code}/${course.name} has no code")
                            assertTrue(course.name.isNotEmpty(), "${course.code}/${course.name} has no name")
                            // Only check real courses
                            if (course.year == null) {
                                if (course.point == null) {
                                    fail("${course.code}/${course.name} has no points/year group")
                                }
                                assertTrue(course.description.isNotEmpty(), "${course.code}/${course.name} has no description")
                                if (course.point != null) {
                                    assertTrue(course.point!! > 0, "${course.code}/${course.name} has no points")
                                } else {
                                    fail("${course.code}/${course.name} has no points")
                                }
                            }
                        }
                    }
                }
    }
}
