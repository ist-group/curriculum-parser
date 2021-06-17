package org.edtech.curriculum

import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.*

class IndividualSubjectFileParserTest {
    private val file1Path = "./src/test/resources/individual/Engelska-Gy-Prod-DRAFT-20210212.txt"
    private val file2Path = "./src/test/resources/individual/Matematik-Gy-Prod-DRAFT-20210212.txt"
    private val file3Path = "./src/test/resources/individual/Moderna-språk-Gy-Prod-DRAFT-20210212.txt"

    @TestFactory
    fun testIndividualParser()= listOf(file1Path, file2Path, file3Path).map { filePath ->
        DynamicTest.dynamicTest(filePath) {
            loadIndividualSubjectsFile(filePath)
        }
    }
    @Throws(IOException::class)
    private fun loadIndividualSubjectsFile(filePath: String) {
        val s = SubjectFileReader(SchoolType.GY, convertAndOpenFile(filePath)!!).getSubjectFromFile()
        System.out.println(filePath + " = " + s.courses.size)

        assertEquals(expectedNumberOfCoursesInXMLFile(filePath),
                s.courses.size, " does not contain expected number of courses in file ")
        System.out.println(s.courses[0].knowledgeRequirementParagraphs);
        //System.out.println(s.courses.size)
    }

    @Throws(IOException::class)
    fun convertAndOpenFile(path: String): InputStream? {
        val f = FileInputStream(File(path))
        val openDataDocument = Jsoup.parse(f, null, "", Parser.xmlParser())
        val subj = openDataDocument.getElementsByTag("ns4:subjects")
        val entries = subj.first().allElements
        for (element in entries) {
            //System.out.println("Found element " + element.nodeName());
            var newName = element.nodeName().replace("ns3:", "")
            newName = newName.replace("ns4:subjects", "subject")
            element.tagName(newName)
        }
        val outfile = "$path.converted"
        val writer = BufferedWriter(FileWriter(outfile))
        writer.write(subj.toString())
        writer.close()
        f.close()
        return FileInputStream(File(outfile))
    }

    private fun expectedNumberOfCoursesInXMLFile(filePath: String): Int {
        return when (filePath) {
            file1Path -> 3
            file2Path -> 11
            file3Path -> 14
            else -> 0
        }
    }

}