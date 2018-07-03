package org.edtech.curriculum.internal

import org.edtech.curriculum.*
import kotlin.math.max

class KnowledgeRequirementConverter {

    /**
     * Parse the grade-step html text into an KnowledgeRequirement
     */
    fun getKnowledgeRequirements(knowledgeRequirementsHtml: Map<GradeStep, String>): List<KnowledgeRequirementParagraph> {
        if (knowledgeRequirementsHtml.isEmpty()) {
            return listOf()
        }
        val knowledgeRequirementResult = if (knowledgeRequirementsHtml.size == 1 && knowledgeRequirementsHtml.containsKey(GradeStep.G)) {
            baseKnowledgeRequirements(knowledgeRequirementsHtml[GradeStep.G] ?: "", GradeStep.G)
        } else if (knowledgeRequirementsHtml.containsKey(GradeStep.E)) {
            var knowledgeRequirements = baseKnowledgeRequirements(knowledgeRequirementsHtml[GradeStep.E] ?: "", GradeStep.E)

            // Combine other levels into the existing structure
            for (gradeStep in listOf(GradeStep.C, GradeStep.A)) {
                knowledgeRequirements = addGradeStep(knowledgeRequirements,
                        fixCurriculumErrors(knowledgeRequirementsHtml[gradeStep] ?: ""), gradeStep)
            }
            knowledgeRequirements
        } else {
            throw Exception("Cannot parse KnowledgeRequirement with structure: " + knowledgeRequirementsHtml.keys)
        }
        return  structureParagraphs(knowledgeRequirementResult)
    }

    /**
     * Return a list of paragraphs
     */
    private fun structureParagraphs(knowledgeRequirementList: List<KnowledgeRequirementData>): List<KnowledgeRequirementParagraph> {
        var paragraphNo = 0
        val structuredRequirements = mutableListOf<KnowledgeRequirementParagraph>()
        val requirementsInParagraph = mutableListOf<KnowledgeRequirement>()
        for (kn in knowledgeRequirementList) {
            if (kn.paragraphNo != paragraphNo) {
                paragraphNo = kn.paragraphNo
                structuredRequirements.add(KnowledgeRequirementParagraph("", requirementsInParagraph.toList()))
                requirementsInParagraph.clear()
            }
            requirementsInParagraph.add(KnowledgeRequirement(kn.text, kn.knowledgeRequirementChoice))
        }
        if (requirementsInParagraph.isNotEmpty()) {
            structuredRequirements.add(KnowledgeRequirementParagraph("", requirementsInParagraph.toList()))
        }
        return structuredRequirements
    }

    /**
     * Create a working structure based on the e-level paragraphs and lines
     */
    private fun baseKnowledgeRequirements(html: String, gradeStep: GradeStep): List<KnowledgeRequirementData> {
        val knowledgeRequirements = ArrayList<KnowledgeRequirementData>()
        val eLevelParagraphs = getParagraphs(fixCurriculumErrors(html))

        for ((paragraphNo, eParagraph) in eLevelParagraphs.withIndex()) {
            // Map the the data object structure
            splitParagraph(eParagraph)
                    .toList()
                    .mapIndexedTo(knowledgeRequirements) { kkrNo, krText ->
                        KnowledgeRequirementData(
                                // Generate Placeholder from E level
                                getPlaceHolderText(krText),
                                kkrNo,
                                paragraphNo,
                                mapOf(Pair(gradeStep, krText)).toMap()
                        )
                    }
        }
        return knowledgeRequirements
    }

    private fun addLevelToKnowledgeRequirement(requirement: KnowledgeRequirementData, gradeStep: GradeStep, line: String): KnowledgeRequirementData {
        val choices = requirement.knowledgeRequirementChoice.toMutableMap()
        if (choices.containsKey(gradeStep)) {
            choices[gradeStep] = (choices[gradeStep] + " " + line.trim()).trim()
        } else {
            choices[gradeStep] = line.trim()
        }

        return KnowledgeRequirementData(
                requirement.text,
                requirement.no,
                requirement.paragraphNo, choices)
    }

    private fun matchRatio(knowledgeRequirement: KnowledgeRequirementData?, line: String?): Double {
        if (knowledgeRequirement != null && line != null) {
            return knowledgeRequirement.knowledgeRequirementChoice.map {
                similarLineRatio(it.value, line)
            }.max() ?: 0.0
        }
        return 0.0
    }

