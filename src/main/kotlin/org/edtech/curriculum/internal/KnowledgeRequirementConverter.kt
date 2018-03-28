package org.edtech.curriculum.internal

import org.edtech.curriculum.GradeStep
import org.edtech.curriculum.KnowledgeRequirement

class KnowledgeRequirementConverter {

    /**
     * Parse the grade-step html text into an KnowledgeRequirement
     */
    fun getKnowledgeRequirements(knowledgeRequirementsHtml: Map<GradeStep, String>): List<KnowledgeRequirement> {
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
        return mergeNoValueLines(knowledgeRequirementResult)
    }

    /**
     * Create a working structure based on the e-level paragraphs and lines
     */
    private fun baseKnowledgeRequirements(html: String, gradeStep: GradeStep): List<KnowledgeRequirement> {
        val knowledgeRequirements = ArrayList<KnowledgeRequirement>()
        val eLevelParagraphs = getParagraphs(fixCurriculumErrors(html))

        for ((paragraphNo, eParagraph) in eLevelParagraphs.withIndex()) {
            // Map the the data object structure
            splitParagraph(eParagraph)
                    .toList()
                    .mapIndexedTo(knowledgeRequirements) { kkrNo, krText ->
                        KnowledgeRequirement(
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


    /**
     * Merges two knowledge requirements
     */
    private fun mergeKnowledgeRequirements(kn1: KnowledgeRequirement, kn2: KnowledgeRequirement): KnowledgeRequirement {
        return KnowledgeRequirement(
                kn1.text + " " + kn2.text,
                kn1.no,
                kn1.paragraphNo,
                kn1.knowledgeRequirementChoice.mapValues {
                    it.value + " " + kn2.knowledgeRequirementChoice.getOrDefault(it.key, "")
                }.toMutableMap()
        )
    }

    /**
     * Some lines do not carry any value (they are the same for all options) these can be merged with the previous line.
     * If the first line needs to be merged, it will be merged with the next line instead
     */
    private fun mergeNoValueLines(knowledgeRequirements: List<KnowledgeRequirement>): List<KnowledgeRequirement> {
        val mergedKnowledgeRequirements = mutableListOf<KnowledgeRequirement>()
        var lastRequirement: KnowledgeRequirement? = null
        var mergeNextRequirement = false
        for (kn in knowledgeRequirements) {
            // If all values are equals merge to previous kn
            if (kn.knowledgeRequirementChoice.filterNot {
                        it.value == kn.knowledgeRequirementChoice[GradeStep.E]
                    }.isEmpty()) {
                if(lastRequirement != null) {
                    lastRequirement = mergeKnowledgeRequirements(lastRequirement, kn)
                } else {
                    if (mergedKnowledgeRequirements.isEmpty()) {
                        mergeNextRequirement = true
                    }
                    lastRequirement = kn
                }
            } else {
                if (mergeNextRequirement && lastRequirement != null) {
                    lastRequirement = mergeKnowledgeRequirements(lastRequirement, kn)
                    mergeNextRequirement = false
                } else {
                    if (lastRequirement != null) {
                        mergedKnowledgeRequirements.add(lastRequirement)
                    }
                    lastRequirement = kn
                }

            }
        }
        if (lastRequirement != null) {
            mergedKnowledgeRequirements.add(lastRequirement)
        }
        return fixNumbering(mergedKnowledgeRequirements)
    }

    /**
     * Add Numbering according to paragraph shifts.
     */
    private fun fixNumbering(knowledgeRequirements: List<KnowledgeRequirement>): List<KnowledgeRequirement> {
        var no = 0
        var paragraphNo = 0
        val fixedRequirements = mutableListOf<KnowledgeRequirement>()
        for (kn in knowledgeRequirements) {
            if (kn.paragraphNo != paragraphNo) {
                paragraphNo = kn.paragraphNo
                no = 0
            }
            fixedRequirements.add(KnowledgeRequirement(kn.text, no++, kn.paragraphNo, kn.knowledgeRequirementChoice))

        }
        return fixedRequirements
    }

    private fun addLevelToKnowledgeRequirement(requirement: KnowledgeRequirement, gradeStep: GradeStep, line: String): KnowledgeRequirement {
        val choices = requirement.knowledgeRequirementChoice.toMutableMap()
        if (choices.containsKey(gradeStep)) {
            choices[gradeStep] = (choices[gradeStep] + " " + line.trim()).trim()
        } else {
            choices[gradeStep] = line.trim()
        }

        return KnowledgeRequirement(
                requirement.text,
                requirement.no,
                requirement.paragraphNo, choices)
    }

    private fun matchRatio(knowledgeRequirement: KnowledgeRequirement?, line: String?): Double {
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
    private fun addGradeStep(knowledgeRequirements: List<KnowledgeRequirement>, html: String, gradeStep: GradeStep, lookahead: Int = 3 ): List<KnowledgeRequirement> {
        val result = mutableListOf<KnowledgeRequirement>()

        // Convert all html paragraphs to a flat line of texts
        val lines = getParagraphs(html)
                .flatMap { splitParagraph(it) }

        // Convert to a straight list of lines
        for ((index, line) in lines.withIndex()) {
            val mappedLineNo = result.size

            // Always take the first line as the first knowledgeRequirement
            if (result.isEmpty()) {
                result.add(addLevelToKnowledgeRequirement(knowledgeRequirements[mappedLineNo], gradeStep, line))
            } else if (knowledgeRequirements.size > mappedLineNo) {
                /**
                 * We got 3 possible outcomes
                 *  - The line matches the current slot
                 *  - The next line matches the current slot better
                 *  - The line matches the next slot better ( will create an empty block in the matrix )
                 */
                val currentLine = matchRatio(knowledgeRequirements[mappedLineNo], line)
                val nextLine = matchRatio(knowledgeRequirements.getOrNull(mappedLineNo + 1), line)

                // Check of any future lines will match better
                val bestLookaheadMatch = ( 1..lookahead).map {
                    matchRatio(knowledgeRequirements[mappedLineNo], lines.getOrNull(index + it))
                }.max() ?: 0.0

                // The line matches the next position better than any of the following lines we are about to map
                // Should only happen when there is no requirement that matches this slot.
                if (nextLine > bestLookaheadMatch && nextLine > currentLine) {
                    // Add an empty slot
                    result.add(addLevelToKnowledgeRequirement(knowledgeRequirements[mappedLineNo], gradeStep, ""))
                    // Add the line at the next slot
                    result.add(addLevelToKnowledgeRequirement(knowledgeRequirements[mappedLineNo + 1], gradeStep, line))

                // The line matches the current position best
                } else if (currentLine >= bestLookaheadMatch) {
                    // Add a new result line
                    result.add(addLevelToKnowledgeRequirement(knowledgeRequirements[mappedLineNo], gradeStep, line))

                // Future lines will match better
                } else {
                    //Add line to the last result
                    result[result.lastIndex] = addLevelToKnowledgeRequirement(result[result.lastIndex], gradeStep, line)
                }
            } else {
                // add to last entry
                result[result.lastIndex] = addLevelToKnowledgeRequirement(result[result.lastIndex], gradeStep, line)
            }
        }
        // Add eventual missing requirements
        if (result.size < knowledgeRequirements.size) {
            knowledgeRequirements.subList(result.size, knowledgeRequirements.size).forEach {
                result.add(addLevelToKnowledgeRequirement(it, gradeStep, ""))
            }
        }
        return result
    }
}