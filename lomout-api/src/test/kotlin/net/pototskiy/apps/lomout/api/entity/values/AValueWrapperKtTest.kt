package net.pototskiy.apps.lomout.api.entity.values

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.entity.AttributeAsCell
import net.pototskiy.apps.lomout.api.entity.AttributeReader
import net.pototskiy.apps.lomout.api.entity.AttributeReaderWithFunction
import net.pototskiy.apps.lomout.api.entity.AttributeWriter
import net.pototskiy.apps.lomout.api.entity.AttributeWriterWithFunction
import net.pototskiy.apps.lomout.api.entity.EntityTypeManagerImpl
import net.pototskiy.apps.lomout.api.entity.reader.defaultReaders
import net.pototskiy.apps.lomout.api.entity.type.ATTRIBUTELIST
import net.pototskiy.apps.lomout.api.entity.type.BOOLEAN
import net.pototskiy.apps.lomout.api.entity.type.BOOLEANLIST
import net.pototskiy.apps.lomout.api.entity.type.DATE
import net.pototskiy.apps.lomout.api.entity.type.DATELIST
import net.pototskiy.apps.lomout.api.entity.type.DATETIME
import net.pototskiy.apps.lomout.api.entity.type.DATETIMELIST
import net.pototskiy.apps.lomout.api.entity.type.DOUBLE
import net.pototskiy.apps.lomout.api.entity.type.DOUBLELIST
import net.pototskiy.apps.lomout.api.entity.type.LONG
import net.pototskiy.apps.lomout.api.entity.type.LONGLIST
import net.pototskiy.apps.lomout.api.entity.type.STRING
import net.pototskiy.apps.lomout.api.entity.type.STRINGLIST
import net.pototskiy.apps.lomout.api.entity.type.TEXT
import net.pototskiy.apps.lomout.api.entity.type.TEXTLIST
import net.pototskiy.apps.lomout.api.entity.writer.defaultWriters
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
        @Suppress("UNCHECKED_CAST")
        val attr = EntityTypeManagerImpl().createAttribute(
            "attr", BOOLEAN::class,
            builder = null,
            reader = defaultReaders[BOOLEAN::class] as AttributeReader<out BOOLEAN>,
            writer = defaultWriters[BOOLEAN::class] as AttributeWriter<out BOOLEAN>
        )
        assertThat(wrapAValue(attr, true)).isEqualTo(BOOLEAN(true))
        assertThat(wrapAValue(attr, false)).isEqualTo(BOOLEAN(false))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }

    @Test
    internal fun wrapLongTest() {
        val attr = EntityTypeManagerImpl().createAttribute("attr", LONG::class)
        assertThat(wrapAValue(attr, 111L)).isEqualTo(LONG(111L))
        assertThat(wrapAValue(attr, 123L)).isEqualTo(LONG(123L))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }

    @Test
    internal fun wrapDoubleTest() {
        val attr = EntityTypeManagerImpl().createAttribute("attr", DOUBLE::class)
        assertThat(wrapAValue(attr, 11.1)).isEqualTo(DOUBLE(11.1))
        assertThat(wrapAValue(attr, 12.3)).isEqualTo(DOUBLE(12.3))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }

    @Test
    internal fun wrapStringTest() {
        val attr = EntityTypeManagerImpl().createAttribute("attr", STRING::class)
        assertThat(wrapAValue(attr, "11.1")).isEqualTo(STRING("11.1"))
        assertThat(wrapAValue(attr, "12.3")).isEqualTo(STRING("12.3"))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }

    @Test
    internal fun wrapDateTest() {
        val attr = EntityTypeManagerImpl().createAttribute("attr", DATE::class)
        val now1 = DateTime()
        val now2 = DateTime()
        assertThat(wrapAValue(attr, now1)).isEqualTo(DATE(now1))
        assertThat(wrapAValue(attr, now2)).isEqualTo(DATE(now2))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }

    @Test
    internal fun wrapDateTimeTest() {
        val attr = EntityTypeManagerImpl().createAttribute("attr", DATETIME::class)
        val now1 = DateTime()
        val now2 = DateTime()
        assertThat(wrapAValue(attr, now1)).isEqualTo(DATETIME(now1))
        assertThat(wrapAValue(attr, now2)).isEqualTo(DATETIME(now2))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }

    @Test
    internal fun wrapTextTest() {
        val attr = EntityTypeManagerImpl().createAttribute("attr", TEXT::class)
        assertThat(wrapAValue(attr, "11.1")).isEqualTo(TEXT("11.1"))
        assertThat(wrapAValue(attr, "12.3")).isEqualTo(TEXT("12.3"))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }

    @Test
    internal fun wrapTextListTest() {
        val attr = EntityTypeManagerImpl().createAttribute(
            "attr", TEXTLIST::class,
            reader = AttributeReaderWithFunction { _, _ -> TEXTLIST(listOf()) },
            writer = AttributeWriterWithFunction { _, _ -> Unit }
        )
        assertThat(wrapAValue(attr, listOf("11.1"))).isEqualTo(TEXTLIST(listOf(TEXT("11.1"))))
        assertThat(wrapAValue(attr, listOf("12.3"))).isEqualTo(TEXTLIST(listOf(TEXT("12.3"))))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }

    @Test
    internal fun wrapBooleanListTest() {
        @Suppress("UNCHECKED_CAST")
        val attr = EntityTypeManagerImpl().createAttribute(
            "attr", BOOLEANLIST::class,
            builder = null,
            reader = defaultReaders[BOOLEANLIST::class] as AttributeReader<out BOOLEANLIST>,
            writer = defaultWriters[BOOLEANLIST::class] as AttributeWriter<out BOOLEANLIST>
        )
        @Suppress("BooleanLiteralArgument")
        assertThat(wrapAValue(attr, listOf(true, false)))
            .isEqualTo(BOOLEANLIST(listOf(BOOLEAN(true), BOOLEAN(false))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }

    @Test
    internal fun wrapLongListTest() {
        val attr = EntityTypeManagerImpl().createAttribute("attr", LONGLIST::class)
        assertThat(wrapAValue(attr, listOf(111L, 123L)))
            .isEqualTo(LONGLIST(listOf(LONG(111L), LONG(123L))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }

    @Test
    internal fun wrapDoubleListTest() {
        val attr = EntityTypeManagerImpl().createAttribute("attr", DOUBLELIST::class)
        assertThat(wrapAValue(attr, listOf(11.1, 12.3)))
            .isEqualTo(DOUBLELIST(listOf(DOUBLE(11.1), DOUBLE(12.3))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }

    @Test
    internal fun wrapStringListTest() {
        val attr = EntityTypeManagerImpl().createAttribute("attr", STRINGLIST::class)
        assertThat(wrapAValue(attr, listOf("11.1", "12.3")))
            .isEqualTo(STRINGLIST(listOf(STRING("11.1"), STRING("12.3"))))
        assertThatThrownBy { wrapAValue(attr, 1) }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }

    @Test
    internal fun wrapDateListTest() {
        val attr = EntityTypeManagerImpl().createAttribute("attr", DATELIST::class)
        val now1 = DateTime()
        val now2 = DateTime()
        assertThat(wrapAValue(attr, listOf(now1, now2)))
            .isEqualTo(DATELIST(listOf(DATE(now1), DATE(now2))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }

    @Test
    internal fun wrapDateTimeListTest() {
        val attr = EntityTypeManagerImpl().createAttribute("attr", DATETIMELIST::class)
        val now1 = DateTime()
        val now2 = DateTime()
        assertThat(wrapAValue(attr, listOf(now1, now2)))
            .isEqualTo(DATETIMELIST(listOf(DATETIME(now1), DATETIME(now2))))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }

    @Test
    internal fun wrapAttributeListTest() {
        val entityTypeManager = EntityTypeManagerImpl()
        val attr = entityTypeManager.createAttribute("attr", ATTRIBUTELIST::class)
        val attr1 = entityTypeManager.createAttribute("attr1", STRING::class)
        val attrMap = mapOf(
            "test1" to AttributeAsCell(attr1, STRING("123")),
            "test2" to AttributeAsCell(attr1, STRING("234"))
        )
        assertThat(wrapAValue(attr, attrMap))
            .isEqualTo(ATTRIBUTELIST(attrMap))
        assertThatThrownBy { wrapAValue(attr, "1") }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Cannot wrap value.")
    }
}
