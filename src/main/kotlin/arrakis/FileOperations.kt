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

import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile

interface FileOperations {

    fun get(folderName: String, filename: String): ResponseEntity<Resource>

    fun post(folderName: String, file: MultipartFile): ResponseEntity<String>

    fun delete(folderName: String, filename: String): ResponseEntity<String>

    fun put(folderName: String, filename: String, updatedFile: MultipartFile):
            ResponseEntity<String>
    
}