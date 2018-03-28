package org.edtech.curriculum

import java.io.*
import java.net.URL

/**
 * A file at http://opendata.skolverket.se/data/ containing subject and course information.
 */
enum class SyllabusType(val filename: String, val schoolType: String? = null) {
    /** https://www.skolverket.se/laroplaner-amnen-och-kurser/gymnasieutbildning/...
     * The same file is used for thiese
     */
    GR("compulsory", "grundskolan"),
    GRSAM("compulsory", "sameskolan"),
    GRS("compulsory", "sarskolan"),
    GRSPEC("compulsory", "specialskolan"),

    /** https://www.skolverket.se/laroplaner-amnen-och-kurser/gymnasieutbildning/gymnasieskola */
    GY("syllabus"),
    /** https://www.skolverket.se/laroplaner-amnen-och-kurser/gymnasieutbildning/... */
    GYS("gys"),
    /** https://www.skolverket.se/laroplaner-amnen-och-kurser/vuxenutbildning/komvux/grundlaggande */
    VUXGR("vuxgr"),
    /** https://www.skolverket.se/laroplaner-amnen-och-kurser/vuxenutbildning/komvux/sfi */
    SFI("sfi");


    private fun getDownloadFileStream(): InputStream {
        val urlToDownload = URL("http://opendata.skolverket.se/data/$filename.tgz")
        val connection = urlToDownload.openConnection()
        return connection.getInputStream()
    }

    private fun getLocallyCachedFile(cacheDir: File): File {
        if (!cacheDir.isDirectory) {
            if (!cacheDir.mkdir()) {
                System.err.println("ERROR: Unable to create cache dir: " + cacheDir.absolutePath)
                System.exit(1)
            }
        }

        val currentFile = File(cacheDir, "$filename.tgz")
        if (currentFile.isFile) return currentFile

        val tmpFile = File(currentFile.absolutePath + ".download")
        val inStream = getDownloadFileStream()
        var bytesRead = -1
        val bytes = ByteArray(4096)
        FileOutputStream(tmpFile).use {
            while ({ bytesRead = inStream.read(bytes); bytesRead }() != -1) {
                it.write(bytes, 0, bytesRead)
            }
        }

        tmpFile.renameTo(currentFile)

        return currentFile
    }

    fun getFileArchive(cacheDir: File = File(System.getProperty("java.io.tmpdir"))): SkolverketFileArchive {
        return SkolverketFileArchive(getLocallyCachedFile(cacheDir))
    }
}