package ru.romanow.logging.web

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class LogSender {
    private val logger = LoggerFactory.getLogger(LogSender::class.java)

    @PostMapping("/api/v1/send", consumes = [APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun send(@RequestBody message: String) {
        logger.info(message)
    }
}
