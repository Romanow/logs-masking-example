package ru.romanow.logging.filter.rules

interface RuleProcessor {
    fun apply(text: String): String
}
