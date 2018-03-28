@file:JvmName("HtmlUtils")

package org.edtech.curriculum.internal

import org.edtech.curriculum.CentralContent
import org.edtech.curriculum.CentralContentType
import org.edtech.curriculum.YearGroup
import org.jsoup.Jsoup
import kotlin.math.abs
import kotlin.math.max

/**
 * Replaces the bold words with ________
 * Whole sentences that are bold will be set to an empty string
 */
fun getPlaceHolderText(htmlText: String): String {
    if (htmlText.matches(Regex("([ ]*)<strong>.*?</strong>([. ]*)"))) return ""
    return htmlText
            .replace(Regex("<strong>( )?.*?( )?</strong>"), "\$1<strong>________</strong>\$2")
            .replace("  ", " ")
            .trim()
}
/**
 * Get all top level paragraphs from an html string
 */
fun getParagraphs(html: String): List<String> {
    return Jsoup.parse(html).select("p").map { it.html() }.filter { it.trim().isNotEmpty() }
}

/**
 * Split a text paragraph into a list of strings, remove all empty lines
 */
fun splitParagraph(text: String): List<String> {
    return text.split(Regex("(?<=\\.)")).toList().map { it.trim() }.filter { it.isNotEmpty() }
}

internal fun fixHtmlEncoding(htmlText: String): String {
    return htmlText
            .replace("&lt;" ,"<")
            .replace("&gt;" ,">")
}

internal fun fixCurriculumErrors(text: String): String {
    return fixHtmlEncoding(text)
            .replace(Regex("(?<=[a-zåäö]) (Vidare|Eleven|Dessutom)"), ". $1")
            .replace("</strong><strong>", "")
            .replace(Regex("<strong> </strong>"), "")
            .replace("<strong><italic>. </italic></strong>", ". ")
            .replace("<strong> <italic>  .  </italic></strong>", ". ")
            .replace("<br/>", " ")
            .replace("<br>", " ")
            .replace("<p>.</p>", "")
            .trim()
}

private fun splitWords(line: String): List<String> {
    val r = Regex("[\\s,.-]+")
    return line.trim().split(r).filter { it.isNotEmpty() }
}

/**
 * Compares words and their positions and return a value between 0-1
 * where 1 is representing the exact same line and 0 when the lines has nothing incommon
 */
internal fun similarLineRatio(line1: String, line2:String): Double {
    val wordList1  = removeInflections(splitWords(removeBoldWords(line1.toLowerCase())))
    val wordList2  = removeInflections(splitWords(removeBoldWords(line2.toLowerCase())))

    if (wordList2.isEmpty() || wordList1.isEmpty()) {
        return 0.0
    }

    val maxLength = max(wordList1.size, wordList2.size)

    // Match words by position, allow +-2 positions
    val matchesWordCount = wordList1
            .mapIndexed {
                index, word ->
                val matchPos = wordList2.indexOf(word)
                if (matchPos != -1) {
                    val distance = abs(matchPos - index).toDouble()
                    1.0 - (distance / maxLength.toDouble())
                } else
                    0.0
            }.sumByDouble { it }

    return  matchesWordCount / maxLength.toDouble()
}

/**
 * Remove some common infliction to make comparisons easier
 */
internal fun removeInflections(wordList: List<String>): List<String> {
    return wordList.map {
        // Noun
        it.replace(Regex("(an|or|orna|en|ar|arna|er|erna|t|et|ena|ens)$"), "")
    }
}

/**
 * Removes all delimiters and bold words
 */
internal fun removeBoldWords(htmlText: String): String {
    return htmlText
            .replace(Regex("<strong> [^>]*</strong>"), " ")
            .replace(Regex("<strong>[^>]* </strong>"), " ")
            .replace(Regex("<strong>[^>]*</strong>"), "")
}

/**
 * Convert a string to a year group
 */
internal fun toYearGroup(year: String): YearGroup? {
    val yearParts = year.split("-")
    return if (yearParts.size > 1) {
        val startYear = yearParts.getOrNull(0)?.toIntOrNull()
        // If an open ended range is given add a stage 4- => 4-6
        val endYear = yearParts.getOrNull(1)?.toIntOrNull() ?: startYear?.plus(2) ?: 0

        YearGroup(
                startYear,
                endYear
        )
    } else {
        val endYear = yearParts.getOrNull(0)?.toIntOrNull() ?: return null
        YearGroup(null, endYear)
    }
}

/**
 * Combine heading and bullets in one list
 */
internal fun toCentralContent(html: String): List<CentralContent> {
    return Jsoup.parse(html).select("strong, li, i, h1, h2, h3, h4, h5, h6")
            .filter { it.text().isNotEmpty() }
            .map {
                val type = when (it.tagName()) {
                    "li" -> CentralContentType.BULLET
                    else -> CentralContentType.HEADING
                }
                CentralContent(it.text(), type)
            }
}