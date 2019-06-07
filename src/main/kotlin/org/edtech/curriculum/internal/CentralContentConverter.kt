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
        // Replace divs with their content.
        fragment.select("body > div").forEach {
            it.parent().insertChildren(it.siblingIndex(), it.children())
            it.remove()
        }

        return fragment
                .select("body > *:not(:empty)")
                .mapNotNull {element ->
                    if (element.tagName() == "ul") {
                        val lines = element.children()
                                .map { it.text().trim() }
                                .filter { it.isNotBlank() }
                        val heading = if (element.previousElementSibling()?.tagName() != "div" && element.previousElementSibling()?.tagName() != "ul") {
                            element.previousElementSibling()?.text()?.trim() ?: ""
                        } else {
                            ""
                        }

                        CentralContent(heading, lines)
                    } else {
                        // Just a heading
                        when {
                            element.nextElementSibling()?.tagName() == "ul" -> null
                            element.text().trim().isNotEmpty() ->
                                // Heading before
                                CentralContent(element.text().trim(), listOf())
                            else -> null
                        }
                    }
                }
    }
}

