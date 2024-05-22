package ru.romanow.logging

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import ru.romanow.logging.config.ApplicationProperties

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties::class)
class LogMaskingApplication

fun main(args: Array<String>) {
    runApplication<LogMaskingApplication>(* args)
}
