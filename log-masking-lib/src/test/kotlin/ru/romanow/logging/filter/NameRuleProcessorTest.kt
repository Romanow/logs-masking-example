package ru.romanow.logging.filter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.romanow.logging.filter.rules.NameRuleProcessor

class NameRuleProcessorTest {

    @Test
    fun testNameRule() {
        val processor = NameRuleProcessor("lastName")
        assertThat(processor.apply("\"lastName\" : \"Романов\"")).isEqualTo("\"lastName\" : \"Р******\"")
        assertThat(processor.apply("\"lastName\" : \"Скалозубов\"")).isEqualTo("\"lastName\" : \"Ск***в\"")
    }
}
