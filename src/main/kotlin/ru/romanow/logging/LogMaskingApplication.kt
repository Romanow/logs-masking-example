package ru.romanow.logging

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LogMaskingApplication

fun main(args: Array<String>) {
    runApplication<LogMaskingApplication>(* args)
}
