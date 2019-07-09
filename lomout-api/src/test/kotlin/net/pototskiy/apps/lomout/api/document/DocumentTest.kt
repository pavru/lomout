package net.pototskiy.apps.lomout.api.document

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

@Suppress("MagicNumber")
internal class DocumentTest {
    private lateinit var documents: Documents

    @BeforeEach
    internal fun setUp() {
        documents = Documents("lomout_test")
    }

    @AfterEach
    internal fun tearDown() {
        documents.close()
    }

    @Test
    fun setGetAttributeTest() {
        val now = LocalDateTime.now()
        val doc = DocTypeOne(
            "test1",
            1L,
            listOf(now, now.plusDays(1), now.plusDays(2))
        )
        assertThat(doc.string).isEqualTo("test1")
        assertThat(doc.getAttribute("string")).isEqualTo("test1")
        assertThat(doc.long).isEqualTo(1L)
        assertThat(doc.getAttribute("long")).isEqualTo(1L)
        assertThat(doc.list)
            .hasSize(3)
            .containsAll(listOf(now, now.plusDays(1), now.plusDays(2)))
        assertThat(doc.getAttribute("list") as List<*>)
            .hasSize(3)
            .containsAll(listOf(now, now.plusDays(1), now.plusDays(2)))
        doc.setAttribute("string", "test2")
        doc.setAttribute("long", 2L)
        doc.setAttribute("list", listOf(now.plusDays(1), now.plusDays(2), now.plusDays(3)))
        assertThat(doc.string).isEqualTo("test2")
        assertThat(doc.getAttribute("string")).isEqualTo("test2")
        assertThat(doc.long).isEqualTo(2L)
        assertThat(doc.getAttribute("long")).isEqualTo(2L)
        assertThat(doc.list)
            .hasSize(3)
            .containsAll(listOf(now.plusDays(1), now.plusDays(2), now.plusDays(3)))
        assertThat(doc.getAttribute("list") as List<*>)
            .hasSize(3)
            .containsAll(listOf(now.plusDays(1), now.plusDays(2), now.plusDays(3)))
        @Suppress("GraziInspection")
        assertThatThrownBy {
            doc.setAttribute("string", 1L)
        }.isInstanceOf(DocumentException::class.java)
            .hasMessageContaining("Try to set value of 'kotlin.Long' to attribute of 'kotlin.String'.")
        @Suppress("GraziInspection")
        assertThatThrownBy {
            doc.setAttribute(DocTypeOne::long.toAttribute(), "2L")
        }.isInstanceOf(DocumentException::class.java)
            .hasMessageContaining("Try to set value of 'kotlin.String' to attribute of 'kotlin.Long'.")
        assertThatThrownBy {
            doc.setAttribute("list", emptyMap<String, String>())
        }.isInstanceOf(DocumentException::class.java)
            .hasMessageContaining(
                "Try to set value of 'kotlin.collections.EmptyMap' to attribute of " +
                        "'kotlin.collections.List<java.time.LocalDateTime>'."
            )
        @Suppress("GraziInspection")
        assertThatThrownBy {
            doc.setAttribute("list", listOf(LocalDate.now()))
        }.isInstanceOf(DocumentException::class.java)
            .hasMessageContaining(
                "Try to set value of 'java.util.Collections.SingletonList<java.time.LocalDate>' " +
                        "to attribute of 'kotlin.collections.List<java.time.LocalDateTime>'."
            )
        doc.setAttribute("nullableValue", null)
        assertThatThrownBy {
            doc.setAttribute("long", null)
        }.isInstanceOf(DocumentException::class.java)
            .hasMessageContaining("Attribute 'long' of 'DocTypeOne' is not nullable.")
    }

    @Test
    internal fun statusTest() {
        val now = LocalDateTime.now()
        var documents = Documents("lomout_test")
        val timestampOne = Documents.timestamp
        val doc =
            DocTypeOne("test1", 1L, listOf(now, now.plusDays(1)))
        assertThat(doc.removed).isEqualTo(false)
        assertThat(doc.createTime).isEqualTo(timestampOne)
        assertThat(doc.updateTime).isEqualTo(timestampOne)
        assertThat(doc.toucheTime).isEqualTo(timestampOne)
        assertThat(doc.removeTime).isNull()
        documents.close()
        runBlocking { delay(1000) }
        documents = Documents("lomout_test")
        val timestampTwo = Documents.timestamp
        doc.markUpdated()
        assertThat(doc.removed).isEqualTo(false)
        assertThat(doc.createTime).isEqualTo(timestampOne)
        assertThat(doc.updateTime).isEqualTo(timestampTwo)
        assertThat(doc.toucheTime).isEqualTo(timestampTwo)
        assertThat(doc.removeTime).isNull()
        documents.close()
        runBlocking { delay(1000) }
        documents = Documents("lomout_test")
        val timestampThree = Documents.timestamp
        doc.touch()
        assertThat(doc.removed).isEqualTo(false)
        assertThat(doc.createTime).isEqualTo(timestampOne)
        assertThat(doc.updateTime).isEqualTo(timestampTwo)
        assertThat(doc.toucheTime).isEqualTo(timestampThree)
        assertThat(doc.removeTime).isNull()
        documents.close()
        runBlocking { delay(1000) }
        documents = Documents("lomout_test")
        val timestampFour = Documents.timestamp
        doc.markRemoved()
        assertThat(doc.removed).isEqualTo(true)
        assertThat(doc.createTime).isEqualTo(timestampOne)
        assertThat(doc.updateTime).isEqualTo(timestampTwo)
        assertThat(doc.toucheTime).isEqualTo(timestampThree)
        @Suppress("UsePropertyAccessSyntax")
        assertThat(doc.removeTime).isNotNull()
        assertThat(doc.removeTime).isEqualTo(timestampFour)
        documents.close()
        runBlocking { delay(1000) }
        @Suppress("UNUSED_VALUE")
        documents = Documents("lomout_test")
        val timestampFive = Documents.timestamp
        doc.touch()
        assertThat(doc.removed).isEqualTo(false)
        assertThat(doc.createTime).isEqualTo(timestampOne)
        assertThat(doc.updateTime).isEqualTo(timestampTwo)
        assertThat(doc.toucheTime).isEqualTo(timestampFive)
        assertThat(doc.removeTime).isNull()
    }

