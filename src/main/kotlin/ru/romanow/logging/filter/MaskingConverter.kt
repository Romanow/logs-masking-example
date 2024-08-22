package ru.romanow.logging.filter

import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.Configuration
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.layout.PatternLayout
import org.apache.logging.log4j.core.pattern.ConverterKeys
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter
import org.apache.logging.log4j.core.pattern.PatternFormatter
import org.springframework.core.io.ClassPathResource
import org.yaml.snakeyaml.Yaml
import ru.romanow.logging.filter.rules.*
import ru.romanow.logging.properties.MaskingProperties
import ru.romanow.logging.properties.RuleType.*

@ConverterKeys("mask")
@Plugin(name = "MaskingConverter", category = "Converter")
class MaskingConverter private constructor(
    private val formatters: MutableList<PatternFormatter>
) : LogEventPatternConverter("mask", "mask") {

    private var rules = mutableListOf<RuleProcessor>()

    init {
        val file = ClassPathResource("logging/masking.yml")
        val properties = Yaml().loadAs(file.inputStream, MaskingProperties::class.java)

        for ((type, field, regex) in properties.masking!!) {
            val ruleProcessor = when (type!!) {
                TEXT -> TextRuleProcessor(field, regex)
                EMAIL -> EmailRuleProcessor(field)
                NAME -> NameRuleProcessor(field)
                FULL_NAME -> FullNameRuleProcessor(field)
            }
            rules.add(ruleProcessor)
        }
    }

    override fun format(event: LogEvent, toAppendTo: StringBuilder) {
        val buffer = java.lang.StringBuilder()
        for (formatter in formatters) {
            formatter.format(event, buffer)
        }
        val message = mask(buffer.toString())
        toAppendTo.append(message)
    }

    private fun mask(message: String): String {
        var result = message
        for (rule in rules) {
            result = rule.apply(result)
        }
        return result
    }

    companion object {

        @JvmStatic
        fun newInstance(config: Configuration, options: Array<String>): MaskingConverter {
            val parser = PatternLayout.createPatternParser(config)
            val formatters = parser.parse(options[0])
            return MaskingConverter(formatters)
        }
    }
}
