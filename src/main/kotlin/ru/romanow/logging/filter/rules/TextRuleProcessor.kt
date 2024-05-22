package ru.romanow.logging.filter.rules

import java.math.RoundingMode.UP

class TextRuleProcessor(field: String? = null, regex: Regex? = null) :
    BaseRuleProcessor(if (field != null) TEXT_REGEX.format(field).toRegex() else regex!!) {

    override fun mask(text: String): String {
        return when (val length = text.length) {
            1 -> text.replace(text, "*")
            in 1..4 -> text.replaceRange(1 until length, "*".repeat(length - 1))
            in 5..9 -> {
                val n = calculateMaskLength(length)
                text.replaceRange(1 until n + 1, "*".repeat(n))
            }
            in 10..15 -> {
                val n = calculateMaskLength(length)
                text.replaceRange(2 until n + 2, "*".repeat(n))
            }
            else -> {
                val n = calculateMaskLength(length)
                text.replaceRange(3 until n + 3, "*".repeat(n))
            }
        }
    }

    private fun calculateMaskLength(length: Int) = (length * 0.6).toBigDecimal().setScale(0, UP).toInt()

    companion object {
        private const val TEXT_REGEX = "\"%s\"\\s*:\\s*\"(.+)\""
    }
}
