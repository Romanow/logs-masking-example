package ru.romanow.logging.filter.rules

class FullNameRuleProcessor(field: String?) : NameRuleProcessor(field) {
    override fun mask(text: String): String {
        val parts = text.split("\\s+".toRegex())
        return if (parts.size == 3) {
            super.mask(parts[0]) + " " + parts[1] + " " + super.mask(parts[2])
        } else {
            super.mask(parts[0]) + " " + parts[1]
        }
    }
}
