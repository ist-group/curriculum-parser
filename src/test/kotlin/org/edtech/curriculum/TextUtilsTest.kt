package org.edtech.curriculum

import org.junit.Assert
import org.junit.jupiter.api.Test

internal class TextUtilsTest {

    @Test
    fun similarLineRatioTest() {
        compareSentences(
                "Eleven arrangerar och komponerar med                      vokalmusik med anpassning till den valda genrens konventioner och i textbehandlingen.",
                "Eleven arrangerar och komponerar med konstnärlig kvalitet instrumentalmusik med anpassning till den valda genrens konventioner.",
                "Eleven arrangerar och komponerar med konstnärlig kvalitet vokalmusik med anpassning till den valda genrens konventioner och i textbehandlingen."
        )
        compareSentences(
                "Dessutom utforskar eleven <strong>med viss säkerhet </strong>rörelsevokabulär.",
                "Eleven interagerar <strong>med viss säkerhet </strong>med andra och tar ansvar för det egna och det gemensamma arbetet genom att följa instruktioner.",
                "Eleven <strong>utforskar</strong> <strong>med god säkerhet </strong>rörelsevokabulär<strong> och rörelsekvaliteter samtidigt som hon eller han fördjupar improvisationen genom att stanna kvar i rörelsen</strong>."
        )
        compareSentences(
                "Eleven <strong>medverkar </strong>i att välja och använda strategier för lyssnande och läsning.",
                "Eleven visar sin förståelse genom att <strong>medverka </strong>i att redogöra för innehållet.",
                "Eleven använder<strong> någon </strong>strategi för lyssnande och läsning."
        )
        compareSentences(
                "Eleven <strong>medverkar</strong> i att hantera produkter från lantbruksdjur på ett hygieniskt och säkert sätt.",
                "Eleven <strong>medverkar</strong> också i att arbeta på ett etiskt, hygieniskt och säkert sätt.",
                "Eleven hanterar produkter från lantbruksdjur på ett hygieniskt och säkert sätt."
        )
        compareSentences(
                "Eleven <strong>medverkar</strong> också i att arbeta på ett etiskt, hygieniskt och säkert sätt.",
                "Eleven hanterar produkter från lantbruksdjur på ett hygieniskt och säkert sätt.",
                "Eleven arbetar på ett etiskt, hygieniskt och säkert sätt."
        )
        compareSentences(
                "Elever kan <strong>med viss säkerhet </strong>samla, sovra och sammanställa information från olika källor.",
                "Med utgångspunkt från detta kan eleven skriva utredande och argumenterande texter som är sammanhängande <strong>och har tydligt urskiljbar disposition</strong>.",
                "Elever kan <strong>med säkerhet </strong>samla, sovra och sammanställa information från olika källor och kan med utgångspunkt från detta skriva utredande och argumenterande texter som är sammanhängande och <strong>väldisponerade</strong>."
        )
    }

    private fun compareSentences(shouldMatch: String, shouldNotMatch: String, matchString: String) {
        val correctMatch =  similarLineRatio(shouldMatch, matchString)
        val incorrectMatch =  similarLineRatio(shouldNotMatch, matchString)
        Assert.assertTrue("Wrong sentence got higher score $correctMatch > $incorrectMatch", correctMatch > incorrectMatch)
    }

    @Test
    fun removeInflectionsTest() {
        Assert.assertEquals(listOf("elev", "elev", "elev"), removeInflections(listOf("eleven", "elever", "elevens")))
    }
}
