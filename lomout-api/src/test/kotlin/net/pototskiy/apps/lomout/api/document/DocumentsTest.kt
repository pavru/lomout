package net.pototskiy.apps.lomout.api.document

import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initDateValue
import net.pototskiy.apps.lomout.api.document.SupportAttributeType.initStringValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.litote.kmongo.eq
import org.litote.kmongo.gt
import java.time.LocalDate

@Suppress("MagicNumber")
internal class DocumentsTest {
    private lateinit var documents: Documents

    @BeforeEach
    internal fun beforeEach() {
        documents = Documents("lomout_test")
        val collection = documents.getCollection(TestDoc::class)
        collection.deleteMany(org.bson.Document())
    }

    @AfterEach
    internal fun afterEach() {
        documents.close()
    }

    @Test
    fun insertTest() {
        documents.deleteMany(TestDoc::class, TestDoc::testId eq this::insertTest.name)
        for (i in 1..100) {
            val doc = TestDoc(
                "insertTest",
                i.toString(),
                i.toLong(),
                doubleAttr = if (i % 2 == 0) i.toDouble() / 10.0 else null
            )
            documents.insert(doc)
        }
        assertThat(
            documents.getCollection(TestDoc::class).countDocuments(
                TestDoc::testId eq this::insertTest.name
            )
        )
            .isEqualTo(100L)
        for (i in 1..100) {
            val doc = documents.getOne(
                TestDoc::class,
                TestDoc::stringAttr eq i.toString(),
                TestDoc::testId eq "insertTest"
            ) as? TestDoc
            assertThat(doc).isNotNull
            doc as TestDoc
            assertThat(doc.stringAttr).isEqualTo(i.toString())
            assertThat(doc.longAttr).isEqualTo(i.toLong())
            if (i % 2 == 0) {
                assertThat(doc.doubleAttr).isEqualTo(i.toDouble() / 10.0)
            } else {
                assertThat(doc.doubleAttr).isNull()
            }
        }
    }

    @Test
    fun updateTest() {
        documents.deleteMany(TestDoc::class, TestDoc::testId eq this::updateTest.name)
        documents.insert(
            TestDoc(
                this::updateTest.name,
                "updateTest",
                10L,
                0.1111
            )
        )
        val doc = documents.getOne(
            TestDoc::class,
            TestDoc::stringAttr eq "updateTest",
            TestDoc::testId eq this::updateTest.name
        )
        assertThat(doc).isNotNull
        doc as TestDoc
        assertThat(doc.longAttr).isEqualTo(10L)
        assertThat(doc.doubleAttr).isEqualTo(.1111)
        val id = doc._id
        doc.stringAttr = "updateTestUpdated"
        doc.doubleAttr = null
        documents.update(doc)
        val updatedDoc = documents.getOne(TestDoc::class, id)
        assertThat(updatedDoc).isNotNull
        updatedDoc as TestDoc
        assertThat(updatedDoc.stringAttr).isEqualTo("updateTestUpdated")
        assertThat(updatedDoc.longAttr).isEqualTo(10L)
        assertThat(updatedDoc.doubleAttr).isNull()
    }

    @Test
    fun deleteTest() {
        documents.deleteMany(TestDoc::class, TestDoc::testId eq this::deleteTest.name)
        val docs = mutableListOf<TestDoc>()
        for (i in 1..10) {
            documents.insert(
                TestDoc(
                    this::deleteTest.name,
                    "${this::deleteTest.name}-$i",
                    i.toLong()
                ).also { docs.add(it) })
        }
        assertThat(
            documents.getCollection(TestDoc::class).countDocuments(
                TestDoc::testId eq this::deleteTest.name
            )
        )
            .isEqualTo(10)
        for (i in 0 until 10 step 2) {
            documents.deleteOne(docs[i])
        }
        assertThat(
            documents.getCollection(TestDoc::class).countDocuments(
                TestDoc::testId eq this::deleteTest.name
            )
        )
            .isEqualTo(5)
        for (i in 1..10 step 2) {
            val doc = documents.getOne(TestDoc::class, docs[i]._id)
            assertThat(doc).isNotNull
        }
        for (i in 1 until 10 step 2) {
            documents.deleteOne(TestDoc::class, docs[i]._id)
        }
        assertThat(
            documents.getCollection(TestDoc::class).countDocuments(
                TestDoc::testId eq this::deleteTest.name
            )
        )
            .isEqualTo(0)
    }

