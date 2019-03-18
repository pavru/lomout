package net.pototskiy.apps.lomout.api.source.nested

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
internal class AttributeListPrinterTest {
    @Test
    internal fun simplePrint1Test() {
        val printer = NestedAttributeListPrinter(null, ',', null, '=')
        assertThat(
            printer.print(
                mapOf(
                    "attr1" to "value1",
                    "attr2" to "value2",
                    "attr3" to ""
                )
            )
        ).isEqualTo("attr1=value1,attr2=value2,attr3=")
    }

    @Test
    internal fun simplePrint2Test() {
        val printer = NestedAttributeListPrinter(null, ',', '"', '=')
        assertThat(
            printer.print(
                mapOf(
                    "attr1" to "value=1",
                    "attr2" to "value2",
                    "attr3" to ""
                )
            )
        ).isEqualTo("attr1=\"value=1\",attr2=value2,attr3=")
    }
}
