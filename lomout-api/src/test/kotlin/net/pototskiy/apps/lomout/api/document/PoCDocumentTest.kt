package net.pototskiy.apps.lomout.api.document

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PoCDocumentTest {
    @Test
    internal fun createDocTes() {
        val doc1 = Product("1234", listOf("cat1", "cat2"))
        assertThat(doc1.documentMetadata.attributes).hasSize(4)
        assertThat(doc1.documentMetadata.attributes.keys)
            .hasSize(4)
            .containsAll(listOf("sku", "categories", "name", "description"))
        assertThat(doc1.documentMetadata.attributes["sku"]?.annotations).isEmpty()
        assertThat(doc1.documentMetadata.attributes["description"]?.annotations).hasSize(1)
        val builder = doc1.documentMetadata.attributes["description"]?.annotationIndex?.get(Builder::class)
        assertThat(builder).isNotNull
        builder as Builder
        assertThat(builder.param).isEqualTo(101)
        val doc2 = Product("1235", listOf("cat1", "cat3"))
        assertThat(doc2.documentMetadata).isSameAs(doc1.documentMetadata)
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.PROPERTY)
    @ExtraAttributeData
    annotation class Builder(val param: Int)

    @Suppress("CanBePrimaryConstructorProperty", "unused")
    class Product(
        sku: String,
        categories: List<String>
    ) : Document() {
        @Key
        @Index("sku_name")
        var sku: String = sku
        var categories: List<String> = categories
        @Index("sku_name", Index.SortOrder.DESC)
        var name: String = ""
        @Builder(101)
        var description: String? = null

        companion object : DocumentMetadata(Product::class)
    }
}