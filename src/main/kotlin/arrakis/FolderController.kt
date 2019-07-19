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

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.io.File
import java.lang.Exception
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths

@RestController
@RequestMapping("/folders")
class FolderController: FolderOperations {

    private val log: Logger = LoggerFactory
        .getLogger(FolderController::class.java)
    private val handler = ExceptionHandler()

    /**
     * Returns a list containing all created folders
     */
    @GetMapping
    override fun get(): ResponseEntity<String> {

        val folders = mutableListOf<String?>()

        try {
            File("/").listFiles()
                .forEach { folder ->
                    if (folder.isDirectory) {
                        folders.add(folder.name)
                    }
                }
        } catch (e: Exception) {
            return handler.handle(e)
        }

        lateinit var responseBody: JSONObject

        try {
            responseBody = JSONObject(
                mapOf( "folders" to folders))

        } catch (e: Exception) {
            return handler.handle(e)
        }

        log.info("List of folders successfully retrieved")

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(responseBody.toString())
    }

    /**
     * Returns names of files contained within the specified folder
     */
    @GetMapping("/{folderName}")
    override fun get(@PathVariable folderName: String):
            ResponseEntity<String> {

        lateinit var folder: File

        try {
            folder = File("/$folderName")

            if (folder.exists() && folder.isDirectory) {
                log.info("/$folderName directory found")

                val files = mutableListOf<String?>()
                val responseBody: JSONObject


                folder.listFiles()?.forEach { file ->
                    files.add(file.name)
                }
                    responseBody = JSONObject(
                        mapOf("folder" to mapOf(
                                "contents" to files,
                                "folder name" to folderName
                        )))

                return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(responseBody.toString())

            } else return ResponseEntity.notFound().build()

        } catch (e: Exception) {
            return handler.handle(e)
        }
    }

    /**
     *  Given "folderName" in [payload], creates folder with that name
     */
    @PostMapping
    override fun post(@RequestBody payload: Map<String, Any>):
            ResponseEntity<String> {

        val message: String
        val responseBody: JSONObject

        if ("folderName" in payload) {
            val folderName = payload["folderName"].toString()
            val folder = File(folderName)

            try {
                if (folder.exists() && folder.isDirectory) {
                    message = "A folder with the name " +
                            "\"$folderName\" already exists"
                    responseBody = JSONObject(mapOf(
                        "message" to message
                    ))

                    return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseBody.toString())
                }

                Files.createDirectories(
                    Paths.get("/$folderName"))

            } catch (e: Exception) {
                return handler.handle(e)
            }

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .build()
        }

        message = "Parameter \"folderName\" missing from request body"
        responseBody = JSONObject(mapOf(
            "message" to message
        ))

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(responseBody.toString())
    }

    /**
     * Given [folderName], it is renamed to
     * the value of the key "folderName" in [payload]
     */
    @PutMapping("/{folderName}")
    override fun put(@PathVariable folderName: String,
            @RequestBody payload: Map<String, Any>):
            ResponseEntity<String> {

        val folder = File("/$folderName")

        if (folder.exists() && folder.isDirectory) {
            if ("folderName" in payload) {
                try {
                    folder.renameTo(
                        File(payload["folderName"].toString())
                    )
                } catch (e: Exception) { return handler.handle(e)}

            } else {
                val message = "\"folderName\" property missing" +
                        " from request body"
                val responseBody: JSONObject

                try {
                    responseBody = JSONObject(mapOf(
                        "message" to message
                    ))
                } catch (e: Exception) {
                    return handler.handle(e)
                }

                return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(responseBody.toString())
            }

        } else {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok().build()
    }

    /**
     * Deletes specified folder
     */
    @DeleteMapping("/{folderName}")
    override fun delete(@PathVariable folderName: String):
            ResponseEntity<String> {

        val folder = File("/$folderName")

        if (folder.exists() && folder.isDirectory) {

            // Folder is not deleted if it contains files
            if (folder.listFiles().isNotEmpty()) {
                lateinit var responseBody: JSONObject
                val message = "\"$folderName\" is not empty," +
                        " and therefore cannot be deleted"

                try {
                    responseBody = JSONObject(mapOf(
                        "message" to message
                    ))
                } catch (e: Exception) {
                    handler.handle(e)
                }

                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(responseBody.toString())
            }

            try {
                folder.delete()
            } catch (e: Exception) {
                handler.handle(e)
            }

        } else {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok().build()
    }
}