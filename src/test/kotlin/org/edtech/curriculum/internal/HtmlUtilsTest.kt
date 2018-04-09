package org.edtech.curriculum.internal

import org.edtech.curriculum.CentralContent
import org.edtech.curriculum.YearGroup
import org.junit.Assert.*
import org.junit.Test

internal class HtmlUtilsTest {

    @Test
    fun getPlaceHolderTextTest() {
        assertEquals(
                "Dessutom utforskar eleven <strong>________</strong> rörelsevokabulär.",
                getPlaceHolderText("Dessutom utforskar eleven <strong>med viss säkerhet </strong>rörelsevokabulär."))
        assertEquals("Dessutom utforskar eleven <strong>________</strong> rörelsevokabulär.",
                getPlaceHolderText("Dessutom utforskar eleven <strong>med viss säkerhet </strong> rörelsevokabulär."))
        assertEquals("Dessutom utforskar eleven <strong>________</strong> rörelsevokabulär.",
                getPlaceHolderText("Dessutom utforskar eleven<strong> med viss säkerhet </strong> rörelsevokabulär."))
        assertEquals("Dessutom utforskar eleven <strong>________</strong>.",
                getPlaceHolderText("Dessutom utforskar eleven <strong>med viss säkerhet</strong>."))
        assertEquals("",
                getPlaceHolderText("<strong>Dessutom utforskar eleven med viss säkerhet .</strong> "))
        assertEquals("",
                getPlaceHolderText("<strong>Dessutom utforskar eleven med viss säkerhet .</strong>"))
        assertEquals("",
                getPlaceHolderText("<strong>Dessutom utforskar eleven <strong>med viss säkerhet .</strong>"))

    }

    @Test
    fun getParagraphsTest() {
        assertArrayEquals(
                arrayOf("1", "2", "3"),
                getParagraphs("<p>1<p><p>2<p><p class=\"header\">3</p>").toTypedArray()
        )
        assertArrayEquals(
                arrayOf("1", "1.1", "2", "3"),
                getParagraphs("<p>1<p>1.1</p><p><p>2<p><p class=\"header\">3</p>").toTypedArray()
        )
    }

    @Test
    fun splitParagraphTest() {
        assertArrayEquals(
                arrayOf("line1.", "line2.", "line3.", "line4 ."),
                splitParagraph("line1. line2. line3. line4 .").toTypedArray()
        )

    }

    @Test
    fun similarLineRatioTest() {
        assertTrue(
                similarLineRatio(
                        "Dessutom utforskar eleven <strong>med viss säkerhet </strong>rörelsevokabulär.",
                        "Eleven <strong>utforskar</strong> <strong>med god säkerhet </strong>rörelsevokabulär<strong> och rörelsekvaliteter samtidigt som hon eller han fördjupar improvisationen genom att stanna kvar i rörelsen</strong>."
                )
                        >
                        similarLineRatio(
                                "Eleven interagerar <strong>med viss säkerhet </strong>med andra och tar ansvar för det egna och det gemensamma arbetet genom att följa instruktioner.",
                                "Eleven <strong>utforskar</strong> <strong>med god säkerhet </strong>rörelsevokabulär<strong> och rörelsekvaliteter samtidigt som hon eller han fördjupar improvisationen genom att stanna kvar i rörelsen</strong>.")
        )
    }

    @Test
    fun removeInflectionsTest() {
        assertEquals(listOf("elev", "elev", "elev"), removeInflections(listOf("eleven", "elever", "elevens")))
    }

    @Test
    fun removeBoldWords() {
        assertEquals("Eleven förhåller sig konstnärligt till rörelsevokabulär samt varierar och utvecklar rörelseuttryck utifrån ",
                removeBoldWords("Eleven förhåller sig <strong>med viss säkerhet </strong>konstnärligt till rörelsevokabulär samt varierar och utvecklar rörelseuttryck utifrån <strong>instruktioner</strong>")
        )
        assertEquals("Eleven förhåller sig konstnärligt till rörelsevokabulär och varierar, och utvecklar rörelseuttryck efter .",
                removeBoldWords("Eleven förhåller sig <strong>med god säkerhet </strong>konstnärligt till rörelsevokabulär och varierar, <strong>undersöker </strong>och utvecklar<strong> konsekvent </strong>rörelseuttryck efter <strong>olika krav</strong>.")
        )
    }

    @Test
    fun toYearGroupTest() {
        assertEquals(YearGroup(1, 3), toYearGroup("1-3"))
        assertEquals(YearGroup(null, 3), toYearGroup("3"))
        assertEquals(YearGroup(3, 6), toYearGroup("3-6"))
        assertEquals(YearGroup(null, 3), toYearGroup("-3"))
        assertEquals(YearGroup(4, 6), toYearGroup("4-"))
        assertNull(toYearGroup(""))
    }

    @Test
    fun toCentralContentTest() {
        assertEquals(
                listOf(
                        CentralContent("I årskurs 1-3", listOf()),
                        CentralContent("Bildframställning", listOf(
                             "Framställning av berättande bilder, till exempel sagobilder.",
                             "Teckning, måleri, modellering och konstruktion.",
                             "Fotografering och överföring av bilder med hjälp av datorprogram."
                         )),
                        CentralContent("Redskap för bildframställning", listOf(
                            "Olika element som bygger upp en bild: färg, form, linje, yta samt för- och bakgrund.",
                            "Några verktyg för teckning, måleri, modellering, konstruktioner och fotografering och hur dessa benämns.",
                            "Plana och formbara material, till exempel papper, lera, gips och naturmaterial och hur dessa kan användas i olika bildarbeten."
                        )),
                        CentralContent("Bildanalys", listOf(
                            "Informativa bilder, till exempel läroboksbilder och hur de är utformade och fungerar.",
                            "Historiska och samtida bilder och vad bilderna berättar, till exempel dokumentära bilder från hemorten och konstbilder."
                        ))
                ),
                toCentralContent("<h3>I årskurs 1-3</h3><h4> Bildframställning</h4><p> </p><ul> <li>Framställning av berättande bilder, till exempel sagobilder.</li> <li>Teckning, måleri, modellering och konstruktion.</li> <li>Fotografering och överföring av bilder med hjälp av datorprogram.</li> </ul><p> </p><h4> Redskap för bildframställning</h4><p> </p><ul> <li>Olika element som bygger upp en bild: färg, form, linje, yta samt för- och bakgrund.</li> <li>Några verktyg för teckning, måleri, modellering, konstruktioner och fotografering och hur dessa benämns.</li> <li>Plana och formbara material, till exempel papper, lera, gips och naturmaterial och hur dessa kan användas i olika bildarbeten.</li> </ul><p> </p><h4> Bildanalys</h4><p> </p><ul> <li>Informativa bilder, till exempel läroboksbilder och hur de är utformade och fungerar.</li> <li>Historiska och samtida bilder och vad bilderna berättar, till exempel dokumentära bilder från hemorten och konstbilder.</li> </ul><p></p>")
        )
    }

}