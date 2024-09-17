package ru.romanow.logging.properties

data class MaskingProperties(
    var masking: List<MaskingRules>? = null
)

data class MaskingRules(
    var type: RuleType? = null,
    var field: String? = null,
    var regex: Regex? = null,
)

enum class RuleType {
    FULL_NAME,
    NAME,
    EMAIL,
    TEXT
}
