package net.pototskiy.apps.lomout.api.entity

import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.database.EntityBooleans
import net.pototskiy.apps.lomout.api.database.EntityDateTimes
import net.pototskiy.apps.lomout.api.database.EntityDates
import net.pototskiy.apps.lomout.api.database.EntityDoubles
import net.pototskiy.apps.lomout.api.database.EntityLongs
import net.pototskiy.apps.lomout.api.database.EntityStrings
import net.pototskiy.apps.lomout.api.database.EntityTexts
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
import net.pototskiy.apps.lomout.api.entity.type.isList
import net.pototskiy.apps.lomout.api.entity.type.isMap
import net.pototskiy.apps.lomout.api.entity.type.isSingle
import net.pototskiy.apps.lomout.api.entity.type.isTypeOf
import net.pototskiy.apps.lomout.api.entity.type.table
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Suppress("MagicNumber", "TooManyFunctions")
@Execution(ExecutionMode.CONCURRENT)
internal class TypeTest {
    @Test
    internal fun sqlTypeTest() {
        assertThat(STRING("").table).isSameAs(EntityStrings)
        assertThat(STRING::class.table).isSameAs(EntityStrings)
        assertThatThrownBy { ATTRIBUTELIST(emptyMap()).table }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Type has no store table.")
        assertThatThrownBy { ATTRIBUTELIST::class.table }
            .isInstanceOf(AppDataException::class.java)
            .hasMessageContaining("Type has no store table.")
    }

    @Test
    internal fun isSingleTest() {
        assertThat(STRING::class.isSingle()).isTrue()
        assertThat(STRINGLIST::class.isSingle()).isFalse()
        assertThat(ATTRIBUTELIST::class.isSingle()).isFalse()
        assertThat(STRING("").isSingle()).isTrue()
        assertThat(STRINGLIST(emptyList()).isSingle()).isFalse()
        assertThat(ATTRIBUTELIST(emptyMap()).isSingle()).isFalse()
    }

    @Test
    internal fun isListTest() {
        assertThat(STRINGLIST::class.isList()).isTrue()
        assertThat(STRING::class.isList()).isFalse()
        assertThat(STRINGLIST(emptyList()).isList()).isTrue()
        assertThat(STRING("").isList()).isFalse()
    }

    @Test
    internal fun isMapTest() {
        assertThat(ATTRIBUTELIST::class.isMap()).isTrue()
        assertThat(STRING::class.isMap()).isFalse()
        assertThat(ATTRIBUTELIST(emptyMap()).isMap()).isTrue()
        assertThat(STRING("").isMap()).isFalse()
    }

    @Test
    internal fun isTypeOfTest() {
        assertThat(STRING::class.isTypeOf<STRING>()).isTrue()
        assertThat(STRING("").isTypeOf<STRING>()).isTrue()
        assertThat(STRING::class.isTypeOf<LONG>()).isFalse()
        assertThat(STRING("").isTypeOf<LONG>()).isFalse()
    }

    @Test
    internal fun booleanTypeTest() {
        assertThat(BOOLEAN::class.table).isSameAs(EntityBooleans)
        assertThat(BOOLEAN(true).table).isSameAs(EntityBooleans)
        assertThat(BOOLEAN(true) == BOOLEAN(true)).isTrue()
    }

    @Test
    internal fun longTypeTest() {
        assertThat(LONG::class.table).isSameAs(EntityLongs)
        assertThat(LONG(3L).table).isSameAs(EntityLongs)
        assertThat(LONG(3L) == LONG(3L)).isTrue()
    }

    @Test
    internal fun doubleTypeTest() {
        assertThat(DOUBLE::class.table).isSameAs(EntityDoubles)
        assertThat(DOUBLE(3.3).table).isSameAs(EntityDoubles)
        assertThat(DOUBLE(3.3) == DOUBLE(3.3)).isTrue()
    }

    @Test
    internal fun stringTypeTest() {
        assertThat(STRING::class.table).isSameAs(EntityStrings)
        assertThat(STRING("").table).isSameAs(EntityStrings)
        assertThat(STRING("test") == STRING("test")).isTrue()
    }

    @Test
    internal fun dateTypeTest() {
        assertThat(DATE::class.table).isSameAs(EntityDates)
        assertThat(DATE(DateTime()).table).isSameAs(EntityDates)
    }

    @Test
    internal fun dateTimeTypeTest() {
        assertThat(DATETIME::class.table).isSameAs(EntityDateTimes)
        assertThat(DATETIME(DateTime()).table).isSameAs(EntityDateTimes)
    }

    @Test
    internal fun textTypeTest() {
        assertThat(TEXT::class.table).isSameAs(EntityTexts)
        assertThat(TEXT("").table).isSameAs(EntityTexts)
        assertThat(TEXT("test") == TEXT("test")).isTrue()
    }

    @Test
    internal fun listTypesTest() {
        assertThat(BOOLEANLIST(emptyList()).table).isSameAs(EntityBooleans)
        assertThat(LONGLIST(emptyList()).table).isSameAs(EntityLongs)
        assertThat(DOUBLELIST(emptyList()).table).isSameAs(EntityDoubles)
        assertThat(STRINGLIST(emptyList()).table).isSameAs(EntityStrings)
        assertThat(TEXTLIST(emptyList()).table).isSameAs(EntityTexts)
        assertThat(DATELIST(emptyList()).table).isSameAs(EntityDates)
        assertThat((DATETIMELIST(emptyList())).table).isSameAs(EntityDateTimes)
        assertThat(BOOLEANLIST(emptyList()).toString()).isEqualTo("[]")
        assertThat(ATTRIBUTELIST(emptyMap()).toString()).isEqualTo("{}")
    }
}
