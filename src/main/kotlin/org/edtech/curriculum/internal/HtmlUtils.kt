@file:JvmName("HtmlUtils")

package org.edtech.curriculum.internal

import org.edtech.curriculum.CentralContent
import org.edtech.curriculum.Purpose
import org.edtech.curriculum.PurposeType
import org.edtech.curriculum.YearGroup
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

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
            .replace("<br/>", " ")
            .replace("<br>", " ")
            .replace(Regex("<italic>([^<]*)</italic>"), "$1")
            .replace(".</p><p>.</p>", ".</p>")
            .replace("</p><p>.</p>", ".</p>")
            // Remove double spacing
            .replace(Regex("[ ][ ]+"),  " ")
            .replace("<strong> ",  " <strong>")
            .replace(Regex("([. ]+)</strong>"),  "</strong>$1")
            .replace(Regex("</strong>(\\s*)<strong>"), "$1")
            .replace(Regex("<strong>(\\s*)</strong>"), "$1")
            .trim()
}

internal fun fixDescriptions(text: String) : String {
    return Jsoup.parse(text).text()
}

/**
 * Converts a html-snippet with
 * <p>- lourum</p>
 * <p>- lourum</p>
 * <p>- lourum</p>
 *
 * to
 *
 * <ul>
 *     <li>lourum</li>
 *     <li>lourum</li>
 *     <li>lourum</li>
 * </ul>
 */
internal fun convertDashListToList(stringHtml: String): String {
    var listElement: Element? = null
    val fragment =  Jsoup.parseBodyFragment(stringHtml)
    fragment.body().children()
        .forEach {
            if (it.tagName() == "p" && it.text().startsWith("–")) {
                val liTag = Element("li").html(it.html().removePrefix("–").trim())
                if (listElement == null) {
                    listElement = Element("ul").appendChild(liTag)
                    it.replaceWith(listElement)
                } else {
                    listElement?.appendChild(liTag)
                    it.remove()
                }
            } else {
                listElement = null
            }
        }
    return fragment.body().html()
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
 * Convert the Purpose html to Entities depending on tag type
 */
internal fun toPurposes(html: String): List<Purpose> {
    val fragment = Jsoup.parseBodyFragment(html)

    // Some subjects do not have real paragraphs, convert <br> tags to <p></p>
    fragment.select("body > p")
            .forEach {
                val paragraphHtml = it.html()
                if (paragraphHtml.contains("<br>")) {
                    it.before("<p>${paragraphHtml.replace("<br>", "</p><p>")}</p>")
                    it.remove()
                }
            }
    // Remove empty paragraphs
    fragment.select("body > p")
            .forEach {
                if (it.text().trim().isEmpty()) {
                    it.remove()
                }
            }
    return fragment
            .select("body > p, body > ul, body > ol, body > h1, body > h2, body > h3, body > h4, body > h5, body > h6, body > i")
            .filter { it.text().isNotEmpty() }

            // Don't want skolverkets content map to be part of the purpose,
            // expect it to be last text to extract.
            .takeWhile { it.text() != "Kurser i ämnet" }
            .mapNotNull {
                when (it.tagName()) {
                // Paragraph
                    "p"  -> {
                        // Sometimes the heading is marked as <p> so skip the paragraph before bullet lists
                        if (it.nextElementSibling() != null && (it.nextElementSibling().tagName() == "ul" || it.nextElementSibling().tagName() == "ol")) {
                            null
                        } else {
                            val heading = if (
                                    it.previousElementSibling() != null &&
                                    it.previousElementSibling().`is`("h1,h2,h3,h4,h5,h6,i")
                            ) {
                                it.previousElementSibling().text()
                            } else {
                                ""
                            }
                            Purpose(PurposeType.PARAGRAPH, heading,
                                    it.text().split(Regex("(?<=\\.)"))
                                            .map { it.trim() }
                                            .filter { it.isNotEmpty() }
                            )
                        }
                    }
                // Bullet list, pick the previous element as heading
                    "ul", "ol" -> {
                        Purpose(PurposeType.BULLET,
                                if (it.previousElementSibling() != null) {
                                    it.previousElementSibling().text()
                                } else {
                                    ""
                                },
                                it.children().map {
                                    it.text().trim()
                                }
                        )
                    }
                // Heading
                    "h1","h2","h3","h4","h5","h6","i" -> {
                        // Only create a "loose" header if there is another header element following this one
                        if (it.nextElementSibling() == null || !it.nextElementSibling().`is`("p, ul, ol")) {
                            // Just a heading, cannot connect it to some content
                            Purpose(PurposeType.PARAGRAPH, it.text().trim(), listOf())
                        } else {
                            null
                        }
                    }
                    else -> null
                }
            }
}