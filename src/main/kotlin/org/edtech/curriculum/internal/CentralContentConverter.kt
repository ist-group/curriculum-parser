package org.edtech.curriculum.internal

import org.edtech.curriculum.CentralContent
import org.jsoup.Jsoup


class CentralContentConverter {
    /**
     * Combine heading and bullets in one list
     */
    internal fun getCentralContents(html: String): List<CentralContent> {
        val fragment = Jsoup.parseBodyFragment(html)
        // Remove empty paragraphs
        fragment.select("body > p")
                .forEach {
                    if (it.text().trim().isEmpty()) {
                        it.remove()
                    }
                }
        return normalizeCentralContents(fragment
                .select("body > *:not(:empty)")
                .flatMap { if (it.tagName() == "div") it.children() else listOf(it) }
                .mapNotNull {
                    if (it.tagName() == "ul") {
                        val lines = it.children().map { it.text() }.filter { it.isNotBlank() }.map { it.trim() }
                        val heading = it.previousElementSibling()?.text()?.trim() ?: ""
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
                })
    }


    private fun normalizeCentralContents(centralContents: List<CentralContent>):  List<CentralContent> {
        val result = mutableListOf<CentralContent>()
        var lastCentralContent: CentralContent? = null
        centralContents.forEach { centralContent ->
            // this is a line that should be together with the last item
            lastCentralContent = if (centralContent.heading.startsWith("– "))  {
                if (lastCentralContent != null) {
                    CentralContent(lastCentralContent?.heading ?: "", (lastCentralContent?.lines ?: listOf()) + centralContent.heading.removePrefix("– "))
                } else {
                    CentralContent( "", listOf(centralContent.heading.removePrefix("- ")))
                }
            } else {
                if (lastCentralContent != null) {
                    result.add(lastCentralContent!!)
                }
                centralContent
            }
        }
        if (lastCentralContent != null) {
            result.add(lastCentralContent!!)
        }
        return result
    }

}

