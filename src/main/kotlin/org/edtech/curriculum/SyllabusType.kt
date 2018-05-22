package org.edtech.curriculum

import java.io.*
import java.net.URL

/**
 * A file at http://opendata.skolverket.se/data/ containing subject and course information.
 */
enum class SyllabusType(val filename: String, val archivePath: String) {
    /** https://www.skolverket.se/laroplaner-amnen-och-kurser/gymnasieutbildning/...
     * The same file is used for thiese
     */
    GR("compulsory", "compulsory/subject-compulsory-S2_0/grundskolan/"),
    GRSAM("compulsory", "compulsory/subject-compulsory-S2_0/sameskolan/"),
    GRS("compulsory", "compulsory/subject-compulsory-S2_0/grundsarskolan/"),
    GRSPEC("compulsory", "compulsory/subject-compulsory-S2_0/specialskolan/"),

    /** https://www.skolverket.se/laroplaner-amnen-och-kurser/gymnasieutbildning/gymnasieskola */
    GY("syllabus", "gyP1_7_S1_4/subject/"),
    /** https://www.skolverket.se/laroplaner-amnen-och-kurser/gymnasieutbildning/... */
    GYS("gys", "gysP1_7_S1_4/subject/"),
    /** https://www.skolverket.se/laroplaner-amnen-och-kurser/vuxenutbildning/komvux/grundlaggande */
    VUXGR("vuxgr", "vuxgrP1_7_S1_4/subject/"),
    /** https://www.skolverket.se/laroplaner-amnen-och-kurser/vuxenutbildning/komvux/sfi */
    SFI("sfi", "sfiP1_7_S1_4/subject/");


    private fun getDownloadFileStream(): InputStream {
        val urlToDownload = URL("http://opendata.skolverket.se/data/$filename.tgz")
        val connection = urlToDownload.openConnection()
        return connection.getInputStream()
    }

    private fun getLocallyCachedFile(cacheDir: File, cache: Boolean): File {
        if (!cacheDir.isDirectory) {
            if (!cacheDir.mkdir()) {
                System.err.println("ERROR: Unable to create cache dir: " + cacheDir.absolutePath)
                System.exit(1)
            }
        }

        val currentFile = File(cacheDir, "$filename.tgz")
        if (currentFile.isFile && cache) {
            return currentFile
        }

        val tmpFile = File(currentFile.absolutePath + ".download")
        val inStream = getDownloadFileStream()
        var bytesRead = -1
        val bytes = ByteArray(4096)
        FileOutputStream(tmpFile).use {
            while ({ bytesRead = inStream.read(bytes); bytesRead }() != -1) {
                it.write(bytes, 0, bytesRead)
            }
        }
        // Remove the old version if exists
        if (currentFile.isFile) {
            currentFile.delete()
        }
        tmpFile.renameTo(currentFile)

        return currentFile
    }

    /**
     * Load the SkolverketFileArchive
     */
    fun getFileArchive(fileDir: File = File(System.getProperty("java.io.tmpdir")), cache: Boolean = true): SkolverketFileArchive {
        return SkolverketFileArchive(getLocallyCachedFile(fileDir, cache))
    }
}