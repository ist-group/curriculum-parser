package org.edtech.curriculum

import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.*
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * A file at http://opendata.skolverket.se/data/ containing subject and course information.
 */
enum class SkolverketFile(val filename: String) {

    /** https://www.skolverket.se/laroplaner-amnen-och-kurser/gymnasieutbildning/gymnasieskola */
    GY("syllabus"),
    /** https://www.skolverket.se/laroplaner-amnen-och-kurser/vuxenutbildning/komvux/grundlaggande */
    VUXGR("vuxgr");

    private fun getLocallyCachedFile(cacheDir: File): File {
        if (!cacheDir.isDirectory) {
            if (!cacheDir.mkdir()) {
                System.err.println("ERROR: Unable to create cache dir: " + cacheDir.absolutePath)
                System.exit(1)
            }
        }

        val currentFile = File(cacheDir, filename + ".tgz")
        if (currentFile.isFile) return currentFile;

        val tmpFile = File(currentFile.absolutePath + ".download")
        val urlToDownload = URL("http://opendata.skolverket.se/data/${filename}.tgz")

        val connection = urlToDownload.openConnection();
        val inStream = connection.getInputStream();
        var bytesRead = -1;
        val bytes = ByteArray(4096);
        FileOutputStream(tmpFile).use {
            while ({ bytesRead = inStream.read(bytes); bytesRead }() != -1) {
                it.write(bytes, 0, bytesRead)
            }
        }

        tmpFile.renameTo(currentFile);

        return currentFile
    }

    fun subjectNames(cacheDir: File = File(System.getProperty("java.io.tmpdir"))): List<String> {
        val result = mutableListOf<String>()

        FileInputStream(getLocallyCachedFile(cacheDir)).use {
            val tarInput = GzipCompressorInputStream(it)
            val dataInput = ArchiveStreamFactory().createArchiveInputStream("tar", tarInput) as TarArchiveInputStream
            var tarEntry: TarArchiveEntry? = null
            while ({ tarEntry = dataInput.nextEntry as TarArchiveEntry?; tarEntry }() != null) {
                var path = tarEntry!!.name
                if (path.contains("/subject/") && path.endsWith(".xml")) {
                    val fileName = path.split("/").last();
                    val subjectName = fileName.substring(0, fileName.length - ".xml".length);
                    result.add(subjectName)
                }
            }
        }

        result.sort()
        return result
    }

    fun openSubject(subjectName: String, cacheDir: File = File(System.getProperty("java.io.tmpdir"))) : SubjectParser {
        FileInputStream(getLocallyCachedFile(cacheDir)).use {
            val tarInput = GzipCompressorInputStream(it)
            val dataInput = ArchiveStreamFactory().createArchiveInputStream("tar", tarInput) as TarArchiveInputStream
            var tarEntry: TarArchiveEntry? = null
            while ({ tarEntry = dataInput.nextEntry as TarArchiveEntry?; tarEntry }() != null) {
                val path = tarEntry!!.name
                if (path.contains("/subject/") && path.endsWith(subjectName + ".xml")) {
                    var line: String? = null
                    val reader = BufferedReader(InputStreamReader(dataInput))
                    val result = StringBuilder()
                    while ({ line = reader.readLine(); line }() != null) {
                        result.append(line).append("\n")
                    }
                    val stream = ByteArrayInputStream(result.toString().toByteArray(StandardCharsets.UTF_8))
                    return SubjectParser(stream)
                }
            }
        }

        throw IllegalArgumentException("Unsupported subject: ${subjectName}")
    }

}