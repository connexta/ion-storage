package arrakis

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
import java.io.File
import java.nio.file.Files.readAllBytes
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.nio.file.Paths

@RestController
@RequestMapping("/folders/{folderName}/files")
class FileController: FileOperations {

    @GetMapping("/{filename}")
    fun get(@PathVariable folderName: String,
            @PathVariable filename: String):
            ResponseEntity<Resource> {

        val file = File("/$folderName/$filename")
        val path = Paths.get(file.absolutePath)
        val resource = ByteArrayResource(readAllBytes(path))

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .contentLength(file.length())
            .contentType(MediaType.parseMediaType(
                "application/octet-stream"))
            .body(resource)
    }

    //TODO: look into uploading multiple files
    @PostMapping()
    fun post(@PathVariable folderName: String,
             @RequestParam file: MultipartFile):
            ResponseEntity<String> {

        if (!file.isEmpty) {
            file.transferTo(File(
                "/$folderName/${file.originalFilename}"))

            return ResponseEntity.ok().build()
        }

        return ResponseEntity.badRequest().build()
    }

    @PutMapping("/{filename}")
    fun put(@PathVariable folderName: String,
            @PathVariable filename: String,
            @RequestParam updatedFile: MultipartFile):
            ResponseEntity<String> {

        val oldFile = File("/$folderName/$filename")

        if (oldFile.exists() && oldFile.isFile) {

            updatedFile.transferTo(File(
                "/$folderName/${oldFile.name}"))

            return ResponseEntity.ok().build()
        }

        return ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{filename}")
    fun delete(@PathVariable folderName: String,
               @PathVariable filename: String):
            ResponseEntity<String> {

        val file = File("/$folderName/$filename")

        if (file.exists() && file.isFile) {

            file.delete()

        } else {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok().build()
    }
}