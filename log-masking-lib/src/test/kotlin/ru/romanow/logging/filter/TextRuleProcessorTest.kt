package ru.romanow.logging.filter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.romanow.logging.filter.rules.TextRuleProcessor

class TextRuleProcessorTest {

    @Test
    fun testTextRule() {
        val processor = TextRuleProcessor("code")
        assertThat(processor.apply("\"code\" : \"1\"")).isEqualTo("\"code\" : \"*\"")
        assertThat(processor.apply("\"code\" : \"1234\"")).isEqualTo("\"code\" : \"1***\"")
        assertThat(processor.apply("\"code\" : \"1234567\"")).isEqualTo("\"code\" : \"1*****7\"")
        assertThat(processor.apply("\"code\" : \"12345678\"")).isEqualTo("\"code\" : \"1*****78\"")
        assertThat(processor.apply("\"code\" : \"123456789\"")).isEqualTo("\"code\" : \"1******89\"")
        assertThat(processor.apply("\"code\" : \"12345678901\"")).isEqualTo("\"code\" : \"12*******01\"")
        assertThat(processor.apply("\"code\" : \"1234567890123\"")).isEqualTo("\"code\" : \"12********123\"")
        assertThat(processor.apply("\"code\" : \"12345678901234567\"")).isEqualTo("\"code\" : \"123***********567\"")
    }

    @Test
    fun testCustomRule() {
        val processor = TextRuleProcessor(regex = "(?:JWT|Authorization)\\s*[:=]\\s*(.+)".toRegex())
        assertThat(processor.apply("Authorization : Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"))
            .isEqualTo("Authorization : Bea**************************R5cCI6IkpXVCJ9")
    }
}
