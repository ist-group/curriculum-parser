package org.edtech.curriculum

import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.*
import java.io.File
import org.apache.commons.compress.archivers.ArchiveInputStream


/**
 * Reads a from Skolverket containing subject and course information.
 * The files are available at file at http://opendata.skolverket.se/data/
 */
class SkolverketFileArchive(private val archiveFile: File) {

    /**
     * Check if archive file exists
     */
    fun archiveExists(): Boolean {
        return archiveFile.exists()
    }

    /**
     * Return a file stream for the specified file
     */
    fun fileExists(fileName: String): Boolean {
        FileInputStream(archiveFile).use {
            val tarInput = GzipCompressorInputStream(it)
            val dataInput = ArchiveStreamFactory().createArchiveInputStream("tar", tarInput) as TarArchiveInputStream
            var tarEntry = dataInput.nextEntry as TarArchiveEntry?
            while (tarEntry != null ) {
                if (tarEntry.name.endsWith(fileName)) {
                    return true
                }
                tarEntry = dataInput.nextTarEntry
            }
        }
        return false
    }

    /**
     * Return a list of file streams for all files matching the pathname
     */
    fun getFileStreams(pathName: String = ""): List<InputStream> {
        val result = mutableListOf<InputStream>()
        FileInputStream(archiveFile).use {
            val tarInput = GzipCompressorInputStream(it)
            val dataInput = ArchiveStreamFactory().createArchiveInputStream("tar", tarInput) as TarArchiveInputStream
            var tarEntry = dataInput.nextEntry as TarArchiveEntry?
            while (tarEntry != null) {
                if (tarEntry.name.contains(pathName)) {
                    if (tarEntry.size.toInt() > 0) {
                        result.add(getArchiveFileData(dataInput, tarEntry.size.toInt()))
                    }
                }
                tarEntry = dataInput.nextTarEntry
            }
        }
        return result.toList()
    }

    /**
     * Reads an file entry from the archive
     */
    private fun getArchiveFileData(tarFile: ArchiveInputStream, fileSize: Int): ByteArrayInputStream {
        /* Get Size of the file and create a byte array for the size */
        var readSize = 4096
        val content = ByteArray(fileSize)
        /* Read file from the archive into byte array */
        for (offset in 0 until content.size step readSize) {
            // Check that we do not exceed the boundary of the record
            if (offset + readSize > fileSize) {
                readSize = fileSize - offset
            }
            tarFile.read(content, offset, readSize)
        }
        return ByteArrayInputStream(content)
    }
}

class SkolverketFileArchiveFileNotFound(override var message:String): Exception(message)