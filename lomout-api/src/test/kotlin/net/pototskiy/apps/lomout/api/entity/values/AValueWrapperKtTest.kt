package net.pototskiy.apps.lomout.api.entity.values

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.entity.AttributeAsCell
import net.pototskiy.apps.lomout.api.entity.AttributeListType
import net.pototskiy.apps.lomout.api.entity.AttributeReaderWithFunction
import net.pototskiy.apps.lomout.api.entity.AttributeWriterWithFunction
import net.pototskiy.apps.lomout.api.entity.BooleanListType
import net.pototskiy.apps.lomout.api.entity.BooleanType
import net.pototskiy.apps.lomout.api.entity.DateListType
import net.pototskiy.apps.lomout.api.entity.DateTimeListType
import net.pototskiy.apps.lomout.api.entity.DateTimeType
import net.pototskiy.apps.lomout.api.entity.DateType
import net.pototskiy.apps.lomout.api.entity.DoubleListType
import net.pototskiy.apps.lomout.api.entity.DoubleType
import net.pototskiy.apps.lomout.api.entity.EntityTypeManager
import net.pototskiy.apps.lomout.api.entity.LongListType
import net.pototskiy.apps.lomout.api.entity.LongType
import net.pototskiy.apps.lomout.api.entity.StringListType
import net.pototskiy.apps.lomout.api.entity.StringType
import net.pototskiy.apps.lomout.api.entity.TextListType
import net.pototskiy.apps.lomout.api.entity.TextType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.CONCURRENT)
@Suppress("MagicNumber", "TooManyFunctions")
internal class AValueWrapperKtTest {
    @Test
    internal fun wrapBooleanTest() {
        val attr = EntityTypeManager().createAttribute("attr", BooleanType::class)
        assertThat(wrapAValue(attr, true)).isEqualTo(BooleanType(true))
        assertThat(wrapAValue(attr, false)).isEqualTo(BooleanType(false))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to BooleanType")
    }

    @Test
    internal fun wrapLongTest() {
        val attr = EntityTypeManager().createAttribute("attr", LongType::class)
        assertThat(wrapAValue(attr, 111L)).isEqualTo(LongType(111L))
        assertThat(wrapAValue(attr, 123L)).isEqualTo(LongType(123L))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to LongType")
    }

    @Test
    internal fun wrapDoubleTest() {
        val attr = EntityTypeManager().createAttribute("attr", DoubleType::class)
        assertThat(wrapAValue(attr, 11.1)).isEqualTo(DoubleType(11.1))
        assertThat(wrapAValue(attr, 12.3)).isEqualTo(DoubleType(12.3))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to DoubleType")
    }

    @Test
    internal fun wrapStringTest() {
        val attr = EntityTypeManager().createAttribute("attr", StringType::class)
        assertThat(wrapAValue(attr, "11.1")).isEqualTo(StringType("11.1"))
        assertThat(wrapAValue(attr, "12.3")).isEqualTo(StringType("12.3"))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to StringType")
    }

    @Test
    internal fun wrapDateTest() {
        val attr = EntityTypeManager().createAttribute("attr", DateType::class)
        val now1 = DateTime()
        val now2 = DateTime()
        assertThat(wrapAValue(attr, now1)).isEqualTo(DateType(now1))
        assertThat(wrapAValue(attr, now2)).isEqualTo(DateType(now2))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to DateType")
    }

    @Test
    internal fun wrapDateTimeTest() {
        val attr = EntityTypeManager().createAttribute("attr", DateTimeType::class)
        val now1 = DateTime()
        val now2 = DateTime()
        assertThat(wrapAValue(attr, now1)).isEqualTo(DateTimeType(now1))
        assertThat(wrapAValue(attr, now2)).isEqualTo(DateTimeType(now2))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to DateTimeType")
    }

