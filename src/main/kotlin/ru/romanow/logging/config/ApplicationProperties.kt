package ru.romanow.logging.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application")
data class ApplicationProperties(
    var masking: List<MaskingRules>? = null
)

data class MaskingRules(
    var name: String? = null,
    var type: RuleType? = null,
    var regex: Regex? = null,
)

enum class RuleType {
    FULL_NAME,
    NAME,
    EMAIL,
    TEXT
}
