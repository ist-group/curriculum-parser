package org.edtech.curriculum

import java.time.Instant

data class SubjectHtml(val name: String,
                   val description: String,
                   val code: String,
                   val designation: String,
                   val skolfsId: String,
                   val purposes: String,
                   val courses: List<CourseHtml>,
                   val applianceDate: Instant?
)
data class CourseHtml(val name: String,
                  val description: String,
                  val code: String,
                  val year: String,
                  val point: String,
                  val centralContent: String,
                  val knowledgeRequirement: Map<GradeStep, String>
)