    /**
     * Add new grade-step values to a list of knowledge requirements
     */
    private fun addGradeStep(knowledgeRequirements: List<KnowledgeRequirementData>, html: String, gradeStep: GradeStep, lookahead: Int = 3 ): List<KnowledgeRequirementData> {
        // Prefer a match with the current line if they are too similar.
        val currentLineBias = 0.15
        val result = mutableListOf<KnowledgeRequirementData>()

        // Convert all html paragraphs to a flat line of texts
        // - Skolverket has written the requirements in different orders, pre-process to get them in correct order
        val lines = getParagraphs(html).flatMap { splitParagraph(it) }

        var index = 0
        // Convert to a straight list of lines
        while (index < lines.size) {
            val mappedLineNo = result.size
            val line = lines[index]

            // Always take the first line as the first knowledgeRequirement
            if (result.isEmpty()) {
                result.add(addLevelToKnowledgeRequirement(knowledgeRequirements[mappedLineNo], gradeStep, line))
            } else if (knowledgeRequirements.size > mappedLineNo) {
                /**
                 * We got 4 possible outcomes
                 *  - The line matches the current slot
                 *  - The next line matches the current slot better
                 *  - The line matches the next slot better ( will create an empty block in the matrix )
                 *  - Skolverket has written the requirements in different orders, reorder the lines
                 */
                val currentLineRatio = matchRatio(knowledgeRequirements[mappedLineNo], line) + currentLineBias
                val nextLineRatio = matchRatio(knowledgeRequirements.getOrNull(mappedLineNo + 1), line)

                // Check of any future lines will match better
                val bestLookaheadMatchRatio = ( 1..lookahead).map { offset ->
                    matchRatio(knowledgeRequirements[mappedLineNo], lines.getOrNull(index + offset))
                }.max() ?: 0.0


                // The line matches the next position better than any of the following lines we are about to map
                // Should only happen when there is no requirement that matches this slot.
               if (nextLineRatio > bestLookaheadMatchRatio && nextLineRatio > currentLineRatio) {
                    // Add an empty slot
                    result.add(addLevelToKnowledgeRequirement(knowledgeRequirements[mappedLineNo], gradeStep, ""))
                    // Add the line at the next slot
                    result.add(addLevelToKnowledgeRequirement(knowledgeRequirements[mappedLineNo + 1], gradeStep, line))

                // The line matches the current position best
                } else if (currentLineRatio >= bestLookaheadMatchRatio) {
                    // Add a new result line
                    result.add(addLevelToKnowledgeRequirement(knowledgeRequirements[mappedLineNo], gradeStep, line))

                // Future lines will match better
                } else {
                   if (lines.size > index + 1 && knowledgeRequirements.size > mappedLineNo + 1) {
                       // Check if we need to reorder the lines
                       val nextLineHereRatio = matchRatio(knowledgeRequirements[mappedLineNo], lines[index + 1])
                       val thisLineNextRatio = matchRatio(knowledgeRequirements[mappedLineNo + 1], line)

                       // Switch current and next lines instead of merge with previous
                       if (nextLineHereRatio  == 1.0 && thisLineNextRatio == 1.0 ) {
                           // Add next line here
                           result.add(addLevelToKnowledgeRequirement(knowledgeRequirements[mappedLineNo], gradeStep, lines[index + 1]))
                           // Add the line at the next slot
                           result.add(addLevelToKnowledgeRequirement(knowledgeRequirements[mappedLineNo + 1], gradeStep, line))
                           // Next line is already taken care of so skip it!
                           index += 2
                           continue
                       }
                   }

                    //Add line to the last result
                    result[result.lastIndex] = addLevelToKnowledgeRequirement(result[result.lastIndex], gradeStep, line)
                }
            } else {
                // add to last entry
                result[result.lastIndex] = addLevelToKnowledgeRequirement(result[result.lastIndex], gradeStep, line)
            }
            index++
        }

        // Add eventual missing requirements
        if (result.size < knowledgeRequirements.size) {
            knowledgeRequirements.subList(result.size, knowledgeRequirements.size).forEach {
                result.add(addLevelToKnowledgeRequirement(it, gradeStep, ""))
            }
        }
        return result
    }

    private data class KnowledgeRequirementData(
        val text: String,
        val no: Int,
        val paragraphNo: Int,
        val knowledgeRequirementChoice: Map<GradeStep, String>
    )
}