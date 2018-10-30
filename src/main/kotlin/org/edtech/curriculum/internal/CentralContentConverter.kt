package org.edtech.curriculum.internal

import org.edtech.curriculum.CentralContent
import org.jsoup.Jsoup


class CentralContentConverter {
    /**
     * Combine heading and bullets in one list
     */
    internal fun getCentralContents(html: String): List<CentralContent> {
        val fragment = Jsoup.parseBodyFragment(convertDashListToList(html))

        // Remove empty paragraphs
        fragment.select("body > p")
                .forEach {
                    if (it.text().trim().isEmpty()) {
                        it.remove()
                    }
                }
        return fragment
                .select("body > *:not(:empty)")
                .flatMap { if (it.tagName() == "div") it.children() else listOf(it) }
                .mapNotNull {
                    if (it.tagName() == "ul") {
                        val lines = it.children()
                                .map { it.text().trim() }
                                .filter { it.isNotBlank() }
                        val heading = if (it.previousElementSibling()?.tagName() != "div") {
                            it.previousElementSibling()?.text()?.trim() ?: ""
                        } else {
                            ""
                        }

                        CentralContent(heading, lines)
                    } else {
                        // Just a heading
                        when {
                            it.nextElementSibling()?.tagName() == "ul" -> null
                            it.text().trim().isNotEmpty() ->
                                // Heading before
                                CentralContent(it.text().trim(), listOf())
                            else -> null
                        }
                    }
                }
    }
}

