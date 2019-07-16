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
import org.json.JSONObject
import java.io.File
import java.util.Date

@RestController
@RequestMapping("/folders")
class FolderController: FolderOperations {

    @GetMapping
    fun get(): ResponseEntity<String> {

        val folders = mutableListOf<String?>()

        File("/").listFiles()
            .forEach { folder ->
                if (folder.isDirectory) {
                    folders.add(folder.name)
                }
            }

        val responseBody = JSONObject(mapOf(
            "folders" to folders
        ))

        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(responseBody.toString())
    }

    @GetMapping("/{folderName}")
    fun get(@PathVariable folderName: String):
            ResponseEntity<String> {

        val folder = File("/$folderName")

        if (folder.exists() && folder.isDirectory) {
            val files = folder.listFiles()
            val lastModified = Date(folder.lastModified())

            val responseBody = JSONObject(mapOf(
                "folder" to mapOf(
                    "contents" to files,
                    "last modified" to lastModified,
                    "folder name" to folderName
                )
            ))

            return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBody.toString())

        } else {
            return ResponseEntity.notFound().build()
        }
    }

    //TODO: Decide between the two POST method designs
    @PostMapping("/{folderName}")
    fun post(@PathVariable folderName: String):
            ResponseEntity<String> {

        //val folderCreated = File(folderName).mkdirs()

        //TODO: Remove this or use HATEOAS
        val responseBody = JSONObject(mapOf(
            "message" to "Folder '$folderName' created successfully"
        ))

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(responseBody.toString())
    }

    @PostMapping
    fun post(@RequestBody payload: Map<String, Any>):
            ResponseEntity<String> {
        if ("folderName" in payload) {
            val folderName = payload["folderName"].toString()
            val folder = File(folderName)

            if (folder.exists() && folder.isDirectory) {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT).build()
            }

            val folderCreated = folder.mkdirs()

            if (folderCreated) {
                //TODO: Remove this or use HATEOAS
                val responseBody = JSONObject(mapOf(
                    "message" to "Folder '$folderName' created successfully"
                ))

                return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(responseBody.toString())

            } else {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT).build()
            }

        }

        return ResponseEntity.badRequest().build()
    }

    @PutMapping("/{folderName}")
    fun put(@PathVariable folderName: String,
            @RequestBody payload: Map<String, Any>):
            ResponseEntity<String> {

        val folder = File("/$folderName")

        if (folder.exists() && folder.isDirectory) {
            if ("folderName" in payload) {
                folder.renameTo(
                    File(payload["folderName"].toString())
                )
            } else {
                return ResponseEntity.badRequest().build()
            }

        } else {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok().build()
    }

    //TODO: Delete folders with files inside?
    @DeleteMapping("/{folderName}")
    fun delete(@PathVariable folderName: String):
            ResponseEntity<String> {

        val folder = File("/$folderName")

        if (folder.exists() && folder.isDirectory) {

            if (folder.listFiles().isNotEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT).build()
            }

            folder.delete()

        } else {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok().build()
    }
}