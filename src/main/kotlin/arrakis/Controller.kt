package arrakis

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping


@RestController
class Controller {
    @GetMapping("/storage")
    fun get(): String = "GET"

    @PostMapping("/storage")
    fun post(): String = "POST"

    @PutMapping("/storage")
    fun put(): String = "PUT"

    @DeleteMapping("/storage")
    fun delete(): String = "DELETE"

}