package ru.romanow.logging.filter.rules

abstract class BaseRuleProcessor(private val regex: Regex) : RuleProcessor {
    override fun apply(text: String): String {
        var result = text
        for (match in regex.findAll(text)) {
            val group = match.groups[1]!!
            result = result.replaceRange(group.range, mask(group.value))
        }
        return result
    }

    protected abstract fun mask(text: String): String
}