    @Test
    internal fun metadataNoCompanionObjectTest() {
        assertThatThrownBy {
            DocTypeTwo("")
        }.isInstanceOf(DocumentException::class.java)
            .hasMessageContaining(
                "Document type '${DocTypeTwo::class.qualifiedName}' is not well-defined, " +
                        "there is no companion object of '${DocumentMetadata::class.qualifiedName}'."
            )
    }

    @Test
    internal fun metadataWrongCompanionObjectTest() {
        assertThatThrownBy {
            DocTypeThree("")
        }.isInstanceOf(DocumentException::class.java)
            .hasMessageContaining(
                "Document type '${DocTypeThree::class.qualifiedName}' is not well-defined, " +
                        "there is no companion object of '${DocumentMetadata::class.qualifiedName}'."
            )
    }

    @Test
    internal fun metadataWrongDocTypeTest() {
        @Suppress("UsePropertyAccessSyntax")
        assertThat(DocTypeFive::class.documentMetadata).isNotNull()
        assertThatThrownBy {
            DocTypeFive("")
        }.isInstanceOf(DocumentException::class.java)
            .hasMessageContaining(
                "Wrong document metadata. It must be metadata of '${DocTypeFive::class.qualifiedName}' and" +
                        " not of '${DocTypeFour::class.qualifiedName}'"
            )
    }

    @Test
    internal fun metadataWrongAttributeTypeTest() {
        assertThatThrownBy {
            DocTypeSix(emptyMap())
        }.isInstanceOf(ExceptionInInitializerError::class.java)
            .hasCauseInstanceOf(DocumentException::class.java)
    }

    @Test
    internal fun metadataAttributesTest() {
        val metadata = DocTypeFour::class.documentMetadata
        assertThat(metadata.attributes).hasSize(4)
        assertThat(metadata.attributes.values.map { it.name })
            .containsAll(listOf("string", "long", "list", "nullableValue"))
        assertThat(metadata.attributes.getValue("string").isKey).isEqualTo(true)
        assertThat(metadata.attributes.getValue("string").isNullable).isEqualTo(false)
        assertThat(metadata.attributes.getValue("string").indexAnnotations).hasSize(1)
        assertThat(metadata.attributes.getValue("string").klass).isEqualTo(String::class)
        assertThat(metadata.attributes.getValue("string").typeName).isEqualTo(String::class.qualifiedName)
        assertThat(metadata.attributes.getValue("long").indexAnnotations).hasSize(2)
        assertThat(metadata.attributes.getValue("long").typeName).isEqualTo(Long::class.qualifiedName)
        assertThat(metadata.attributes.getValue("nullableValue").isNullable).isEqualTo(true)
        assertThat(metadata.attributes.getValue("list").klass).isEqualTo(List::class)
        assertThat(metadata.attributes.getValue("list").listParameter).isEqualTo(LocalDateTime::class)
        assertThat(metadata.attributes.getValue("list").annotations).hasSize(1)
        assertThat(metadata.attributes.getValue("list").annotations[0]).isInstanceOf(Builder::class.java)
    }

    data class DocTypeOne(
        var string: String,
        var long: Long,
        var list: List<LocalDateTime>,
        var nullableValue: Long? = null
    ) : Document() {
        companion object : DocumentMetadata(DocTypeOne::class)
    }

    data class DocTypeTwo(
        var string: String
    ) : Document()

    data class DocTypeThree(
        var string: String
    ) : Document() {
        companion object {
            @Suppress("unused")
            const val i = 1
        }
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.PROPERTY)
    @ExtraAttributeData
    annotation class Builder

    data class DocTypeFour(
        @Key
        @Index("index_string_long")
        var string: String,
        @Indexes(
            [
                Index("index_long"),
                Index("index_string_long")
            ]
        )
        var long: Long,
        @Builder
        var list: List<LocalDateTime>,
        var nullableValue: Long? = null
    ) : Document() {
        companion object : DocumentMetadata(DocTypeFour::class)
    }

    data class DocTypeFive(
        var string: String
    ) : Document() {
        companion object : DocumentMetadata(DocTypeFour::class)
    }

    data class DocTypeSix(
        var string: Map<String, String>
    ) : Document() {
        companion object : DocumentMetadata(DocTypeSix::class)
    }
}
