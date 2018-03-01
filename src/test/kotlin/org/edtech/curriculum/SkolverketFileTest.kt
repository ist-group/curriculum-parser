package org.edtech.curriculum

import org.junit.Assert
import org.junit.Test

class SkolverketFileTest {

    @Test
    fun testGY() {
        val subjectNames = SkolverketFile.GY.subjectNames()
        Assert.assertEquals(286, subjectNames.size)
        Assert.assertEquals("ATV- och MC-teknik", subjectNames[0])
        Assert.assertEquals("Administration", subjectNames[1])
        Assert.assertEquals("Information och kommunikation", subjectNames[124])
        Assert.assertEquals("Yttre miljo", subjectNames.last())
    }

    @Test
    fun testVUXGR() {
        val subjectNames = SkolverketFile.VUXGR.subjectNames()
        Assert.assertEquals(24, subjectNames.size)
        Assert.assertEquals(listOf(
                "Biologi", "Engelska", "Fysik", "Geografi", "Hem- och konsumentkunskap", "Historia", "Kemi",
                "Matematik", "Matematik Nationell delkurs 1", "Matematik Nationell delkurs 2",
                "Matematik Nationell delkurs 3", "Matematik Nationell delkurs 4", "Religionskunskap",
                "Samhallskunskap", "Svenska",
                "Svenska Nationell delkurs 1", "Svenska Nationell delkurs 2",
                "Svenska Nationell delkurs 3", "Svenska Nationell delkurs 4",
                "Svenska som andrasprak",
                "Svenska som andrasprak Nationell delkurs 1", "Svenska som andrasprak Nationell delkurs 2",
                "Svenska som andrasprak Nationell delkurs 3", "Svenska som andrasprak Nationell delkurs 4"),
                subjectNames
        )
    }

}