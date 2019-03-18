package net.pototskiy.apps.lomout.api.source.nested

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class NestedAttributeListParserTest {
    @Test
    internal fun parseCorrectDataTest() {
        val parser = NestedAttributeListParser(null, ',', null, '=')
        val v = parser.parse("attr1=value1,attr2=value2")
        assertThat(v).containsAllEntriesOf(
            mapOf(
                "attr1" to "value1",
                "attr2" to "value2"
            )
        )
    }

    @Test
    internal fun parseNoValueDataTest() {
        val parser = NestedAttributeListParser(null, ',', null, '=')
        val v = parser.parse("attr1=,attr2=")
        assertThat(v).containsAllEntriesOf(
            mapOf(
                "attr1" to "",
                "attr2" to ""
            )
        )
    }

    @Test
    internal fun parseNoNameDataTest() {
        val parser = NestedAttributeListParser(null, ',', null, '=')
        val v = parser.parse("=value1,=value2")
        assertThat(v).containsAllEntriesOf(
            mapOf(
                "" to "value1",
                "" to "value2"
            )
        )
    }

    @Test
    internal fun parseNoDataTest() {
        val parser = NestedAttributeListParser(null, ',', null, '=')
        val v = parser.parse("=,=")
        assertThat(v).containsAllEntriesOf(
            mapOf(
                "" to "",
                "" to ""
            )
        )
    }
}
