package org.edtech.curriculum.internal

import org.edtech.curriculum.Purpose
import org.edtech.curriculum.PurposeType
import org.junit.Assert
import org.junit.jupiter.api.Test

class SubjectParserTest {

    @Test
    fun testNormalizePurposes() {
        Assert.assertArrayEquals(
                arrayOf(
                        Purpose(PurposeType.PARAGRAPH, "", listOf("bla bla.", "bla bla.")),
                        Purpose(PurposeType.PARAGRAPH, "Heading", listOf("bla bla.", "bla bla.")),
                        Purpose(PurposeType.PARAGRAPH, "", listOf("bla bla.", "bla bla.")),
                        Purpose(PurposeType.BULLET, "Heading", listOf("bla bla.", "bla bla.", "bla bla.", "bla bla.", "bla bla.")),
                        Purpose(PurposeType.PARAGRAPH, "", listOf( "bla bla.", "bla bla."))
                ),
                SubjectParser().normalizePurposes(
                    listOf(
                            Purpose(PurposeType.PARAGRAPH, "", listOf("bla bla.", "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "Heading", listOf("bla bla.", "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "", listOf("bla bla.", "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "Heading", listOf("1.", "bla bla.",  "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "", listOf("2.", "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "", listOf("3.", "bla bla.", "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "", listOf( "bla bla.", "bla bla."))
                    )
                ).toTypedArray()
        )
        Assert.assertArrayEquals(
                arrayOf(
                        Purpose(PurposeType.PARAGRAPH, "", listOf("bla bla.", "bla bla.")),
                        Purpose(PurposeType.PARAGRAPH, "Heading", listOf("bla bla.", "bla bla.")),
                        Purpose(PurposeType.PARAGRAPH, "", listOf("bla bla.", "bla bla.")),
                        Purpose(PurposeType.BULLET, "Heading", listOf("bla bla.", "bla bla.", "bla bla.")),
                        Purpose(PurposeType.BULLET, "Heading", listOf("bla bla.", "bla bla."))
                ),
                SubjectParser().normalizePurposes(
                    listOf(
                            Purpose(PurposeType.PARAGRAPH, "", listOf("bla bla.", "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "Heading", listOf("bla bla.", "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "", listOf("bla bla.", "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "Heading", listOf("1.", "bla bla.",  "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "", listOf("2.", "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "Heading", listOf("1.", "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "", listOf("2.", "bla bla."))
                    )
                ).toTypedArray()
        )
        Assert.assertArrayEquals(
                arrayOf(
                        Purpose(PurposeType.PARAGRAPH, "", listOf("bla bla.", "bla bla.")),
                        Purpose(PurposeType.PARAGRAPH, "Heading", listOf("bla bla.", "bla bla.")),
                        Purpose(PurposeType.PARAGRAPH, "", listOf("bla bla.", "bla bla.")),
                        Purpose(PurposeType.BULLET, "Heading", listOf("bla bla.", "bla bla.", "bla bla.")),
                        Purpose(PurposeType.BULLET, "Heading", listOf("bla bla."))
                ),
                SubjectParser().normalizePurposes(
                    listOf(
                            Purpose(PurposeType.PARAGRAPH, "", listOf("bla bla.", "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "Heading", listOf("bla bla.", "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "", listOf("bla bla.", "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "Heading", listOf("1.", "bla bla.",  "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "", listOf("2.", "bla bla.")),
                            Purpose(PurposeType.PARAGRAPH, "Heading", listOf("1.", "bla bla."))
                    )
                ).toTypedArray()
        )
    }
}