package org.edtech.curriculum.internal

import org.edtech.curriculum.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class HtmlUtilsTest {

    @Test
    fun getPlaceHolderTextTest() {
        assertEquals(
            "Dessutom utforskar eleven <strong>________</strong> rörelsevokabulär.",
            getPlaceHolderText("Dessutom utforskar eleven <strong>med viss säkerhet </strong>rörelsevokabulär.")
        )
        assertEquals(
            "Dessutom utforskar eleven <strong>________</strong> rörelsevokabulär.",
            getPlaceHolderText("Dessutom utforskar eleven <strong>med viss säkerhet </strong> rörelsevokabulär.")
        )
        assertEquals(
            "Dessutom utforskar eleven <strong>________</strong> rörelsevokabulär.",
            getPlaceHolderText("Dessutom utforskar eleven<strong> med viss säkerhet </strong> rörelsevokabulär.")
        )
        assertEquals(
            "Dessutom utforskar eleven <strong>________</strong>.",
            getPlaceHolderText("Dessutom utforskar eleven <strong>med viss säkerhet</strong>.")
        )
        assertEquals(
            "",
            getPlaceHolderText("<strong>Dessutom utforskar eleven med viss säkerhet .</strong> ")
        )
        assertEquals(
            "",
            getPlaceHolderText("<strong>Dessutom utforskar eleven med viss säkerhet .</strong>")
        )
        assertEquals(
            "",
            getPlaceHolderText("<strong>Dessutom utforskar eleven <strong>med viss säkerhet .</strong>")
        )

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
    fun fixCurriculumErrorsText() {

        assertEquals(
            "I muntliga framställningar av olika slag formulerar sig eleven enkelt, <strong>relativt varierat, tydligt och relativt</strong> sammanhängande. <strong>Eleven formulerar sig även med visst flyt och i någon mån anpassat till syfte, mottagare och situation</strong>. För att förtydliga och variera sin kommunikation bearbetar eleven, och gör <strong>välgrundade</strong> förbättringar av, egna framställningar.",
            fixCurriculumErrors("I muntliga framställningar av olika slag formulerar sig eleven enkelt, <strong>relativt varierat, tydligt och relativt </strong>sammanhängande.<strong> Eleven formulerar sig även med visst flyt och i någon mån anpassat till syfte, mottagare och situation.</strong> För att förtydliga och variera sin kommunikation bearbetar eleven, och gör <strong>välgrundade</strong> förbättringar av, egna framställningar.")
        )
        assertEquals(
            "Genom att svara på frågor om och återge delar av innehållet på ett <strong>relevant</strong> sätt visar eleven sin förståelse för texterna.",
            fixCurriculumErrors("Genom att svara på frågor om och återge delar av innehållet på ett <strong>relevant</strong> sätt visar eleven sin förståelse för texterna<strong>. </strong>")
        )
        assertEquals(
            "<h4>Betyget A</h4><p>Eleven planerar och organiserar <strong>efter samråd</strong> med handledare olika arbetsuppgifter i matsal eller lokal utifrån de tidsramar som ska gälla för arbetets utförande. I planeringen väljer eleven <strong>efter samråd</strong> med handledare metoder, material, redskap och annan utrustning utifrån olika teman och högtider. Eleven skapar <strong>med säkerhet</strong> bordsdekorationer för olika arrangemang och ceremoniella måltider.</p><p>Eleven kombinerar, presenterar och rekommenderar <strong>med säkerhet</strong> mat och dryck utifrån meny och dryckeslista samt sätter samman promemorior, olika arrangé och matsedlar för gästers räkning. Eleven utför med <strong>mycket gott</strong> handlag servering av mat och dryck vid olika beställningsarrangemang med tanke på tidsåtgång, ekonomi och miljö samt sätter <strong>efter samråd</strong> med handledare samman körscheman för detta. Dessutom bemöter eleven gästen och utför <strong>efter samråd</strong> med handledare arbetet på ett serviceinriktat sätt. Eleven utför också kalkylering, prissättning och lönsamhetsberäkningar av olika beställningsarrangemang <strong>efter samråd</strong> med handledare. </p><p>Eleven arbetar hygieniskt, ergonomiskt och på ett sätt som är säkert för eleven själv och andra utifrån lagar och andra bestämmelser. När arbetet är utfört utvärderar eleven sitt arbete och resultat med <strong>nyanserade</strong> omdömen <strong>samt ger förslag på hur arbetet kan förbättras</strong>. När eleven samråder med handledare bedömer hon eller han <strong>med säkerhet</strong> den egna förmågan och situationens krav.</p>",
            fixCurriculumErrors(
                "<h4>Betyget A</h4><p>Eleven planerar och organiserar <strong>efter samråd </strong>med handledare olika arbetsuppgifter i matsal eller lokal utifrån de tidsramar som ska gälla för arbetets utförande. I planeringen väljer eleven <strong>efter samråd </strong>med handledare metoder, material, redskap och annan utrustning utifrån olika teman och högtider. Eleven skapar <strong>med säkerhet </strong>bordsdekorationer för olika arrangemang och ceremoniella måltider.</p><p>Eleven kombinerar, presenterar och rekommenderar <strong>med säkerhet </strong>mat och dryck utifrån meny och dryckeslista samt sätter samman promemorior, olika arrangé och matsedlar för gästers räkning. Eleven utför med<strong> mycket gott </strong>handlag servering av mat och dryck vid olika beställningsarrangemang med tanke på tidsåtgång, ekonomi och miljö samt sätter <strong>efter samråd </strong>med handledare<strong> </strong>samman körscheman för detta. Dessutom bemöter eleven gästen och utför <strong>efter samråd </strong>med handledare arbetet på ett serviceinriktat sätt. Eleven utför också kalkylering, prissättning och lönsamhetsberäkningar av olika beställningsarrangemang <strong>efter samråd </strong>med handledare. </p><p>Eleven arbetar hygieniskt, ergonomiskt och på ett sätt som är säkert för eleven själv och andra utifrån lagar och andra bestämmelser. När arbetet är utfört utvärderar eleven sitt arbete och resultat med <strong>nyanserade </strong>omdömen <strong>samt ger förslag på hur arbetet kan förbättras</strong>. När eleven samråder med handledare bedömer hon eller han <strong>med säkerhet </strong>den egna förmågan och situationens krav.</p>"
            )
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
            "<p>Text <strong>text</strong>.</p>",
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
            "<p>Text <strong>text</strong>. </p>",
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
    fun fixDescriptionsTest() {
        assertEquals(
            "Text text.",
            fixDescriptions("<p>Text text.</p>")
        )
        assertEquals(
            "Text text.",
            fixDescriptions("<p align=\"left\">Text text.</p>")
        )
        assertEquals(
            "Text text.",
            fixDescriptions("<p align=\"left\">Text <i>text</i>.</p>")
        )
    }

    @Test
    fun convertDashListToListTest() {
        assertEquals(
            """<h3>I årskurs 4-6</h3>
<h4>Läsa och skriva</h4>
<ul>
 <li>Lässtrategier för att förstå och tolka texter från olika medier samt för att urskilja texters budskap, både det uttalade och sådant som står mellan raderna.</li>
 <li>Strategier för att skriva olika typer av texter med anpassning till deras typiska uppbyggnad och språkliga drag.</li>
 <li>Modersmålets grundläggande struktur i jämförelse med svenskans.</li>
 <li>Ordböcker och andra hjälpmedel för stavning och ordförståelse.</li>
</ul>
<h4>Tala, lyssna och samtala</h4>
<ul>
 <li>Muntliga presentationer för olika mottagare.</li>
 <li>Uttal, betoning och satsmelodi samt uttalets betydelse för att göra sig förstådd.</li>
 <li>Modersmålets uttal i jämförelse med svenskans.</li>
</ul>
<h4>Berättande texter och sakprosatexter</h4>
<ul>
 <li>Berättande texter och poetiska texter för barn och unga i form av skönlitteratur, lyrik, sagor och myter från olika tider och områden där modersmålet talas. Berättande och poetiska texter som belyser människors villkor, identitets- och livsfrågor.</li>
 <li>Berättande och poetiska texters typiska språkliga drag samt deras ord och begrepp.</li>
 <li>Beskrivande, förklarande och instruerande texter för barn och unga med anknytning till traditioner, företeelser och språkliga uttryckssätt i områden där modersmålet talas.</li>
 <li>Texternas innehåll och deras typiska ord och begrepp.</li>
</ul>
<h4>Språkbruk</h4>
<ul>
 <li>Ord och begrepp för att uttrycka känslor, kunskaper och åsikter. Ords och begrepps nyanser och värdeladdning.</li>
 <li>Synonymer och motsatsord.</li>
</ul>
<h4>Kultur och samhälle</h4>
<ul>
 <li>Seder, bruk och traditioner i områden där modersmålet talas i jämförelse med svenska seder, bruk och traditioner.</li>
 <li>Skolgång i områden där modersmålet talas i jämförelse med skolgång i Sverige.</li>
</ul>""",
            convertDashListToList(
                "<h3>I årskurs 4-6</h3><h4>Läsa och skriva</h4><p>– Lässtrategier för att förstå och tolka texter från olika medier samt för att urskilja texters budskap, både det uttalade och sådant som står mellan raderna.</p><p>– Strategier för att skriva olika typer av texter med anpassning till deras typiska uppbyggnad och språkliga drag.</p><p>– Modersmålets grundläggande struktur i jämförelse med svenskans.</p><p>– Ordböcker och andra hjälpmedel för stavning och ordförståelse.</p><h4>Tala, lyssna och samtala</h4><p>– Muntliga presentationer för olika mottagare.</p><p>– Uttal, betoning och satsmelodi samt uttalets betydelse för att göra sig förstådd.</p><p>– Modersmålets uttal i jämförelse med svenskans.</p><h4>Berättande texter och sakprosatexter</h4><p>– Berättande texter och poetiska texter för barn och unga i form av skönlitteratur, lyrik, sagor och myter från olika tider och områden där modersmålet talas. Berättande och poetiska texter som belyser människors villkor, identitets- och livsfrågor.</p><p>– Berättande och poetiska texters typiska språkliga drag samt deras ord och begrepp.</p><p>– Beskrivande, förklarande och instruerande texter för barn och unga med anknytning till traditioner, företeelser och språkliga uttryckssätt i områden där modersmålet talas.</p><p>– Texternas innehåll och deras typiska ord och begrepp.</p><h4>Språkbruk</h4><p>– Ord och begrepp för att uttrycka känslor, kunskaper och åsikter. Ords och begrepps nyanser och värdeladdning.</p><p>– Synonymer och motsatsord.</p><h4>Kultur och samhälle</h4><p>– Seder, bruk och traditioner i områden där modersmålet talas i jämförelse med svenska seder, bruk och traditioner.</p><p>– Skolgång i områden där modersmålet talas i jämförelse med skolgång i Sverige.</p>"
            )
        )
        assertEquals(
            """<h3>I årskurs 4-6</h3>
<h4>Läsa och skriva</h4>
<ul>
 <li>Lässtrategier för att förstå och tolka texter från olika medier samt för att urskilja texters budskap, både det uttalade och sådant som står mellan raderna.</li>
 <li>Strategier för att skriva olika typer av texter med anpassning till deras typiska uppbyggnad och språkliga drag.</li>
 <li>Modersmålets grundläggande struktur i jämförelse med svenskans.</li>
 <li>Ordböcker och andra hjälpmedel för stavning och ordförståelse.</li>
</ul>
<h4>Tala, lyssna och samtala</h4>
<ul>
 <li>Muntliga presentationer för olika mottagare.</li>
 <li>Uttal, betoning och satsmelodi samt uttalets betydelse för att göra sig förstådd.</li>
 <li>Modersmålets uttal i jämförelse med svenskans.</li>
</ul>
<h4>Berättande texter och sakprosatexter</h4>
<ul>
 <li>Berättande texter och poetiska texter för barn och unga i form av skönlitteratur, lyrik, sagor och myter från olika tider och områden där modersmålet talas. Berättande och poetiska texter som belyser människors villkor, identitets- och livsfrågor.</li>
 <li>Berättande och poetiska texters typiska språkliga drag samt deras ord och begrepp.</li>
 <li>Beskrivande, förklarande och instruerande texter för barn och unga med anknytning till traditioner, företeelser och språkliga uttryckssätt i områden där modersmålet talas.</li>
 <li>Texternas innehåll och deras typiska ord och begrepp.</li>
</ul>
<h4>Språkbruk</h4>
<ul>
 <li>Ord och begrepp för att uttrycka känslor, kunskaper och åsikter. Ords och begrepps nyanser och värdeladdning.</li>
 <li>Synonymer och motsatsord.</li>
</ul>
<h4>Kultur och samhälle</h4>
<ul>
 <li>Seder, bruk och traditioner i områden där modersmålet talas i jämförelse med svenska seder, bruk och traditioner.</li>
 <li>Skolgång i områden där modersmålet talas i jämförelse med skolgång i Sverige.</li>
</ul>""",
            convertDashListToList(
                "<h3>I årskurs 4-6</h3><h4>Läsa och skriva</h4><p>• Lässtrategier för att förstå och tolka texter från olika medier samt för att urskilja texters budskap, både det uttalade och sådant som står mellan raderna.</p><p>• Strategier för att skriva olika typer av texter med anpassning till deras typiska uppbyggnad och språkliga drag.</p><p>• Modersmålets grundläggande struktur i jämförelse med svenskans.</p><p>• Ordböcker och andra hjälpmedel för stavning och ordförståelse.</p><h4>Tala, lyssna och samtala</h4><p>• Muntliga presentationer för olika mottagare.</p><p>• Uttal, betoning och satsmelodi samt uttalets betydelse för att göra sig förstådd.</p><p>• Modersmålets uttal i jämförelse med svenskans.</p><h4>Berättande texter och sakprosatexter</h4><p>• Berättande texter och poetiska texter för barn och unga i form av skönlitteratur, lyrik, sagor och myter från olika tider och områden där modersmålet talas. Berättande och poetiska texter som belyser människors villkor, identitets- och livsfrågor.</p><p>• Berättande och poetiska texters typiska språkliga drag samt deras ord och begrepp.</p><p>• Beskrivande, förklarande och instruerande texter för barn och unga med anknytning till traditioner, företeelser och språkliga uttryckssätt i områden där modersmålet talas.</p><p>• Texternas innehåll och deras typiska ord och begrepp.</p><h4>Språkbruk</h4><p>• Ord och begrepp för att uttrycka känslor, kunskaper och åsikter. Ords och begrepps nyanser och värdeladdning.</p><p>• Synonymer och motsatsord.</p><h4>Kultur och samhälle</h4><p>• Seder, bruk och traditioner i områden där modersmålet talas i jämförelse med svenska seder, bruk och traditioner.</p><p>• Skolgång i områden där modersmålet talas i jämförelse med skolgång i Sverige.</p>"
            )

        )
        assertEquals(
            """<h4>Title</h4>
<ul>
 <li>ett.</li>
 <li>två.</li>
</ul>""",
            convertDashListToList("<h4>Title</h4><p>1. ett.</p><p>2. två.</p>")
        )
        assertEquals(
            """
            <h4>Kultur och samhälle</h4>
            <p>
             <ul>
              <li>Lekar och sånger från områden där modersmålet talas.</li>
              <li>Högtider och traditioner som eleven möter i olika sammanhang.</li>
             </ul></p>""".trimIndent(),
            convertDashListToList("<h4>Kultur och samhälle</h4><p>•Lekar och sånger från områden där modersmålet talas.<br />•Högtider och traditioner som eleven möter i olika sammanhang.</p>")
        )
        assertEquals(
            """
            <p>Kultur och samhälle
             <ul>
              <li>Lekar och sånger från områden där modersmålet talas.</li>
              <li>Högtider och traditioner som eleven möter i olika sammanhang.</li>
             </ul></p>""".trimIndent(),
            convertDashListToList("<p>Kultur och samhälle<br />•Lekar och sånger från områden där modersmålet talas.<br />•Högtider och traditioner som eleven möter i olika sammanhang.</p>")
        )
    }

    @Test
    fun removeBoldWords() {
        assertEquals(
            "Eleven förhåller sig konstnärligt till rörelsevokabulär samt varierar och utvecklar rörelseuttryck utifrån ",
            removeBoldWords("Eleven förhåller sig <strong>med viss säkerhet </strong>konstnärligt till rörelsevokabulär samt varierar och utvecklar rörelseuttryck utifrån <strong>instruktioner</strong>")
        )
        assertEquals(
            "Eleven förhåller sig konstnärligt till rörelsevokabulär och varierar, och utvecklar rörelseuttryck efter .",
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
    fun toPurposesTest() {
        assertEquals(
            listOf(
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Undervisningen i ämnet biologi ska syfta till att eleverna utvecklar kunskaper om biologins begrepp, teorier, modeller och arbetsmetoder.",
                        "Den ska bidra till att eleverna utvecklar förståelse av biologins betydelse i samhället, till exempel för livskvalitet och hälsa genom medicinen, och för skyddandet av jordens ekosystem genom ekologin.",
                        "Genom undervisningen ska eleverna ges möjlighet att utveckla ett naturvetenskapligt perspektiv på vår omvärld med evolutionsteorin som grund.",
                        "I undervisningen ska aktuell forskning och elevernas upplevelser, nyfikenhet och kreativitet tas till vara.",
                        "Undervisningen ska också bidra till att eleverna, från en naturvetenskaplig utgångspunkt, kan delta i samhällsdebatten och diskutera etiska frågor och ställningstaganden."
                    )
                ),
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Molekylärbiologin, liksom många andra områden inom biologin, utvecklas i snabb takt.",
                        "Utvecklingen sker i ett samspel mellan teori och experiment, där hypoteser, teorier och modeller testas, omvärderas och förändras.",
                        "Undervisningen ska därför behandla teoriers och modellers utveckling, begränsningar och giltighetsområden.",
                        "Den ska bidra till att eleverna utvecklar förmåga att arbeta teoretiskt och experimentellt samt att kommunicera med hjälp av ett naturvetenskapligt språk.",
                        "Undervisningen ska också bidra till att eleverna utvecklar förmåga att kritiskt värdera och skilja mellan påståenden som bygger på vetenskaplig respektive icke-vetenskaplig grund."
                    )
                ),
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Undervisningen ska innefatta naturvetenskapliga arbetsmetoder som att formulera och söka svar på frågor, göra systematiska observationer, planera och utföra experiment och fältstudier samt bearbeta, tolka och kritiskt granska resultat och information.",
                        "I undervisningen ska eleverna ges tillfällen att argumentera kring och presentera analyser och slutsatser.",
                        "De ska även ges möjlighet att använda datorstödd utrustning för insamling, simulering, beräkning, bearbetning och presentation av data."
                    )
                ),
                Purpose(
                    PurposeType.BULLET,
                    "Undervisningen i ämnet biologi ska ge eleverna förutsättningar att utveckla följande:",
                    listOf(
                        "Kunskaper om biologins begrepp, modeller, teorier och arbetsmetoder samt förståelse av hur dessa utvecklas.",
                        "Förmåga att analysera och söka svar på ämnesrelaterade frågor samt att identifiera, formulera och lösa problem. Förmåga att reflektera över och värdera valda strategier, metoder och resultat.",
                        "Förmåga att planera, genomföra, tolka och redovisa fältstudier, experiment och observationer samt förmåga att hantera material och utrustning.",
                        "Kunskaper om biologins betydelse för individ och samhälle.",
                        "Förmåga att använda kunskaper i biologi för att kommunicera samt för att granska och använda information."
                    )
                )
            ),
            toPurposes(
                "<p>Undervisningen i ämnet biologi ska syfta till att eleverna utvecklar kunskaper om biologins begrepp, teorier, modeller och arbetsmetoder. Den ska bidra till att eleverna utvecklar förståelse av biologins betydelse i samhället, till exempel för livskvalitet och hälsa genom medicinen, och för skyddandet av jordens ekosystem genom ekologin. Genom undervisningen ska eleverna ges möjlighet att utveckla ett naturvetenskapligt perspektiv på vår omvärld med evolutionsteorin som grund. I undervisningen ska aktuell forskning och elevernas upplevelser, nyfikenhet och kreativitet tas till vara. Undervisningen ska också bidra till att eleverna, från en naturvetenskaplig utgångspunkt, kan delta i samhällsdebatten och diskutera etiska frågor och ställningstaganden.</p><p>Molekylärbiologin, liksom många andra områden inom biologin, utvecklas i snabb takt. Utvecklingen sker i ett samspel mellan teori och experiment, där hypoteser, teorier och modeller testas, omvärderas och förändras. Undervisningen ska därför behandla teoriers och modellers utveckling, begränsningar och giltighetsområden. Den ska bidra till att eleverna utvecklar förmåga att arbeta teoretiskt och experimentellt samt att kommunicera med hjälp av ett naturvetenskapligt språk. Undervisningen ska också bidra till att eleverna utvecklar förmåga att kritiskt värdera och skilja mellan påståenden som bygger på vetenskaplig respektive icke-vetenskaplig grund.</p><p>Undervisningen ska innefatta naturvetenskapliga arbetsmetoder som att formulera och söka svar på frågor, göra systematiska observationer, planera och utföra experiment och fältstudier samt bearbeta, tolka och kritiskt granska resultat och information. I undervisningen ska eleverna ges tillfällen att argumentera kring och presentera analyser och slutsatser. De ska även ges möjlighet att använda datorstödd utrustning för insamling, simulering, beräkning, bearbetning och presentation av data.</p><h4>Undervisningen i ämnet biologi ska ge eleverna förutsättningar att utveckla följande:</h4><p> </p><ol> <li>Kunskaper om biologins begrepp, modeller, teorier och arbetsmetoder samt förståelse av hur dessa utvecklas.</li> <li>Förmåga att analysera och söka svar på ämnesrelaterade frågor samt att identifiera, formulera och lösa problem. Förmåga att reflektera över och värdera valda strategier, metoder och resultat.</li> <li>Förmåga att planera, genomföra, tolka och redovisa<br/>fältstudier, experiment och observationer samt förmåga att hantera material och utrustning.</li> <li>Kunskaper om biologins betydelse för individ och samhälle.</li> <li>Förmåga att använda kunskaper i biologi för att kommunicera samt för att granska och använda information.</li> </ol><p></p><h3>Kurser i ämnet</h3><p></p><ol> <li>Biologi 1, 100 poäng, som bygger på grundskolans kunskaper eller motsvarande.</li> <li>Biologi 2, 100 poäng, som bygger på kursen biologi 1.</li> <li>Bioteknik, 100 poäng, som bygger på kursen biologi 1.</li> </ol><p></p>",
                SchoolType.GR,
                "BiologyS"
            )
        )
        assertEquals(
            listOf(
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Undervisningen i ämnet bild ska syfta till att eleverna utvecklar kunskaper om hur bilder skapas och kan tolkas.",
                        "Genom undervisningen ska eleverna få erfarenheter av visuell kultur där film, foto, design, konst, arkitektur och miljöer ingår."
                    )
                ),
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "I undervisningen ska eleverna ges möjligheter att utveckla kunskaper om hur man framställer och presenterar egna bilder med olika metoder, material och uttrycksformer.",
                        "Undervisningen ska bidra till att eleverna utvecklar sin kreativitet och sitt intresse för att skapa.",
                        "Den ska också uppmuntra eleverna att ta egna initiativ och att arbeta på ett undersökande och problemlösande sätt."
                    )
                ),
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Undervisningen ska bidra till att eleverna utvecklar förståelse för hur bildbudskap utformas i olika medier.",
                        "Undervisningen ska också ge eleverna möjligheter att diskutera och kritiskt granska olika bildbudskap och bidra till att eleverna utvecklar kunskaper om bilder i olika kulturer, både historiskt och i nutid.",
                        "Genom undervisningen ska eleverna även ges möjlighet att använda sina kunskaper om olika typer av bilder i det egna bildskapandet."
                    )
                ),
                Purpose(
                    PurposeType.BULLET,
                    "Genom undervisningen i ämnet bild ska eleverna sammanfattningsvis ges förutsättningar att utveckla sin förmåga att",
                    listOf(
                        "kommunicera med bilder för att uttrycka budskap,",
                        "skapa bilder med digitala och hantverksmässiga tekniker och verktyg samt med olika material,",
                        "undersöka och presentera olika ämnesområden med bilder, och",
                        "analysera historiska och samtida bilders uttryck, innehåll och funktioner."
                    )
                )
            ),
            toPurposes(
                "<p>Undervisningen i ämnet bild ska syfta till att eleverna utvecklar kunskaper om hur bilder skapas och kan tolkas. Genom undervisningen ska eleverna få erfarenheter av visuell kultur där film, foto, design, konst, arkitektur och miljöer ingår.<br/>I undervisningen ska eleverna ges möjligheter att utveckla kunskaper om hur man framställer och presenterar egna bilder med olika metoder, material och uttrycksformer. Undervisningen ska bidra till att eleverna utvecklar sin kreativitet och sitt intresse för att skapa. Den ska också uppmuntra eleverna att ta egna initiativ och att arbeta på ett undersökande och problemlösande sätt.<br/>Undervisningen ska bidra till att eleverna utvecklar förståelse för hur bildbudskap utformas i olika medier. Undervisningen ska också ge eleverna möjligheter att diskutera och kritiskt granska olika bildbudskap och bidra till att eleverna utvecklar kunskaper om bilder i olika kulturer, både historiskt och i nutid. Genom undervisningen ska eleverna även ges möjlighet att använda sina kunskaper om olika typer av bilder i det egna bildskapandet.<br/>Genom undervisningen i ämnet bild ska eleverna sammanfattningsvis ges förutsättningar att utveckla sin förmåga att </p><ul> <li>kommunicera med bilder för att uttrycka budskap,</li> <li>skapa bilder med digitala och hantverksmässiga tekniker och verktyg samt med olika material,</li> <li>undersöka och presentera olika ämnesområden med bilder, och</li> <li>analysera historiska och samtida bilders uttryck, innehåll och funktioner.</li> </ul><p></p>",
                SchoolType.GR, "Test"
            )
        )
        assertEquals(
            listOf(
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Undervisningen i ämnet matematik ska syfta till att eleverna utvecklar kunskaper om matematik och matematikens användning i vardagen.",
                        "Den ska bidra till att eleverna utvecklar intresse för matematik och en tilltro till sin egen förmåga att använda matematik i olika sammanhang."
                    )
                ),
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Vidare ska undervisningen ge eleverna möjlighet att utveckla kunskaper om grundläggande matematiska metoder och hur dessa kan användas för att besvara frågor i vardagliga situationer.",
                        "Undervisningen ska också bidra till att eleverna får uppleva matematiken som en estetisk och kreativ aktivitet som kan användas vid problemlösning och matematiska undersökningar."
                    )
                ),
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Undervisningen ska bidra till att eleverna utvecklar ett kritiskt förhållningssätt i situationer där det finns behov av att göra överväganden om matematisk rimlighet.",
                        "Eleverna ska genom undervisningen ges möjligheter att utveckla kunskaper i att använda digital teknik för att undersöka problemställningar, göra beräkningar och för att presentera och tolka resultat."
                    )
                ),
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Vidare ska undervisningen i matematik bidra till att eleverna utvecklar kunskaper om ämnesspecifika begrepp.",
                        "På så sätt ska eleverna ges förutsättningar att samtala om matematik och presentera och utvärdera arbetsprocesser."
                    )
                ),
                Purpose(
                    PurposeType.BULLET,
                    "Genom undervisningen i ämnet matematik ska eleverna sammanfattningsvis ges förutsättningar att utveckla sin förmåga att",
                    listOf(
                        "lösa matematiska problem,",
                        "använda matematiska metoder för att göra beräkningar och lösa rutinuppgifter,",
                        "reflektera över rimlighet i situationer med matematisk anknytning, och",
                        "använda ämnesspecifika ord, begrepp och symboler."
                    )
                )
            ),
            toPurposes(
                "<p>Undervisningen i ämnet matematik ska syfta till att eleverna utvecklar kunskaper om matematik och matematikens användning i vardagen. Den ska bidra till att eleverna utvecklar intresse för matematik och en tilltro till sin egen förmåga att använda matematik i olika sammanhang.</p><p>Vidare ska undervisningen ge eleverna möjlighet att utveckla kunskaper om grundläggande matematiska metoder och hur dessa kan användas för att besvara frågor i vardagliga situationer. Undervisningen ska också bidra till att eleverna får uppleva matematiken som en estetisk och kreativ aktivitet som kan användas vid problemlösning och matematiska undersökningar.</p><p>Undervisningen ska bidra till att eleverna utvecklar ett kritiskt förhållningssätt i situationer där det finns behov av att göra överväganden om matematisk rimlighet. Eleverna ska genom undervisningen ges möjligheter att utveckla kunskaper i att använda digital teknik för att undersöka problemställningar, göra beräkningar och för att presentera och tolka resultat.</p><p>Vidare ska undervisningen i matematik bidra till att eleverna utvecklar kunskaper om ämnesspecifika begrepp. På så sätt ska eleverna ges förutsättningar att samtala om matematik och presentera och utvärdera arbetsprocesser.</p><p>Genom undervisningen i ämnet matematik ska eleverna sammanfattningsvis ges förutsättningar att utveckla sin förmåga att<br/></p><ul> <li>lösa matematiska problem,</li> <li>använda matematiska metoder för att göra beräkningar och lösa rutinuppgifter,</li> <li>reflektera över rimlighet i situationer med matematisk anknytning, och</li> <li>använda ämnesspecifika ord, begrepp och symboler.</li> </ul><p></p>",
                SchoolType.GR, "Test"
            )
        )
        assertEquals(
            listOf(
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Undervisningen i ämnet processteknik – kemi ska syfta till att eleverna utvecklar kunskaper om hur man identifierar och löser vanliga processtekniska problem genom att analysera tillgänglig information, använda olika metoder och lösningsstrategier samt implementera dessa."
                    )
                ),
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Undervisningen ska leda till att eleverna utvecklar kunskaper om processtekniska system i kemisktekniska anläggningar samt om deras komponenter och metoder.",
                        "Dessutom ska undervisningen leda till att eleverna utvecklar kunskaper i utförande av vanligt förekommande arbetsuppgifter inom kemitekniska processer och förmåga att utvärdera resultatet."
                    )
                ),
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Undervisningen ska bidra till att eleverna utvecklar förmåga att arbeta systematiskt och följa standarder och säkerhetsföreskrifter samt tar hänsyn till ställda kvalitetskrav.",
                        "Vidare ska eleverna ges möjlighet att utveckla förmåga att arbeta med produktionsprocesser och med hållbarhet i olika dimensioner."
                    )
                ),
                Purpose(
                    PurposeType.BULLET,
                    "Undervisningen i ämnet processteknik – kemi ska ge eleverna förutsättningar att utveckla följande:",
                    listOf(
                        "Kunskaper om processtekniska system, deras komponenter och elementära processer samt förmåga att koppla samman dessa till kretsprocesser samt hur digital teknik kan användas i arbetet.",
                        "Kunskaper om säkerhetsfilosofier och säkerhetsföreskrifter inom det processtekniska området.",
                        "Kunskaper om kvalitetsarbete i produktionsprocesser samt kunskaper om hur provtagningar kan utformas och genomföras för att mäta och dokumentera kvalitet.",
                        "Förståelse av hur kemisk produktion påverkar miljön och hur miljöregleringen påverkar produktionen samt kunskaper om gällande lagstiftning.",
                        "Förmåga att genomföra, tolka och redovisa provtagningar och observationer av fysikaliska storheter och kemiska egenskaper samt förmåga att hantera kemikalier och utrustning.",
                        "Förmåga att reglera och optimera processer inom kemisk produktion.",
                        "Förmåga att planera sitt arbete och att övervaka, styra och reglera utrustning samt utföra vanliga arbetsuppgifter inom det processtekniska området.",
                        "Förmåga att tolka och upprätta processteknisk dokumentation."
                    )
                )
            ),
            toPurposes(
                """
        <div> 
            <p>
                Undervisningen i ämnet processteknik – kemi ska syfta till att eleverna utvecklar kunskaper om hur man identifierar och löser vanliga processtekniska problem genom att analysera tillgänglig information, använda olika metoder och lösningsstrategier samt implementera dessa.
            </p> 
            <div>
                <p>
                    Undervisningen ska leda till att eleverna utvecklar kunskaper om processtekniska system i kemisktekniska anläggningar samt om deras komponenter och metoder. 
                    Dessutom ska undervisningen leda till att eleverna utvecklar kunskaper i utförande av vanligt förekommande arbetsuppgifter inom kemitekniska processer och förmåga att utvärdera resultatet.
                 </p>
                <p>
                    Undervisningen ska bidra till att eleverna utvecklar förmåga att arbeta systematiskt och följa standarder och säkerhetsföreskrifter samt tar hänsyn till ställda kvalitetskrav. Vidare ska eleverna ges möjlighet att utveckla förmåga att arbeta med produktionsprocesser och med hållbarhet i olika dimensioner.
                </p> 
            </div>
        </div> 
        <h4>Undervisningen i ämnet processteknik – kemi ska ge eleverna förutsättningar att utveckla följande:</h4> 
        <ol> 
            <li>Kunskaper om processtekniska system, deras komponenter och elementära processer samt förmåga att koppla samman dessa till kretsprocesser samt hur digital teknik kan användas i arbetet.</li>
            <li>Kunskaper om säkerhetsfilosofier och säkerhetsföreskrifter inom det processtekniska området.</li> 
            <li>Kunskaper om kvalitetsarbete i produktionsprocesser samt kunskaper om hur provtagningar kan utformas och genomföras för att mäta och dokumentera kvalitet.</li> 
            <li>Förståelse av hur kemisk produktion påverkar miljön och hur miljöregleringen påverkar produktionen samt kunskaper om gällande lagstiftning.</li> 
            <li>Förmåga att genomföra, tolka och redovisa provtagningar och observationer av fysikaliska storheter och kemiska egenskaper samt förmåga att hantera kemikalier och utrustning.</li> 
            <li>Förmåga att reglera och optimera processer inom kemisk produktion.</li> <li>Förmåga att planera sitt arbete och att övervaka, styra och reglera utrustning samt utföra vanliga arbetsuppgifter inom det processtekniska området.</li> 
            <li>Förmåga att tolka och upprätta processteknisk dokumentation.</li> 
        </ol> 
        <h3>Kurser i ämnet</h3>
        <ul>
            <li>Processteknik – kemi 1, 100 poäng. </li> 
            <li>Processteknik – kemi 2, 100 poäng, som bygger på kursen processteknik – kemi 1.</li>
        </ul>
                    """.trimIndent(), SchoolType.GR, "Test"

            )
        )

        assertEquals(
            listOf(
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Undervisningen i ämnet religionskunskap ska syfta till att eleverna breddar, fördjupar och utvecklar kunskaper om religioner, livsåskådningar och etiska förhållningssätt och olika tolkningar när det gäller dessa.",
                        "Kunskaper om samt förståelse för kristendomen och dess traditioner har särskild betydelse då denna tradition förvaltat den värdegrund som ligger till grund för det svenska samhället.",
                        "Undervisningen ska ta sin utgångspunkt i en samhällssyn som präglas av öppenhet i fråga om livsstilar, livshållningar och människors olikheter samt ge eleverna möjlighet att utveckla en beredskap att förstå och leva i ett samhälle präglat av mångfald.",
                        "Eleverna ska ges möjlighet att diskutera hur relationen mellan religion och vetenskap kan tolkas och uppfattas, till exempel beträffande frågor om skapelse och evolution."
                    )
                ),
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Undervisningen ska leda till att eleverna utvecklar kunskaper om hur människors moraliska förhållningssätt kan motiveras utifrån religioner och livsåskådningar.",
                        "De ska ges möjlighet att reflektera över och analysera människors värderingar och trosföreställningar och därigenom utveckla respekt och förståelse för olika sätt att tänka och leva.",
                        "Undervisningen ska också ge eleverna möjlighet att analysera och värdera hur religion kan förhålla sig till bland annat etnicitet, kön, sexualitet och socioekonomisk bakgrund."
                    )
                ),
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "I undervisningen ska eleverna ges möjlighet att analysera texter och begrepp, kritiskt granska källor i digital och annan form, diskutera och argumentera."
                    )
                ),
                Purpose(
                    PurposeType.BULLET,
                    "Undervisningen i ämnet religionskunskap ska ge eleverna förutsättningar att utveckla följande:",
                    listOf(
                        "Förmåga att analysera religioner och livsåskådningar utifrån olika tolkningar och perspektiv.",
                        "Kunskaper om människors identitet i relation till religioner och livsåskådningar.",
                        "Kunskaper om olika uppfattningar om relationen mellan religion och vetenskap samt förmåga att analysera dessa.",
                        "Förmåga att använda etiska begrepp, teorier och modeller.",
                        "Förmåga att undersöka och analysera etiska frågor i relation till kristendomen, andra religioner och livsåskådningar."
                    )
                )
            ),
            toPurposes(
                """
        <p>Undervisningen i ämnet religionskunskap ska syfta till att eleverna breddar, fördjupar och utvecklar kunskaper om religioner, livsåskådningar och etiska förhållningssätt och olika tolkningar när det gäller dessa. Kunskaper om samt förståelse för kristendomen och dess traditioner har särskild betydelse då denna tradition förvaltat den värdegrund som ligger till grund för det svenska samhället. Undervisningen ska ta sin utgångspunkt i en samhällssyn som präglas av öppenhet i fråga om livsstilar, livshållningar och människors olikheter samt ge eleverna möjlighet att utveckla en beredskap att förstå och leva i ett samhälle präglat av mångfald. Eleverna ska ges möjlighet att diskutera hur relationen mellan religion och vetenskap kan tolkas och uppfattas, till exempel beträffande frågor om skapelse och evolution.</p>
        <p>Undervisningen ska leda till att eleverna utvecklar kunskaper om hur människors moraliska förhållningssätt kan motiveras utifrån religioner och livsåskådningar. De ska ges möjlighet att reflektera över och analysera människors värderingar och trosföreställningar och därigenom utveckla respekt och förståelse för olika sätt att tänka och leva. Undervisningen ska också ge eleverna möjlighet att analysera och värdera hur religion kan förhålla sig till bland annat etnicitet, kön, sexualitet och socioekonomisk bakgrund.</p>
        <p>I undervisningen ska eleverna ges möjlighet att analysera texter och begrepp, kritiskt granska källor i digital och annan form, diskutera och argumentera.</p>
        <p>Undervisningen i ämnet religionskunskap ska ge eleverna förutsättningar att utveckla följande:</p>
        <ol>
            <li>Förmåga att analysera religioner och livsåskådningar utifrån olika tolkningar och perspektiv.</li>
            <li>Kunskaper om människors identitet i relation till religioner och livsåskådningar.</li>
            <li>Kunskaper om olika uppfattningar om relationen mellan religion och vetenskap samt förmåga att analysera dessa.</li>
            <li>Förmåga att använda etiska begrepp, teorier och modeller.</li>
            <li>Förmåga att undersöka och analysera etiska frågor i relation till kristendomen, andra religioner och livsåskådningar. </li>
        </ol>
        <h3>Kurser i ämnet</h3>
        <ul>
            <li>Religionskunskap 1, 50 poäng, som bygger på de kunskaper grundskolan ger eller motsvarande.</li>
            <li>Religionskunskap 2, 50 poäng, som bygger på kursen religionskunskap 1.</li>
            <li>Religionskunskap – specialisering, 100 poäng, som bygger på kursen religionskunskap 1.</li>
        </ul>
                    """.trimIndent(), SchoolType.GY, "Religionskunskap"

            )
        )


        assertEquals(
            listOf(

                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Undervisningen i ämnet språk specialisering ska syfta till att eleverna fördjupar sin förmåga att använda målspråket inom ett färdighetsområde.",
                        "I undervisningen ska eleverna ges möjlighet att utveckla språklig säkerhet och kreativitet i olika genrer och sammanhang inom färdighetsområdet.",
                        "Eleverna ska vidare ges möjlighet att utveckla förmåga att förstå talat och skrivet språk, interagera samt uttrycka sig med variation och med anpassning till mottagarens erfarenheter, referensramar och kulturella bakgrund.",
                        "Undervisningen i ämnet ska även syfta till att eleverna utvecklar kunskaper om målspråket och om sammanhang där språket används samt tilltro till sin förmåga att använda språket.",
                        "Undervisningen ska i allt väsentligt bedrivas på målspråket."
                    )
                ),
                Purpose(
                    PurposeType.PARAGRAPH, "", listOf(
                        "Undervisningen ska också leda till att eleverna utvecklar förmåga att använda relevanta begrepp, metoder och arbetsprocesser inom området.",
                        "Eleverna ska ges möjlighet att utveckla förmåga att söka och värdera information och olika budskap, att diskutera och beakta frågor om etik, upphovsrätt och källkritik samt att resonera kring och presentera resultat.",
                        "Eleverna ska ges möjlighet att utveckla förmåga att konstruktivt ge respons på andras arbetsprocesser och framställningar och att, efter egen reflektion och andras råd, bearbeta och förbättra sina egna processer och framställningar.",
                        "I undervisningen ska eleverna dessutom ges möjlighet att utveckla förmågan att använda informations- och kommunikationsteknik inom området."
                    )
                ),
                Purpose(
                    PurposeType.BULLET, "Undervisningen i ämnet språk specialisering ska ge eleverna förutsättningar att utveckla följande:", listOf(
                        "Förmåga att använda språket utifrån kunskaper om genre, språkriktighet, stilnivå, stilistiska drag och kulturella sammanhang.",
                        "Förmåga att arbeta processinriktat inom området, dvs. att på ett strukturerat och metodiskt sätt planera, bearbeta, presentera och utvärdera framställningar.",
                        "Förmåga att tillgodogöra sig, reflektera över och kritiskt granska processer och framställningar samt förbättra egna.",
                        "Förmåga att använda informations- och kommunikationsteknik samt att reflektera över och diskutera etiska frågor, upphovsrätt och källkritik."
                    )
                )
            ),
            toPurposes(
                """
            <p>Undervisningen i ämnet språk specialisering ska syfta till att eleverna fördjupar sin förmåga att använda målspråket inom ett färdighetsområde. I undervisningen ska eleverna ges möjlighet att utveckla språklig säkerhet och kreativitet i olika genrer och sammanhang inom färdighetsområdet. Eleverna ska vidare ges möjlighet att utveckla förmåga att förstå talat och skrivet språk, interagera samt uttrycka sig med variation och med anpassning till mottagarens erfarenheter, referensramar och kulturella bakgrund. Undervisningen i ämnet ska även syfta till att eleverna utvecklar kunskaper om målspråket och om sammanhang där språket används samt tilltro till sin förmåga att använda språket. Undervisningen ska i allt väsentligt bedrivas på målspråket.</p>
            <p>Undervisningen ska också leda till att eleverna utvecklar förmåga att använda relevanta begrepp, metoder och arbetsprocesser inom området. Eleverna ska ges möjlighet att utveckla förmåga att söka och värdera information och olika budskap, att diskutera och beakta frågor om etik, upphovsrätt och källkritik samt att resonera kring och presentera resultat. Eleverna ska ges möjlighet att utveckla förmåga att konstruktivt ge respons på andras arbetsprocesser och framställningar och att, efter egen reflektion och andras råd, bearbeta och förbättra sina egna processer och framställningar. I undervisningen ska eleverna dessutom ges möjlighet att utveckla förmågan att använda informations- och kommunikationsteknik inom området. </p>
            <ol>
                <li>Förmåga att använda språket utifrån kunskaper om genre, språkriktighet, stilnivå, stilistiska drag och kulturella sammanhang.</li>
                <li>Förmåga att arbeta processinriktat inom området, dvs. att på ett strukturerat och metodiskt sätt planera, bearbeta, presentera och utvärdera framställningar.</li>
                <li>Förmåga att tillgodogöra sig, reflektera över och kritiskt granska processer och framställningar samt förbättra egna.</li>
                <li>Förmåga att använda informations- och kommunikationsteknik samt att reflektera över och diskutera etiska frågor, upphovsrätt och källkritik. </li>
            </ol>
            <h3>Kurser i ämnet</h3>
            <ol>
                <li>Språk specialisering – retorik 1a, 50 poäng, som bygger på någon av kurserna engelska 5, moderna språk 5 eller modersmål 1. Kursen kan läsas flera gånger i olika språk. I ett och samma språk kan endast betyg i en av kurserna språk specialisering – retorik 1a eller språk specialisering – retorik 1b ingå i elevens examen.</li>
                <li>Språk specialisering – retorik 1b, 100 poäng, som bygger på någon av kurserna engelska 5, moderna språk 5 eller modersmål 1. Kursen kan läsas flera gånger i olika språk. I ett och samma språk kan endast betyg i en av kurserna språk specialisering – retorik 1b eller språk specialisering – retorik 1a ingå i elevens examen.</li>
                <li>Språk specialisering – skrivande, 100 poäng, som bygger på någon av kurserna engelska 5, moderna språk 5 eller modersmål 1. Kursen kan läsas flera gånger i olika språk. </li>
            </ol>
                    """.trimIndent(), SchoolType.GY, "Språk specialisering"

            )
        )


    }
}
