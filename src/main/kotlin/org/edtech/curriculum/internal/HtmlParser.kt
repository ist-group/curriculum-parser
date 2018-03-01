package org.edtech.curriculum.internal

import org.edtech.curriculum.CentralContent
import org.edtech.curriculum.CentralContentType
import org.edtech.curriculum.Purpose
import org.edtech.curriculum.PurposeType
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.concurrent.atomic.AtomicInteger

/**
 * Extract contents from a JSOUP parsed HTML Strings
 */
class HtmlParser {
    private fun toSections(doc: Document): List<Purpose> {
        val elements :Elements = doc.select("p:not(:empty)")
        val ai = AtomicInteger()
        return elements.filter { it.html().isNotEmpty() }.map { Purpose(it.html(), ai.incrementAndGet(), PurposeType.SECTION) }
    }
    private fun toLongTermLongTermHeading(doc: Document): Purpose {
        val element : Element? = doc.select("h4").firstOrNull()
        return Purpose(element?.text()?:"", -1, PurposeType.HEADING)
    }
    private fun toLongTermLongTermBullets(doc: Document): List<Purpose> {
        val element : Element? = doc.select("ol").firstOrNull()
        val ai = AtomicInteger()
        return element?.children()?.map { Purpose(it.html(), ai.incrementAndGet(), PurposeType.BULLET) }?:ArrayList()
    }


    /**
     * Combine heading and bullets in one list
     */
    fun toCentralContent(doc: Document): List<CentralContent> {
        val ai = AtomicInteger()
        return doc.select("strong, li").map {
            val type = when (it.tagName()) {
                "strong" -> CentralContentType.HEADING
                else -> CentralContentType.BULLET
            }
            val lineNo = if (type == CentralContentType.HEADING) -1 else ai.incrementAndGet()
            CentralContent(it.text(), lineNo, type)
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