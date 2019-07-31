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

import org.json.JSONException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.io.IOException
import java.lang.Exception

class ExceptionHandler {

    private val log: Logger = LoggerFactory
        .getLogger(FolderController::class.java)

    fun handle(e: Exception): ResponseEntity<String> {
        var message: String = when (e) {
            is IOException -> "An IO error occurred while attempting to fulfil request."
            is JSONException -> "An error occurred while creating the JSON" +
                    " object for the response body."
            is OutOfMemoryError -> "Unable to allocate required space."
            else -> throw e
        }

        log.error(message)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(message)
    }
}