    @Test
    internal fun wrapTextTest() {
        val attr = EntityTypeManager().createAttribute("attr", TextType::class)
        assertThat(wrapAValue(attr, "11.1")).isEqualTo(TextType("11.1"))
        assertThat(wrapAValue(attr, "12.3")).isEqualTo(TextType("12.3"))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to TextType")
    }

    @Test
    internal fun wrapTextListTest() {
        val attr = EntityTypeManager().createAttribute("attr", TextListType::class) {
            reader(AttributeReaderWithFunction { _, _ -> TextListType(listOf()) })
            writer(AttributeWriterWithFunction { _, _ -> Unit })
        }
        assertThat(wrapAValue(attr, listOf("11.1"))).isEqualTo(TextListType(listOf(TextType("11.1"))))
        assertThat(wrapAValue(attr, listOf("12.3"))).isEqualTo(TextListType(listOf(TextType("12.3"))))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to TextListType")
    }

    class NewType : LongType(1, false)

    @Test
    internal fun wrapUnknownTypeTest() {
        val attr = EntityTypeManager().createAttribute("attr", NewType::class) {
            reader(AttributeReaderWithFunction { _, _ -> NewType() })
            writer(AttributeWriterWithFunction { _, _ -> Unit })
        }
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Unexpected type<${NewType::class.simpleName}>")
    }

    @Test
    internal fun wrapBooleanListTest() {
        val attr = EntityTypeManager().createAttribute("attr", BooleanListType::class)
        @Suppress("BooleanLiteralArgument")
        assertThat(wrapAValue(attr, listOf(true, false)))
            .isEqualTo(BooleanListType(listOf(BooleanType(true), BooleanType(false))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to BooleanListType")
    }

    @Test
    internal fun wrapLongListTest() {
        val attr = EntityTypeManager().createAttribute("attr", LongListType::class)
        assertThat(wrapAValue(attr, listOf(111L, 123L)))
            .isEqualTo(LongListType(listOf(LongType(111L), LongType(123L))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to LongListType")
    }

    @Test
    internal fun wrapDoubleListTest() {
        val attr = EntityTypeManager().createAttribute("attr", DoubleListType::class)
        assertThat(wrapAValue(attr, listOf(11.1, 12.3)))
            .isEqualTo(DoubleListType(listOf(DoubleType(11.1), DoubleType(12.3))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to DoubleListType")
    }

    @Test
    internal fun wrapStringListTest() {
        val attr = EntityTypeManager().createAttribute("attr", StringListType::class)
        assertThat(wrapAValue(attr, listOf("11.1", "12.3")))
            .isEqualTo(StringListType(listOf(StringType("11.1"), StringType("12.3"))))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to StringListType")
    }

    @Test
    internal fun wrapDateListTest() {
        val attr = EntityTypeManager().createAttribute("attr", DateListType::class)
        val now1 = DateTime()
        val now2 = DateTime()
        assertThat(wrapAValue(attr, listOf(now1, now2)))
            .isEqualTo(DateListType(listOf(DateType(now1), DateType(now2))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to DateListType")
    }

    @Test
    internal fun wrapDateTimeListTest() {
        val attr = EntityTypeManager().createAttribute("attr", DateTimeListType::class)
        val now1 = DateTime()
        val now2 = DateTime()
        assertThat(wrapAValue(attr, listOf(now1, now2)))
            .isEqualTo(DateTimeListType(listOf(DateTimeType(now1), DateTimeType(now2))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to DateTimeListType")
    }

    @Test
    internal fun wrapAttributeListTest() {
        val entityTypeManager = EntityTypeManager()
        val attr = entityTypeManager.createAttribute("attr", AttributeListType::class)
        val attr1 = entityTypeManager.createAttribute("attr1", StringType::class)
        val attrMap = mapOf(
            "test1" to AttributeAsCell(attr1, StringType("123")),
            "test2" to AttributeAsCell(attr1, StringType("234"))
        )
        assertThat(wrapAValue(attr, attrMap))
            .isEqualTo(AttributeListType(attrMap))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value to AttributeListType")
    }
}
