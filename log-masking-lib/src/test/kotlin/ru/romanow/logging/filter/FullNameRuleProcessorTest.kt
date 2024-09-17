package ru.romanow.logging.filter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.romanow.logging.filter.rules.FullNameRuleProcessor

class FullNameRuleProcessorTest {

    @Test
    fun testFullNameRule() {
        val processor = FullNameRuleProcessor("fullName")
        assertThat(processor.apply("\"fullName\" : \"Романов Алексей Сергеевич\""))
            .isEqualTo("\"fullName\" : \"Р****** Алексей Се***ч\"")
        assertThat(processor.apply("\"fullName\" : \"Гогия Владимир\""))
            .isEqualTo("\"fullName\" : \"Г**** Владимир\"")
    }
}
