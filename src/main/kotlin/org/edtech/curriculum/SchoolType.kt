package org.edtech.curriculum

import java.io.*
import java.net.URL

enum class SchoolType(val filename: String, val archivePath: String) {

    /** https://www.skolverket.se/undervisning/grundskolan/laroplan-och-kursplaner-for-grundskolan */
    GR("compulsory.tgz", "compulsory/subject-compulsory-S2_0/grundskolan/"),

    /** https://www.skolverket.se/undervisning/sameskolan/laroplan-och-kursplaner-i-sameskolan */
    GRSAM("compulsory.tgz", "compulsory/subject-compulsory-S2_0/sameskolan/"),

    /** https://www.skolverket.se/undervisning/grundsarskolan/laroplan-och-kursplaner-for-grundsarskolan */
    GRS("compulsory.tgz", "compulsory/subject-compulsory-S2_0/grundsarskolan/"),

    /** https://www.skolverket.se/undervisning/specialskolan/laroplan-och-kursplaner-i-specialskolan */
    GRSPEC("compulsory.tgz", "compulsory/subject-compulsory-S2_0/specialskolan/"),

    /** https://www.skolverket.se/undervisning/gymnasieskolan/laroplan-program-och-amnen-i-gymnasieskolan */
    GY("syllabus.tgz", "gyP1_7_S1_4/subject/"),

    /** https://www.skolverket.se/undervisning/laroplaner-amnen-och-kurser/gymnasieutbildning */
    GYS("gys.tgz", "gysP1_7_S1_4/subject/"),

    /** https://www.skolverket.se/undervisning/laroplaner-amnen-och-kurser/vuxenutbildning/komvux/grundlaggande */
    VUXGR("vuxgr.tgz", "vuxgrP1_7_S1_4/subject/"),

    /** https://www.skolverket.se/undervisning/laroplaner-amnen-och-kurser/vuxenutbildning/komvux/sfi */
    SFI("sfi.tgz", "sfiP1_7_S1_4/subject/");


    private fun getDownloadFileStream(): InputStream {
        val urlToDownload = URL("http://opendata.skolverket.se/data/$filename")
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

        val currentFile = File(cacheDir, filename)
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