package net.pototskiy.apps.magemediation.api.entity.values

import net.pototskiy.apps.magemediation.api.database.DatabaseException
import net.pototskiy.apps.magemediation.api.entity.AttributeAsCell
import net.pototskiy.apps.magemediation.api.entity.AttributeListType
import net.pototskiy.apps.magemediation.api.entity.AttributeListValue
import net.pototskiy.apps.magemediation.api.entity.AttributeReaderWithFunction
import net.pototskiy.apps.magemediation.api.entity.AttributeWriterWithFunction
import net.pototskiy.apps.magemediation.api.entity.BooleanListType
import net.pototskiy.apps.magemediation.api.entity.BooleanListValue
import net.pototskiy.apps.magemediation.api.entity.BooleanType
import net.pototskiy.apps.magemediation.api.entity.BooleanValue
import net.pototskiy.apps.magemediation.api.entity.DateListType
import net.pototskiy.apps.magemediation.api.entity.DateListValue
import net.pototskiy.apps.magemediation.api.entity.DateTimeListType
import net.pototskiy.apps.magemediation.api.entity.DateTimeListValue
import net.pototskiy.apps.magemediation.api.entity.DateTimeType
import net.pototskiy.apps.magemediation.api.entity.DateTimeValue
import net.pototskiy.apps.magemediation.api.entity.DateType
import net.pototskiy.apps.magemediation.api.entity.DateValue
import net.pototskiy.apps.magemediation.api.entity.DoubleListType
import net.pototskiy.apps.magemediation.api.entity.DoubleListValue
import net.pototskiy.apps.magemediation.api.entity.DoubleType
import net.pototskiy.apps.magemediation.api.entity.DoubleValue
import net.pototskiy.apps.magemediation.api.entity.EntityTypeManager
import net.pototskiy.apps.magemediation.api.entity.LongListType
import net.pototskiy.apps.magemediation.api.entity.LongListValue
import net.pototskiy.apps.magemediation.api.entity.LongType
import net.pototskiy.apps.magemediation.api.entity.LongValue
import net.pototskiy.apps.magemediation.api.entity.StringListType
import net.pototskiy.apps.magemediation.api.entity.StringListValue
import net.pototskiy.apps.magemediation.api.entity.StringType
import net.pototskiy.apps.magemediation.api.entity.StringValue
import net.pototskiy.apps.magemediation.api.entity.TextListType
import net.pototskiy.apps.magemediation.api.entity.TextListValue
import net.pototskiy.apps.magemediation.api.entity.TextType
import net.pototskiy.apps.magemediation.api.entity.TextValue
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
        assertThat(wrapAValue(attr, true)).isEqualTo(BooleanValue(true))
        assertThat(wrapAValue(attr, false)).isEqualTo(BooleanValue(false))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to BooleanType")
    }

    @Test
    internal fun wrapLongTest() {
        val attr = EntityTypeManager().createAttribute("attr", LongType::class)
        assertThat(wrapAValue(attr, 111L)).isEqualTo(LongValue(111L))
        assertThat(wrapAValue(attr, 123L)).isEqualTo(LongValue(123L))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to LongType")
    }

    @Test
    internal fun wrapDoubleTest() {
        val attr = EntityTypeManager().createAttribute("attr", DoubleType::class)
        assertThat(wrapAValue(attr, 11.1)).isEqualTo(DoubleValue(11.1))
        assertThat(wrapAValue(attr, 12.3)).isEqualTo(DoubleValue(12.3))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to DoubleType")
    }

    @Test
    internal fun wrapStringTest() {
        val attr = EntityTypeManager().createAttribute("attr", StringType::class)
        assertThat(wrapAValue(attr, "11.1")).isEqualTo(StringValue("11.1"))
        assertThat(wrapAValue(attr, "12.3")).isEqualTo(StringValue("12.3"))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to StringType")
    }

    @Test
    internal fun wrapDateTest() {
        val attr = EntityTypeManager().createAttribute("attr", DateType::class)
        val now1 = DateTime()
        val now2 = DateTime()
        assertThat(wrapAValue(attr, now1)).isEqualTo(DateValue(now1))
        assertThat(wrapAValue(attr, now2)).isEqualTo(DateValue(now2))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to DateType")
    }

    @Test
    internal fun wrapDateTimeTest() {
        val attr = EntityTypeManager().createAttribute("attr", DateTimeType::class)
        val now1 = DateTime()
        val now2 = DateTime()
        assertThat(wrapAValue(attr, now1)).isEqualTo(DateTimeValue(now1))
        assertThat(wrapAValue(attr, now2)).isEqualTo(DateTimeValue(now2))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to DateTimeType")
    }

    @Test
    internal fun wrapTextTest() {
        val attr = EntityTypeManager().createAttribute("attr", TextType::class)
        assertThat(wrapAValue(attr, "11.1")).isEqualTo(TextValue("11.1"))
        assertThat(wrapAValue(attr, "12.3")).isEqualTo(TextValue("12.3"))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to TextType")
    }

    @Test
    internal fun wrapTextListTest() {
        val attr = EntityTypeManager().createAttribute("attr", TextListType::class) {
            reader(AttributeReaderWithFunction { _, _ -> TextListValue(listOf()) })
            writer(AttributeWriterWithFunction { _, _ -> Unit })
        }
        assertThat(wrapAValue(attr, listOf("11.1"))).isEqualTo(TextListValue(listOf(TextValue("11.1"))))
        assertThat(wrapAValue(attr, listOf("12.3"))).isEqualTo(TextListValue(listOf(TextValue("12.3"))))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to TextListType")
    }

    abstract class NewType : LongType(1, false)
    class NewValue : NewType()

    @Test
    internal fun wrapUnknownTypeTest() {
        val attr = EntityTypeManager().createAttribute("attr", NewType::class) {
            reader(AttributeReaderWithFunction { _, _ -> NewValue() })
            writer(AttributeWriterWithFunction { _, _ -> Unit })
        }
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Unexpected type<${NewType::class.simpleName}>")
    }

    @Test
    internal fun wrapBooleanListTest() {
        val attr = EntityTypeManager().createAttribute("attr", BooleanListType::class)
        @Suppress("BooleanLiteralArgument")
        assertThat(wrapAValue(attr, listOf(true, false)))
            .isEqualTo(BooleanListValue(listOf(BooleanValue(true), BooleanValue(false))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to BooleanListType")
    }

    @Test
    internal fun wrapLongListTest() {
        val attr = EntityTypeManager().createAttribute("attr", LongListType::class)
        assertThat(wrapAValue(attr, listOf(111L, 123L)))
            .isEqualTo(LongListValue(listOf(LongValue(111L), LongValue(123L))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to LongListType")
    }

    @Test
    internal fun wrapDoubleListTest() {
        val attr = EntityTypeManager().createAttribute("attr", DoubleListType::class)
        assertThat(wrapAValue(attr, listOf(11.1, 12.3)))
            .isEqualTo(DoubleListValue(listOf(DoubleValue(11.1), DoubleValue(12.3))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to DoubleListType")
    }

    @Test
    internal fun wrapStringListTest() {
        val attr = EntityTypeManager().createAttribute("attr", StringListType::class)
        assertThat(wrapAValue(attr, listOf("11.1", "12.3")))
            .isEqualTo(StringListValue(listOf(StringValue("11.1"), StringValue("12.3"))))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to StringListType")
    }

    @Test
    internal fun wrapDateListTest() {
        val attr = EntityTypeManager().createAttribute("attr", DateListType::class)
        val now1 = DateTime()
        val now2 = DateTime()
        assertThat(wrapAValue(attr, listOf(now1, now2)))
            .isEqualTo(DateListValue(listOf(DateValue(now1), DateValue(now2))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to DateListType")
    }

    @Test
    internal fun wrapDateTimeListTest() {
        val attr = EntityTypeManager().createAttribute("attr", DateTimeListType::class)
        val now1 = DateTime()
        val now2 = DateTime()
        assertThat(wrapAValue(attr, listOf(now1, now2)))
            .isEqualTo(DateTimeListValue(listOf(DateTimeValue(now1), DateTimeValue(now2))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to DateTimeListType")
    }

    @Test
    internal fun wrapAttributeListTest() {
        val entityTypeManager = EntityTypeManager()
        val attr = entityTypeManager.createAttribute("attr", AttributeListType::class)
        val attr1 = entityTypeManager.createAttribute("attr1", StringType::class)
        val attrMap = mapOf(
            "test1" to AttributeAsCell(attr1, StringValue("123")),
            "test2" to AttributeAsCell(attr1, StringValue("234"))
        )
        assertThat(wrapAValue(attr, attrMap))
            .isEqualTo(AttributeListValue(attrMap))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(DatabaseException::class.java)
            .hasMessageContaining("Can not wrap value to AttributeListType")
    }
}
