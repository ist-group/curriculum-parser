package org.edtech.curriculum.internal

import info.debatty.java.stringsimilarity.NormalizedLevenshtein
import org.edtech.curriculum.GradeStep
import org.edtech.curriculum.KnowledgeRequirement
import org.jsoup.Jsoup
import kotlin.math.max
import kotlin.math.min

fun fixCurriculumErrors(text: String): String {
    return text
            .replace(Regex("(?<=[a-zåäö]) (Vidare|Eleven|Dessutom)"), ". $1")
            .replace("&lt;" ,"<")
            .replace("</strong><strong>", "")
            .replace(Regex("<strong> </strong>"), "")
            .replace("<strong><italic>. </italic></strong>", ". ")
            .replace("<strong> <italic>  .  </italic></strong>", ". ")
            .replace("<br/>", " ")
            .replace("<br>", " ")
            .replace("<p>.</p>", "")
            .trim()
}


/**
 * Count the number of words that matches, value needs to be over 80% to count as a match
 */
fun textMatches(text1: String, text2: String): Boolean {
    if (text2.trim().isBlank() || text1.trim().isBlank()) return false

    val similarityThreshold = 0.8
    val r = Regex("[\\s,.-]+")

    val wordList1  = getTextWithoutBoldWords(text1).trim().split(r).filter { it.isNotEmpty() }
    val wordList2  = getTextWithoutBoldWords(text2).trim().split(r).filter { it.isNotEmpty() }

    val minLength = min(wordList1.size, wordList2.size)
    val maxLength = max(wordList1.size, wordList2.size)

    // Make sure that we do not match single word lines
    if (minLength == 1 && maxLength > 3) {
        return false
    }

    // Match words by position, allow +-2 positions
    val matchesWordCount = wordList1
            .filterIndexed {
                index, word -> wordList2.contains(word) && wordList2.indexOf(word) - index in -2..2
            }.size

    if (matchesWordCount.toDouble() / minLength.toDouble() > similarityThreshold) {
        return true
    }

    // Match with Levenshtein as a fallback to handle when words have different inflections
    val l = NormalizedLevenshtein()
    if (l.similarity(wordList1.joinToString(" "),  wordList2.joinToString(" ")) > similarityThreshold ) {
        return true
    }
    return false
}

/**
 * Removes all delimiters and bold words
 */
fun getTextWithoutBoldWords(htmlText: String): String {
    return htmlText
            .replace(Regex("<strong> [^>]* </strong>"), " ")
            .replace(Regex("<strong>[^>]*</strong>"), "")
}


internal class KnowledgeRequirementParser {

    /**
     * Replaces the bold words with ________
     */
    private fun getPlaceHolderText(htmlText: String): String {
        return htmlText
                .replace(Regex("<strong>[^>]*</strong>"), "<strong>________</strong>")
    }
    /**
     * Get all top level paragraphs from an html string
     */
    private fun getParagraphs(html: String): List<String> {
        return Jsoup.parse(html).select("p").map { it.html() }.filter { it.trim().isNotEmpty() }
    }

    /**
     * Split a text paragraph into a list of strings, remove all empty lines
     */
    private fun splitParagraph(text: String): List<String> {
        return text.split(Regex("(?<=\\.)")).toList().map { it.trim() }.filter { it.isNotEmpty() }
    }

    /**
     * Create a working structure based on the e-level paragraphs and lines
     */
    private fun eLevelToBaseKnowledgeRequirements(eLevelHtml: String): List<KnowledgeRequirement> {
        val knowledgeRequirements = ArrayList<KnowledgeRequirement>()
        val eLevelParagraphs = getParagraphs(fixCurriculumErrors(eLevelHtml))

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
                            mapOf(Pair(GradeStep.E, krText), Pair(GradeStep.C, ""), Pair(GradeStep.A, "")).toMutableMap()
                    )
            }
        }
        return knowledgeRequirements
    }

    /**
     * Add grade-step text value to the knowledge requirement in the line specified by lineNo
     */
    private fun commitLines(text: String, lineNo: Int, knowledgeRequirements: List<KnowledgeRequirement>, gradeStep: GradeStep) {
        // Append to existing
        val index = if (lineNo >= 0) lineNo else 0
        if (knowledgeRequirements.size > lineNo) {
            if (knowledgeRequirements[index].knowledgeRequirementChoice.containsKey(gradeStep)) {
                knowledgeRequirements[index].knowledgeRequirementChoice[gradeStep] =
                        (knowledgeRequirements[index].knowledgeRequirementChoice[gradeStep] + " " + text).trim()
            } else {
                knowledgeRequirements[index].knowledgeRequirementChoice[gradeStep] = text
            }
        } else {
            throw Error("Cannot commit lines outside range..")
        }
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

    /**
     * Add new grade-step values to a list of knowledge requirements
     */
    private fun addGradeStep(knowledgeRequirements: List<KnowledgeRequirement>, html: String, gradeStep: GradeStep) {
        // Convert all html paragraphs to a flat line of texts
        val unmappedLines: MutableList<String> = ArrayList()

        fun matchLineToLineNo(line: String, mappedLineNo: Int): Boolean {
            if (mappedLineNo < 0 || mappedLineNo >= knowledgeRequirements.size) {
                return false
            }
            if ( textMatches(knowledgeRequirements[mappedLineNo].knowledgeRequirementChoice[GradeStep.E] ?: "", line) ||
                    (gradeStep == GradeStep.A && textMatches(knowledgeRequirements[mappedLineNo].knowledgeRequirementChoice[GradeStep.C] ?: "", line)) ) {
                // Commit unmapped lines to previous row
                if (unmappedLines.isNotEmpty()) {
                    commitLines(unmappedLines.joinToString(" "), mappedLineNo - 1, knowledgeRequirements, gradeStep)
                    unmappedLines.clear()
                }

                // Add the matched line
                commitLines(line, mappedLineNo, knowledgeRequirements, gradeStep)
                return true
            }
            return false
        }

        var mappedLineNo = 0
        for (line in getParagraphs(html)
                .map { splitParagraph(it) }
                .flatten()) {
            // Does the current or next line match
            when {
                matchLineToLineNo(line, mappedLineNo) -> mappedLineNo++
                matchLineToLineNo(line, mappedLineNo + 1) -> mappedLineNo += 2
                else -> unmappedLines.add(line)
            }
        }

        if (unmappedLines.size > 0) {
            // Add the extra rest to the last line that did match
            if (unmappedLines.size > 0) {
                commitLines(unmappedLines.joinToString(" "), knowledgeRequirements.size - 1, knowledgeRequirements, gradeStep)
            }
        }
    }

    /**
     * Parse the grade-step html text into an KnowledgeRequirement
     */
    fun getKnowledgeRequirements(eLevelHtml: String, cLevelHtml: String, aLevelHtml: String): List<KnowledgeRequirement> {
        val knowledgeRequirements = eLevelToBaseKnowledgeRequirements(fixCurriculumErrors(eLevelHtml))

        // Map other levels into the existing structure
        addGradeStep(knowledgeRequirements, fixCurriculumErrors(cLevelHtml), GradeStep.C)
        addGradeStep(knowledgeRequirements, fixCurriculumErrors(aLevelHtml), GradeStep.A)

        return mergeNoValueLines(knowledgeRequirements)
    }
}