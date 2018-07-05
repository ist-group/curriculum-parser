package org.edtech.curriculum.internal

import org.edtech.curriculum.CentralContent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class CentralContentConverterTest {

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
                CentralContentConverter().getCentralContents("<h3>I årskurs 1-3</h3><h4> Bildframställning</h4><p> </p><ul> <li>Framställning av berättande bilder, till exempel sagobilder.</li> <li>Teckning, måleri, modellering och konstruktion.</li> <li>Fotografering och överföring av bilder med hjälp av datorprogram.</li> </ul><p> </p><h4> Redskap för bildframställning</h4><p> </p><ul> <li>Olika element som bygger upp en bild: färg, form, linje, yta samt för- och bakgrund.</li> <li>Några verktyg för teckning, måleri, modellering, konstruktioner och fotografering och hur dessa benämns.</li> <li>Plana och formbara material, till exempel papper, lera, gips och naturmaterial och hur dessa kan användas i olika bildarbeten.</li> </ul><p> </p><h4> Bildanalys</h4><p> </p><ul> <li>Informativa bilder, till exempel läroboksbilder och hur de är utformade och fungerar.</li> <li>Historiska och samtida bilder och vad bilderna berättar, till exempel dokumentära bilder från hemorten och konstbilder.</li> </ul><p></p>")
        )
        assertEquals(
                listOf(
                        CentralContent("I årskurs 1-3", listOf()),
                        CentralContent("Läsa och skriva", listOf(
                                "Lässtrategier för att förstå och tolka texter samt för att anpassa läsningen efter textens form och innehåll.",
                                "Strategier för att skriva olika typer av texter om för eleven välbekanta ämnen.",
                                "Läsriktning och skrivteckens form och ljud i jämförelse med svenska.",
                                "Ordföljd och interpunktion samt stavningsregler för vanligt förekommande ord i elevnära texter. Jämförelser med svenskans ordföljd, interpunktion och stavningsregler."
                        )),
                        CentralContent("Tala, lyssna och samtala", listOf(
                                "Muntligt berättande för olika mottagare.",
                                "Uttal, betoning och satsmelodi och uttalets betydelse för att göra sig förstådd.",
                                "Modersmålets uttal i jämförelse med svenskans."
                        )),
                        CentralContent("Berättande texter och sakprosatexter", listOf(
                                "Berättande texter och poetiska texter för barn i form av bilderböcker, kapitelböcker, lyrik, sagor och myter från olika tider och områden där modersmålet talas. Berättande och poetiska texter som belyser människors upplevelser och erfarenheter.",
                                "Rim, ramsor och gåtor ur modersmålets tradition.",
                                "Beskrivande och förklarande texter för barn med anknytning till traditioner, företeelser och språkliga uttryckssätt i områden där modersmålet talas."
                        )),
                        CentralContent("Språkbruk", listOf(
                                "Ord och begrepp för att uttrycka känslor, kunskaper och åsikter."
                        )),
                        CentralContent("Kultur och samhälle", listOf(
                                "Traditioner och högtider som eleven möter i olika sammanhang.",
                                "Lekar och musik från områden där modersmålet talas."
                        ))
                ),
                CentralContentConverter().getCentralContents(" <h3>I årskurs 1-3</h3><h4>Läsa och skriva</h4> " +
                        "<p>– Lässtrategier för att förstå och tolka texter samt för att anpassa läsningen efter textens form och innehåll.</p> " +
                        "<p>– Strategier för att skriva olika typer av texter om för eleven välbekanta ämnen.</p> " +
                        "<p>– Läsriktning och skrivteckens form och ljud i jämförelse med svenska.</p> " +
                        "<p>– Ordföljd och interpunktion samt stavningsregler för vanligt förekommande ord i elevnära texter. Jämförelser med svenskans ordföljd, interpunktion och stavningsregler.</p> " +
                        "<h4>Tala, lyssna och samtala</h4> " +
                        "<p>– Muntligt berättande för olika mottagare.</p> " +
                        "<p>– Uttal, betoning och satsmelodi och uttalets betydelse för att göra sig förstådd.</p> " +
                        "<p>– Modersmålets uttal i jämförelse med svenskans.</p> " +
                        "<h4>Berättande texter och sakprosatexter</h4> " +
                        "<p>– Berättande texter och poetiska texter för barn i form av bilderböcker, kapitelböcker, lyrik, sagor och myter från olika tider och områden där modersmålet talas. Berättande och poetiska texter som belyser människors upplevelser och erfarenheter.</p> " +
                        "<p>– Rim, ramsor och gåtor ur modersmålets tradition.</p> " +
                        "<p>– Beskrivande och förklarande texter för barn med anknytning till traditioner, företeelser och språkliga uttryckssätt i områden där modersmålet talas.</p> " +
                        "<h4>Språkbruk</h4> " +
                        "<p>– Ord och begrepp för att uttrycka känslor, kunskaper och åsikter.</p> " +
                        "<h4>Kultur och samhälle</h4> " +
                        "<p>– Traditioner och högtider som eleven möter i olika sammanhang.</p> " +
                        "<p>– Lekar och musik från områden där modersmålet talas.</p>")
        )
        assertEquals(
                listOf(
                        CentralContent("I årskurs 1–3", listOf()),
                        CentralContent("Bildframställning", listOf(
                                "Framställning av berättande bilder, till exempel sagobilder.",
                                "Teckning, måleri och modellering.",
                                "Presentation av eget bildskapande."
                        )),
                        CentralContent("Redskap för bildframställning", listOf(
                                "Några verktyg och tekniker för bildframställning."))
                ),
                CentralContentConverter().getCentralContents("<h3>I årskurs 1&ndash;3</h3>\n" +
                        "<div>\n" +
                        "    <h4>Bildframställning</h4>\n" +
                        "    <ul>\n" +
                        "        <li>Framställning av berättande bilder, till exempel sagobilder.</li>\n" +
                        "        <li>Teckning, måleri och modellering.</li>\n" +
                        "        <li>Presentation av eget bildskapande.</li>\n" +
                        "    </ul>\n" +
                        "    <h4>Redskap för bildframställning</h4>\n" +
                        "    <ul>\n" +
                        "        <li>Några verktyg och tekniker för bildframställning.</li>\n" +
                        "    </ul>\n" +
                        "</div>")
        )
    }

    @Test
    fun testEmptyLines() {
        val centralContents = CentralContentConverter().getCentralContents("<h4> Testar tomma rader  </h4><ul><li>Rad 1</li><li></li><li>  Rad 2 </li> <li> </li>")
        assertAll(
                { assertEquals(1, centralContents.size) },
                { assertEquals("Testar tomma rader", centralContents[0].heading) },
                { assertEquals(2, centralContents[0].lines.size) },
                { assertEquals("Rad 2", centralContents[0].lines[1]) }
        )

    }
}