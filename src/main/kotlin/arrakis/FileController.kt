/*
 * Copyright (c) 2019 Connexta, LLC
 *
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU
 *  Lesser General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *  A copy of the GNU Lesser General Public License is distributed along with this program and can be found at
 *  http://www.gnu.org/licenses/lgpl.html
 */
package arrakis

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files.readAllBytes
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.nio.file.Paths
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.nio.file.InvalidPathException

@RestController
@RequestMapping("/folders/{folderName}/files")
class FileController: FileOperations {

    private val log: Logger = LoggerFactory
        .getLogger(FolderController::class.java)
    private val handler = ExceptionHandler()

    @GetMapping("/{filename}")
    override fun get(@PathVariable folderName: String,
                     @PathVariable filename: String):
            ResponseEntity<Resource> {

        val file = File("/$folderName/$filename")
        lateinit var resource: Resource

        try {
            resource = ByteArrayResource(
                readAllBytes(Paths.get(file.absolutePath)))
        } catch (e: Exception) {
            when (e) {
                is InvalidPathException,
                is IOException,
                is OutOfMemoryError -> {
                    log.error("Unable to retrieve file")
                    return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build()
                }
            }
        }

        log.info("File $filename retrieved successfully")
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .contentLength(file.length())
            .contentType(MediaType.parseMediaType(
                "application/octet-stream"))
            .body(resource)
    }

    //TODO: look into uploading multiple files
    @PostMapping
    override fun post(@PathVariable folderName: String,
             @RequestParam file: MultipartFile):
            ResponseEntity<String> {

        if (!file.isEmpty) {

            try {
                file.transferTo(File(
                    "/$folderName/${file.originalFilename}"))
            } catch (e: Exception) {
                return handler.handle(e)
            }
            log.info("File upload successful")
            return ResponseEntity.ok().build()
        }

        return ResponseEntity.badRequest().build()
    }

    @PutMapping("/{filename}")
    override fun put(@PathVariable folderName: String,
            @PathVariable filename: String,
            @RequestParam updatedFile: MultipartFile):
            ResponseEntity<String> {

        val oldFile = File("/$folderName/$filename")

        if (oldFile.exists() && oldFile.isFile) {
            try {
                updatedFile.transferTo(File(
                    "/$folderName/${oldFile.name}"))

            } catch (e: Exception) {
                return handler.handle(e)
            }
            log.info("File updated")
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{filename}")
    override fun delete(@PathVariable folderName: String,
               @PathVariable filename: String):
            ResponseEntity<String> {

        val file = File("/$folderName/$filename")

        if (file.exists() && file.isFile) {
            try {
                file.delete()
            } catch (e: Exception) {
                return handler.handle(e)
            }
        } else {
            return ResponseEntity.notFound().build()
        }
        log.info("File deleted")
        return ResponseEntity.ok().build()
    }
}