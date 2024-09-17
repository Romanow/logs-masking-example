package ru.romanow.logging.filter.rules

class EmailRuleProcessor(field: String?) : BaseRuleProcessor(EMAIL_PATTERN.format(field).toRegex()) {
    override fun mask(text: String): String {
        val index = text.indexOf("@")
        return text.replaceRange(0 until index, text[0].toString() + "*".repeat(index - 1))
    }

    companion object {
        private const val EMAIL_PATTERN = "\"%s\"\\s*:\\s*\"(\\S+@\\S+)\""
    }
}
