package org.edtech.curriculum.internal

import org.edtech.curriculum.CentralContent
import org.edtech.curriculum.Purpose
import org.edtech.curriculum.PurposeType
import org.edtech.curriculum.YearGroup
import org.jsoup.Jsoup
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

    @Test fun fixCurriculumErrorsText() {
        assertEquals(
            "<h4>Betyget A</h4><p>Eleven planerar och organiserar <strong>efter samråd </strong>med handledare olika arbetsuppgifter i matsal eller lokal utifrån de tidsramar som ska gälla för arbetets utförande. I planeringen väljer eleven <strong>efter samråd </strong>med handledare metoder, material, redskap och annan utrustning utifrån olika teman och högtider. Eleven skapar <strong>med säkerhet </strong>bordsdekorationer för olika arrangemang och ceremoniella måltider.</p><p>Eleven kombinerar, presenterar och rekommenderar <strong>med säkerhet </strong>mat och dryck utifrån meny och dryckeslista samt sätter samman promemorior, olika arrangé och matsedlar för gästers räkning. Eleven utför med<strong> mycket gott </strong>handlag servering av mat och dryck vid olika beställningsarrangemang med tanke på tidsåtgång, ekonomi och miljö samt sätter <strong>efter samråd </strong>med handledare samman körscheman för detta. Dessutom bemöter eleven gästen och utför <strong>efter samråd </strong>med handledare arbetet på ett serviceinriktat sätt. Eleven utför också kalkylering, prissättning och lönsamhetsberäkningar av olika beställningsarrangemang <strong>efter samråd </strong>med handledare. </p><p>Eleven arbetar hygieniskt, ergonomiskt och på ett sätt som är säkert för eleven själv och andra utifrån lagar och andra bestämmelser. När arbetet är utfört utvärderar eleven sitt arbete och resultat med <strong>nyanserade </strong>omdömen <strong>samt ger förslag på hur arbetet kan förbättras</strong>. När eleven samråder med handledare bedömer hon eller han <strong>med säkerhet </strong>den egna förmågan och situationens krav.</p>",
            fixCurriculumErrors("<h4>Betyget A</h4><p>Eleven planerar och organiserar <strong>efter samråd </strong>med handledare olika arbetsuppgifter i matsal eller lokal utifrån de tidsramar som ska gälla för arbetets utförande. I planeringen väljer eleven <strong>efter samråd </strong>med handledare metoder, material, redskap och annan utrustning utifrån olika teman och högtider. Eleven skapar <strong>med säkerhet </strong>bordsdekorationer för olika arrangemang och ceremoniella måltider.</p><p>Eleven kombinerar, presenterar och rekommenderar <strong>med säkerhet </strong>mat och dryck utifrån meny och dryckeslista samt sätter samman promemorior, olika arrangé och matsedlar för gästers räkning. Eleven utför med<strong> mycket gott </strong>handlag servering av mat och dryck vid olika beställningsarrangemang med tanke på tidsåtgång, ekonomi och miljö samt sätter <strong>efter samråd </strong>med handledare<strong> </strong>samman körscheman för detta. Dessutom bemöter eleven gästen och utför <strong>efter samråd </strong>med handledare arbetet på ett serviceinriktat sätt. Eleven utför också kalkylering, prissättning och lönsamhetsberäkningar av olika beställningsarrangemang <strong>efter samråd </strong>med handledare. </p><p>Eleven arbetar hygieniskt, ergonomiskt och på ett sätt som är säkert för eleven själv och andra utifrån lagar och andra bestämmelser. När arbetet är utfört utvärderar eleven sitt arbete och resultat med <strong>nyanserade </strong>omdömen <strong>samt ger förslag på hur arbetet kan förbättras</strong>. När eleven samråder med handledare bedömer hon eller han <strong>med säkerhet </strong>den egna förmågan och situationens krav.</p>")
        )
        assertEquals(
            "<p>Text text.</p>",
            fixCurriculumErrors("<p>Text <italic>text</italic>.</p>")
        )
        assertEquals(
            "<p>Text text.</p>",
            fixCurriculumErrors("<p>Text<strong> </strong>text.</p>")
        )
        assertEquals(
            "<p>Text<strong> text</strong>.</p>",
            fixCurriculumErrors("<p>Text<strong> <italic> text</italic></strong>.</p>")
        )
        assertEquals(
            "<p><strong>Text text</strong>. </p>",
            fixCurriculumErrors("<p><strong>Text</strong><strong> text</strong>.<br></p>")
        )
        assertEquals(
            "<strong>Text text</strong>",
            fixCurriculumErrors("<strong>Text</strong> <strong>text</strong>")
        )
        assertEquals(
            "<p>Text<strong> text</strong>. </p>",
            fixCurriculumErrors("<p>Text<strong> text</strong>.<br/></p>")
        )
        assertEquals(
            "<p>Text<strong> text</strong>. </p>",
            fixCurriculumErrors("<p>Text<strong> text</strong>.<br/></p>")
        )
        assertEquals(
            "<p>Text text.</p>",
            fixCurriculumErrors("<p>Text text</p><p>.</p>")
        )
        assertEquals(
            "<p>Text text.</p>",
            fixCurriculumErrors("<p>Text text.</p><p>.</p>")
        )
    }

    @Test
    fun convertDashListToListTest(){
        assertEquals(
                "<h3>I årskurs 4-6</h3>\n" +
                        "<h4>Läsa och skriva</h4>\n" +
                        "<ul>\n" +
                        " <li>Lässtrategier för att förstå och tolka texter från olika medier samt för att urskilja texters budskap, både det uttalade och sådant som står mellan raderna.</li>\n" +
                        " <li>Strategier för att skriva olika typer av texter med anpassning till deras typiska uppbyggnad och språkliga drag.</li>\n" +
                        " <li>Modersmålets grundläggande struktur i jämförelse med svenskans.</li>\n" +
                        " <li>Ordböcker och andra hjälpmedel för stavning och ordförståelse.</li>\n" +
                        "</ul>\n" +
                        "<h4>Tala, lyssna och samtala</h4>\n" +
                        "<ul>\n" +
                        " <li>Muntliga presentationer för olika mottagare.</li>\n" +
                        " <li>Uttal, betoning och satsmelodi samt uttalets betydelse för att göra sig förstådd.</li>\n" +
                        " <li>Modersmålets uttal i jämförelse med svenskans.</li>\n" +
                        "</ul>\n" +
                        "<h4>Berättande texter och sakprosatexter</h4>\n" +
                        "<ul>\n" +
                        " <li>Berättande texter och poetiska texter för barn och unga i form av skönlitteratur, lyrik, sagor och myter från olika tider och områden där modersmålet talas. Berättande och poetiska texter som belyser människors villkor, identitets- och livsfrågor.</li>\n" +
                        " <li>Berättande och poetiska texters typiska språkliga drag samt deras ord och begrepp.</li>\n" +
                        " <li>Beskrivande, förklarande och instruerande texter för barn och unga med anknytning till traditioner, företeelser och språkliga uttryckssätt i områden där modersmålet talas.</li>\n" +
                        " <li>Texternas innehåll och deras typiska ord och begrepp.</li>\n" +
                        "</ul>\n" +
                        "<h4>Språkbruk</h4>\n" +
                        "<ul>\n" +
                        " <li>Ord och begrepp för att uttrycka känslor, kunskaper och åsikter. Ords och begrepps nyanser och värdeladdning.</li>\n" +
                        " <li>Synonymer och motsatsord.</li>\n" +
                        "</ul>\n" +
                        "<h4>Kultur och samhälle</h4>\n" +
                        "<ul>\n" +
                        " <li>Seder, bruk och traditioner i områden där modersmålet talas i jämförelse med svenska seder, bruk och traditioner.</li>\n" +
                        " <li>Skolgång i områden där modersmålet talas i jämförelse med skolgång i Sverige.</li>\n" +
                        "</ul>",
                convertDashListToList("<h3>I årskurs 4-6</h3><h4>Läsa och skriva</h4><p>– Lässtrategier för att förstå och tolka texter från olika medier samt för att urskilja texters budskap, både det uttalade och sådant som står mellan raderna.</p><p>– Strategier för att skriva olika typer av texter med anpassning till deras typiska uppbyggnad och språkliga drag.</p><p>– Modersmålets grundläggande struktur i jämförelse med svenskans.</p><p>– Ordböcker och andra hjälpmedel för stavning och ordförståelse.</p><h4>Tala, lyssna och samtala</h4><p>– Muntliga presentationer för olika mottagare.</p><p>– Uttal, betoning och satsmelodi samt uttalets betydelse för att göra sig förstådd.</p><p>– Modersmålets uttal i jämförelse med svenskans.</p><h4>Berättande texter och sakprosatexter</h4><p>– Berättande texter och poetiska texter för barn och unga i form av skönlitteratur, lyrik, sagor och myter från olika tider och områden där modersmålet talas. Berättande och poetiska texter som belyser människors villkor, identitets- och livsfrågor.</p><p>– Berättande och poetiska texters typiska språkliga drag samt deras ord och begrepp.</p><p>– Beskrivande, förklarande och instruerande texter för barn och unga med anknytning till traditioner, företeelser och språkliga uttryckssätt i områden där modersmålet talas.</p><p>– Texternas innehåll och deras typiska ord och begrepp.</p><h4>Språkbruk</h4><p>– Ord och begrepp för att uttrycka känslor, kunskaper och åsikter. Ords och begrepps nyanser och värdeladdning.</p><p>– Synonymer och motsatsord.</p><h4>Kultur och samhälle</h4><p>– Seder, bruk och traditioner i områden där modersmålet talas i jämförelse med svenska seder, bruk och traditioner.</p><p>– Skolgång i områden där modersmålet talas i jämförelse med skolgång i Sverige.</p>")
        )
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

    @Test
    fun toPurposesTest() {
        assertEquals(
                listOf(
                        Purpose(PurposeType.PARAGRAPH,"", listOf(
                                "Undervisningen i ämnet biologi ska syfta till att eleverna utvecklar kunskaper om biologins begrepp, teorier, modeller och arbetsmetoder.",
                                "Den ska bidra till att eleverna utvecklar förståelse av biologins betydelse i samhället, till exempel för livskvalitet och hälsa genom medicinen, och för skyddandet av jordens ekosystem genom ekologin.",
                                "Genom undervisningen ska eleverna ges möjlighet att utveckla ett naturvetenskapligt perspektiv på vår omvärld med evolutionsteorin som grund.",
                                "I undervisningen ska aktuell forskning och elevernas upplevelser, nyfikenhet och kreativitet tas till vara.",
                                "Undervisningen ska också bidra till att eleverna, från en naturvetenskaplig utgångspunkt, kan delta i samhällsdebatten och diskutera etiska frågor och ställningstaganden."
                        )),
                        Purpose(PurposeType.PARAGRAPH,"", listOf(
                                "Molekylärbiologin, liksom många andra områden inom biologin, utvecklas i snabb takt.",
                                "Utvecklingen sker i ett samspel mellan teori och experiment, där hypoteser, teorier och modeller testas, omvärderas och förändras.",
                                "Undervisningen ska därför behandla teoriers och modellers utveckling, begränsningar och giltighetsområden.",
                                "Den ska bidra till att eleverna utvecklar förmåga att arbeta teoretiskt och experimentellt samt att kommunicera med hjälp av ett naturvetenskapligt språk.",
                                "Undervisningen ska också bidra till att eleverna utvecklar förmåga att kritiskt värdera och skilja mellan påståenden som bygger på vetenskaplig respektive icke-vetenskaplig grund."
                        )),
                        Purpose(PurposeType.PARAGRAPH, "", listOf(
                                "Undervisningen ska innefatta naturvetenskapliga arbetsmetoder som att formulera och söka svar på frågor, göra systematiska observationer, planera och utföra experiment och fältstudier samt bearbeta, tolka och kritiskt granska resultat och information.",
                                "I undervisningen ska eleverna ges tillfällen att argumentera kring och presentera analyser och slutsatser.",
                                "De ska även ges möjlighet att använda datorstödd utrustning för insamling, simulering, beräkning, bearbetning och presentation av data."
                        )),
                        Purpose(PurposeType.BULLET, "Undervisningen i ämnet biologi ska ge eleverna förutsättningar att utveckla följande:", listOf(
                                "Kunskaper om biologins begrepp, modeller, teorier och arbetsmetoder samt förståelse av hur dessa utvecklas.",
                                "Förmåga att analysera och söka svar på ämnesrelaterade frågor samt att identifiera, formulera och lösa problem. Förmåga att reflektera över och värdera valda strategier, metoder och resultat.",
                                "Förmåga att planera, genomföra, tolka och redovisa fältstudier, experiment och observationer samt förmåga att hantera material och utrustning.",
                                "Kunskaper om biologins betydelse för individ och samhälle.",
                                "Förmåga att använda kunskaper i biologi för att kommunicera samt för att granska och använda information."
                        ))
                ),
                toPurposes("<p>Undervisningen i ämnet biologi ska syfta till att eleverna utvecklar kunskaper om biologins begrepp, teorier, modeller och arbetsmetoder. Den ska bidra till att eleverna utvecklar förståelse av biologins betydelse i samhället, till exempel för livskvalitet och hälsa genom medicinen, och för skyddandet av jordens ekosystem genom ekologin. Genom undervisningen ska eleverna ges möjlighet att utveckla ett naturvetenskapligt perspektiv på vår omvärld med evolutionsteorin som grund. I undervisningen ska aktuell forskning och elevernas upplevelser, nyfikenhet och kreativitet tas till vara. Undervisningen ska också bidra till att eleverna, från en naturvetenskaplig utgångspunkt, kan delta i samhällsdebatten och diskutera etiska frågor och ställningstaganden.</p><p>Molekylärbiologin, liksom många andra områden inom biologin, utvecklas i snabb takt. Utvecklingen sker i ett samspel mellan teori och experiment, där hypoteser, teorier och modeller testas, omvärderas och förändras. Undervisningen ska därför behandla teoriers och modellers utveckling, begränsningar och giltighetsområden. Den ska bidra till att eleverna utvecklar förmåga att arbeta teoretiskt och experimentellt samt att kommunicera med hjälp av ett naturvetenskapligt språk. Undervisningen ska också bidra till att eleverna utvecklar förmåga att kritiskt värdera och skilja mellan påståenden som bygger på vetenskaplig respektive icke-vetenskaplig grund.</p><p>Undervisningen ska innefatta naturvetenskapliga arbetsmetoder som att formulera och söka svar på frågor, göra systematiska observationer, planera och utföra experiment och fältstudier samt bearbeta, tolka och kritiskt granska resultat och information. I undervisningen ska eleverna ges tillfällen att argumentera kring och presentera analyser och slutsatser. De ska även ges möjlighet att använda datorstödd utrustning för insamling, simulering, beräkning, bearbetning och presentation av data.</p><h4>Undervisningen i ämnet biologi ska ge eleverna förutsättningar att utveckla följande:</h4><p> </p><ol> <li>Kunskaper om biologins begrepp, modeller, teorier och arbetsmetoder samt förståelse av hur dessa utvecklas.</li> <li>Förmåga att analysera och söka svar på ämnesrelaterade frågor samt att identifiera, formulera och lösa problem. Förmåga att reflektera över och värdera valda strategier, metoder och resultat.</li> <li>Förmåga att planera, genomföra, tolka och redovisa<br/>fältstudier, experiment och observationer samt förmåga att hantera material och utrustning.</li> <li>Kunskaper om biologins betydelse för individ och samhälle.</li> <li>Förmåga att använda kunskaper i biologi för att kommunicera samt för att granska och använda information.</li> </ol><p></p><h3>Kurser i ämnet</h3><p></p><ol> <li>Biologi 1, 100 poäng, som bygger på grundskolans kunskaper eller motsvarande.</li> <li>Biologi 2, 100 poäng, som bygger på kursen biologi 1.</li> <li>Bioteknik, 100 poäng, som bygger på kursen biologi 1.</li> </ol><p></p>")
        )
        assertEquals(
                listOf(
                        Purpose(PurposeType.PARAGRAPH,"", listOf(
                                "Undervisningen i ämnet bild ska syfta till att eleverna utvecklar kunskaper om hur bilder skapas och kan tolkas.",
                                "Genom undervisningen ska eleverna få erfarenheter av visuell kultur där film, foto, design, konst, arkitektur och miljöer ingår."
                        )),
                        Purpose(PurposeType.PARAGRAPH,"", listOf(
                                "I undervisningen ska eleverna ges möjligheter att utveckla kunskaper om hur man framställer och presenterar egna bilder med olika metoder, material och uttrycksformer.",
                                "Undervisningen ska bidra till att eleverna utvecklar sin kreativitet och sitt intresse för att skapa.",
                                "Den ska också uppmuntra eleverna att ta egna initiativ och att arbeta på ett undersökande och problemlösande sätt."
                        )),
                        Purpose(PurposeType.PARAGRAPH, "", listOf(
                                "Undervisningen ska bidra till att eleverna utvecklar förståelse för hur bildbudskap utformas i olika medier.",
                                "Undervisningen ska också ge eleverna möjligheter att diskutera och kritiskt granska olika bildbudskap och bidra till att eleverna utvecklar kunskaper om bilder i olika kulturer, både historiskt och i nutid.",
                                "Genom undervisningen ska eleverna även ges möjlighet att använda sina kunskaper om olika typer av bilder i det egna bildskapandet."
                        )),
                        Purpose(PurposeType.BULLET, "Genom undervisningen i ämnet bild ska eleverna sammanfattningsvis ges förutsättningar att utveckla sin förmåga att", listOf(
                                "kommunicera med bilder för att uttrycka budskap,",
                                "skapa bilder med digitala och hantverksmässiga tekniker och verktyg samt med olika material,",
                                "undersöka och presentera olika ämnesområden med bilder, och",
                                "analysera historiska och samtida bilders uttryck, innehåll och funktioner."
                        ))
                ),
                toPurposes("<p>Undervisningen i ämnet bild ska syfta till att eleverna utvecklar kunskaper om hur bilder skapas och kan tolkas. Genom undervisningen ska eleverna få erfarenheter av visuell kultur där film, foto, design, konst, arkitektur och miljöer ingår.<br/>I undervisningen ska eleverna ges möjligheter att utveckla kunskaper om hur man framställer och presenterar egna bilder med olika metoder, material och uttrycksformer. Undervisningen ska bidra till att eleverna utvecklar sin kreativitet och sitt intresse för att skapa. Den ska också uppmuntra eleverna att ta egna initiativ och att arbeta på ett undersökande och problemlösande sätt.<br/>Undervisningen ska bidra till att eleverna utvecklar förståelse för hur bildbudskap utformas i olika medier. Undervisningen ska också ge eleverna möjligheter att diskutera och kritiskt granska olika bildbudskap och bidra till att eleverna utvecklar kunskaper om bilder i olika kulturer, både historiskt och i nutid. Genom undervisningen ska eleverna även ges möjlighet att använda sina kunskaper om olika typer av bilder i det egna bildskapandet.<br/>Genom undervisningen i ämnet bild ska eleverna sammanfattningsvis ges förutsättningar att utveckla sin förmåga att </p><ul> <li>kommunicera med bilder för att uttrycka budskap,</li> <li>skapa bilder med digitala och hantverksmässiga tekniker och verktyg samt med olika material,</li> <li>undersöka och presentera olika ämnesområden med bilder, och</li> <li>analysera historiska och samtida bilders uttryck, innehåll och funktioner.</li> </ul><p></p>")
        )
        assertEquals(
                listOf(
                        Purpose(PurposeType.PARAGRAPH,"", listOf(
                                "Undervisningen i ämnet matematik ska syfta till att eleverna utvecklar kunskaper om matematik och matematikens användning i vardagen.",
                                "Den ska bidra till att eleverna utvecklar intresse för matematik och en tilltro till sin egen förmåga att använda matematik i olika sammanhang."
                        )),
                        Purpose(PurposeType.PARAGRAPH,"", listOf(
                                "Vidare ska undervisningen ge eleverna möjlighet att utveckla kunskaper om grundläggande matematiska metoder och hur dessa kan användas för att besvara frågor i vardagliga situationer.",
                                "Undervisningen ska också bidra till att eleverna får uppleva matematiken som en estetisk och kreativ aktivitet som kan användas vid problemlösning och matematiska undersökningar."
                        )),
                        Purpose(PurposeType.PARAGRAPH, "", listOf(
                                "Undervisningen ska bidra till att eleverna utvecklar ett kritiskt förhållningssätt i situationer där det finns behov av att göra överväganden om matematisk rimlighet.",
                                "Eleverna ska genom undervisningen ges möjligheter att utveckla kunskaper i att använda digital teknik för att undersöka problemställningar, göra beräkningar och för att presentera och tolka resultat."
                        )),
                        Purpose(PurposeType.PARAGRAPH, "", listOf(
                                "Vidare ska undervisningen i matematik bidra till att eleverna utvecklar kunskaper om ämnesspecifika begrepp.",
                                "På så sätt ska eleverna ges förutsättningar att samtala om matematik och presentera och utvärdera arbetsprocesser."
                        )),
                        Purpose(PurposeType.BULLET, "Genom undervisningen i ämnet matematik ska eleverna sammanfattningsvis ges förutsättningar att utveckla sin förmåga att", listOf(
                                "lösa matematiska problem,",
                                "använda matematiska metoder för att göra beräkningar och lösa rutinuppgifter,",
                                "reflektera över rimlighet i situationer med matematisk anknytning, och",
                                "använda ämnesspecifika ord, begrepp och symboler."
                        ))
                ),
                toPurposes("<p>Undervisningen i ämnet matematik ska syfta till att eleverna utvecklar kunskaper om matematik och matematikens användning i vardagen. Den ska bidra till att eleverna utvecklar intresse för matematik och en tilltro till sin egen förmåga att använda matematik i olika sammanhang.</p><p>Vidare ska undervisningen ge eleverna möjlighet att utveckla kunskaper om grundläggande matematiska metoder och hur dessa kan användas för att besvara frågor i vardagliga situationer. Undervisningen ska också bidra till att eleverna får uppleva matematiken som en estetisk och kreativ aktivitet som kan användas vid problemlösning och matematiska undersökningar.</p><p>Undervisningen ska bidra till att eleverna utvecklar ett kritiskt förhållningssätt i situationer där det finns behov av att göra överväganden om matematisk rimlighet. Eleverna ska genom undervisningen ges möjligheter att utveckla kunskaper i att använda digital teknik för att undersöka problemställningar, göra beräkningar och för att presentera och tolka resultat.</p><p>Vidare ska undervisningen i matematik bidra till att eleverna utvecklar kunskaper om ämnesspecifika begrepp. På så sätt ska eleverna ges förutsättningar att samtala om matematik och presentera och utvärdera arbetsprocesser.</p><p>Genom undervisningen i ämnet matematik ska eleverna sammanfattningsvis ges förutsättningar att utveckla sin förmåga att<br/></p><ul> <li>lösa matematiska problem,</li> <li>använda matematiska metoder för att göra beräkningar och lösa rutinuppgifter,</li> <li>reflektera över rimlighet i situationer med matematisk anknytning, och</li> <li>använda ämnesspecifika ord, begrepp och symboler.</li> </ul><p></p>")
        )
    }
}