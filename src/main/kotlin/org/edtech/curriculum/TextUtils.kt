@file:JvmName("TextUtils")

package org.edtech.curriculum

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

private fun splitWords(line: String): List<String> {
    val r = Regex("[\\s,.-]+")
    return line.trim().split(r).filter { it.isNotEmpty() }
}

/**
 * Compares words and their positions and return a value between 0-1
 * where 1 is representing the exact same line and 0 when the lines has nothing incommon
 */
fun similarLineRatio(line1: String, line2:String): Double {
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
            .replace(Regex("[ ][ ]+"),  " ")
}