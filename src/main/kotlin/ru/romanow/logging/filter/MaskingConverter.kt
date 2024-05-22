package ru.romanow.logging.filter

import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.layout.PatternLayout
import org.apache.logging.log4j.core.layout.PatternLayout.newBuilder
import org.apache.logging.log4j.core.pattern.ConverterKeys
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter
import java.util.regex.Pattern

@ConverterKeys("mask")
@Plugin(name = "MaskingConverter", category = "Converter")
class MaskingConverter private constructor(options: Array<String>) : LogEventPatternConverter("mask", "mask") {

    private val patternLayout: PatternLayout

    init {
        patternLayout = newBuilder()
            .withPattern(options[0])
            .build()
    }

    override fun format(event: LogEvent, toAppendTo: StringBuilder) {
        val formattedMessage = patternLayout.toSerializable(event)
        val maskedMessage = maskSensitiveValues(formattedMessage)
        toAppendTo.setLength(0)
        toAppendTo.append(maskedMessage)
    }

    private fun maskSensitiveValues(message: String): String {
        return findMatchesAndMask(NAME_PATTERN, NAME_MASK_PATTERN, message)
    }

    private fun findMatchesAndMask(pattern: Pattern, mask: String, message: String): String {
        var result = message
        val matcher = pattern.matcher(message)
        while (matcher.find()) {
            result = result.replaceRange(matcher.start()..<matcher.end(), mask)
        }
        return result
    }

    companion object {
        private const val NAME_MASK_PATTERN = "\"name\":\"<masked>\""
        private val NAME_PATTERN = Pattern.compile("\"name\"\\s*:\\s*\".+\"")

        @JvmStatic
        fun newInstance(options: Array<String>): MaskingConverter = MaskingConverter(options)
    }
}
