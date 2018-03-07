package org.edtech.curriculum.internal

import org.edtech.curriculum.CentralContent
import org.edtech.curriculum.CentralContentType
import org.edtech.curriculum.Purpose
import org.edtech.curriculum.PurposeType
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * Extract contents from a JSOUP parsed HTML Strings
 */
class HtmlParser {
    private fun toSections(doc: Document): List<Purpose> {
        val elements :Elements = doc.select("p:not(:empty)")
        return elements.filter { it.html().isNotEmpty() }.map { Purpose(it.html(), PurposeType.SECTION) }
    }
    private fun toLongTermLongTermHeading(doc: Document): Purpose {
        val element : Element? = doc.select("h4").firstOrNull()
        return Purpose(element?.text()?:"", PurposeType.HEADING)
    }
    private fun toLongTermLongTermBullets(doc: Document): List<Purpose> {
        val element : Element? = doc.select("ol").firstOrNull()
        return element?.children()?.map { Purpose(it.html(), PurposeType.BULLET) }?:ArrayList()
    }


    /**
     * Combine heading and bullets in one list
     */
    fun toCentralContent(doc: Document): List<CentralContent> {
        return doc.select("strong, li, i, h1, h2, h3, h4, h5, h6").map {
            val type = when (it.tagName()) {
                "li" -> CentralContentType.BULLET
                else -> CentralContentType.HEADING
            }
            CentralContent(it.text(), type)
        }
    }

    /**
     * Combine all purpose types in one list
     */
    fun toPurposes(doc: Document): ArrayList<Purpose> {
        val contentList: ArrayList<Purpose> = ArrayList()
        contentList.addAll(toSections(doc))
        contentList.add(toLongTermLongTermHeading(doc))
        contentList.addAll(toLongTermLongTermBullets(doc))
        return contentList
    }
}