package ru.romanow.logging.filter

import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.layout.PatternLayout
import org.apache.logging.log4j.core.layout.PatternLayout.newBuilder
import org.apache.logging.log4j.core.pattern.ConverterKeys
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter
import org.springframework.core.io.ClassPathResource
import org.yaml.snakeyaml.Yaml
import ru.romanow.logging.filter.rules.EmailRuleProcessor
import ru.romanow.logging.filter.rules.FullNameRuleProcessor
import ru.romanow.logging.filter.rules.NameRuleProcessor
import ru.romanow.logging.filter.rules.RuleProcessor
import ru.romanow.logging.filter.rules.TextRuleProcessor
import ru.romanow.logging.properties.MaskingProperties
import ru.romanow.logging.properties.RuleType.EMAIL
import ru.romanow.logging.properties.RuleType.FULL_NAME
import ru.romanow.logging.properties.RuleType.NAME
import ru.romanow.logging.properties.RuleType.TEXT

@ConverterKeys("mask")
@Plugin(name = "MaskingConverter", category = "Converter")
class MaskingConverter private constructor(options: Array<String>) : LogEventPatternConverter("mask", "mask") {

    private val patternLayout: PatternLayout
    private var rules = mutableListOf<RuleProcessor>()

    init {
        patternLayout = newBuilder()
            .withPattern(options[0])
            .build()

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
        var message = patternLayout.toSerializable(event)
        for (rule in rules) {
            message = rule.apply(message)
        }
        toAppendTo.setLength(0)
        toAppendTo.append(message)
    }

    companion object {

        @JvmStatic
        fun newInstance(options: Array<String>): MaskingConverter = MaskingConverter(options)
    }
}