    @Test
    fun getOneTest() {
        val testId = this::getOneTest.name
        documents.deleteMany(TestDoc::class, TestDoc::testId eq testId)
        val docs = mutableListOf<TestDoc>()
        for (i in 1..3) documents.insert(
            TestDoc(
                testId,
                "$testId-$i",
                i.toLong()
            ).also { docs.add(it) })
        var doc = documents.getOne(TestDoc::class, docs[1]._id)
        assertThat(doc).isNotNull
        doc as TestDoc
        assertThat(doc.testId).isEqualTo(testId)
        assertThat(doc.stringAttr).isEqualTo("$testId-2")
        assertThat(doc.longAttr).isEqualTo(2L)
        doc = documents.getOne(
            TestDoc::class,
            TestDoc::stringAttr eq "$testId-3",
            TestDoc::longAttr eq 3L
        )
        assertThat(doc).isNotNull
        doc as TestDoc
        assertThat(doc.testId).isEqualTo(testId)
        assertThat(doc.stringAttr).isEqualTo("$testId-3")
        assertThat(doc.longAttr).isEqualTo(3L)
        doc = documents.getOne(
            TestDoc::class,
            TestDoc::stringAttr eq "$testId-3",
            TestDoc::longAttr eq 4L
        )
        assertThat(doc).isNull()
    }

    @Test
    fun getManyTest() {
        val testId = this::getManyTest.name
        documents.deleteMany(TestDoc::class, TestDoc::testId eq testId)
        val docs = Array(10) {
            TestDoc(
                testId,
                "$testId-${it + 1}",
                (it + 1).toLong()
            )
        }
        docs.forEach { documents.insert(it) }
        var doc = documents.getMany(TestDoc::class)
        assertThat(doc).hasSize(docs.size).containsAll(docs.toList())
        doc = documents.getMany(
            TestDoc::class,
            TestDoc::testId eq testId,
            TestDoc::longAttr gt 5L
        )
        assertThat(doc).hasSize(5).containsAll(docs.slice(5..9))
    }

    @Test
    fun getManyIDTest() {
        val testId = this::getManyIDTest.name
        val docs = Array(10) {
            TestDoc(
                testId,
                "$testId-${it + 1}",
                (it + 1).toLong()
            )
        }
        docs.forEach { documents.insert(it) }
        var ids = documents.getManyID(TestDoc::class)
        assertThat(ids).hasSize(docs.size).containsAll(docs.map { it._id })
        ids = documents.getManyID(TestDoc::class, 2, 0)
        assertThat(ids).hasSize(2).containsAll(docs.slice(0..1).map { it._id })
        ids = documents.getManyID(TestDoc::class, 2, 3)
        assertThat(ids).hasSize(2).containsAll(docs.slice(6..7).map { it._id })
    }

    @Test
    fun getCollectionTest() {
        assertThat(documents.getCollection(TestDoc::class)).isNotNull
        assertThat(TestDoc("", "", 0L))
    }

    @Test
    internal fun openClassTest() {
        val doc = DocTypeOne().apply {
            attr1 = "test-value-1"
        }
        documents.insert(doc)
        val loaded = documents.getOne(DocTypeOne::class, doc._id) as DocTypeOne
        assertThat(loaded.attr1).isEqualTo("test-value-1")
    }

    data class TestDoc(
        var testId: String,
        @Key
        var stringAttr: String,
        @Indexes(
            [
                Index("index_longAttr"),
                Index("index_longAttr_doubleAttr")
            ]
        )
        var longAttr: Long,
        @Index("index_longAttr_doubleAttr", Index.SortOrder.DESC)
        var doubleAttr: Double? = null,
        @Index("index_dupDouble", Index.SortOrder.DESC)
        var dupDouble: Double? = null,
        @Key
        var dateAttr: LocalDate = initDateValue
    ) : Document() {
        @Suppress("unused")
        @Transient
        var notAnAttribute: Boolean = true

        companion object : DocumentMetadata(TestDoc::class)
    }

    open class DocTypeOne : Document() {
        var attr1: String = initStringValue

        companion object : DocumentMetadata(DocTypeOne::class)
    }

    @Suppress("unused")
    class DocTypeTwo : DocTypeOne() {
        var attr2: String = initStringValue

        companion object : DocumentMetadata(DocTypeTwo::class)
    }
}