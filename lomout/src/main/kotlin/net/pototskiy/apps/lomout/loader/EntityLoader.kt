package net.pototskiy.apps.lomout.loader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.pototskiy.apps.lomout.api.AppConfigException
import net.pototskiy.apps.lomout.api.AppDataException
import net.pototskiy.apps.lomout.api.AppException
import net.pototskiy.apps.lomout.api.DomainExceptionPlace
import net.pototskiy.apps.lomout.api.LOADER_LOG_NAME
import net.pototskiy.apps.lomout.api.badPlace
import net.pototskiy.apps.lomout.api.config.EmptyRowBehavior
import net.pototskiy.apps.lomout.api.config.loader.FieldSet
import net.pototskiy.apps.lomout.api.config.loader.Load
import net.pototskiy.apps.lomout.api.document.DocumentMetadata.Attribute
import net.pototskiy.apps.lomout.api.entity.EntityRepositoryInterface
import net.pototskiy.apps.lomout.api.entity.reader
import net.pototskiy.apps.lomout.api.plugable.AttributeReader
import net.pototskiy.apps.lomout.api.plus
import net.pototskiy.apps.lomout.api.source.Field
import net.pototskiy.apps.lomout.api.source.FieldAttributeMap
import net.pototskiy.apps.lomout.api.source.workbook.Cell
import net.pototskiy.apps.lomout.api.source.workbook.CellType
import net.pototskiy.apps.lomout.api.source.workbook.Row
import net.pototskiy.apps.lomout.api.source.workbook.Sheet
import org.apache.logging.log4j.LogManager
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class EntityLoader(
    private val repository: EntityRepositoryInterface,
    private val loadConfig: Load,
    private val emptyRowBehavior: EmptyRowBehavior,
    private val sheet: Sheet
) {

    private val log = LogManager.getLogger(LOADER_LOG_NAME)
    var processedRows = 0L
    private val updateChanel = Channel<UpdaterData>(CHANEL_CAPACITY)

    private lateinit var updater: EntityUpdater
    private val extraData = mutableMapOf<String, Map<Attribute, Any>>()
    private var fieldSets = loadConfig.fieldSets
    private var eType = loadConfig.entity

    fun load() = runBlocking {
        val updaterJob = launch(Dispatchers.IO) {
            updateChanel.consumeEach { updateEntity(it) }
        }
        updater = EntityUpdater(repository, eType)
        processRows()
        updateChanel.close()
        updaterJob.join()
        repository.markEntitiesAsRemoved(eType)
        repository.updateAbsentDays(eType)
        repository.removeOldEntities(eType, loadConfig.maxAbsentDays)
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun processRows() {
        loop@ for (row in sheet) {
            processedRows++
            if (row.rowNum == loadConfig.headersRow || row.rowNum < loadConfig.rowsToSkip) continue
            when (checkEmptyRow(row, emptyRowBehavior)) {
                EmptyRowTestResult.STOP -> break@loop
                EmptyRowTestResult.SKIP -> continue@loop
                EmptyRowTestResult.PROCESS -> { // assemble row }
                }
            }
            try {
                processRow(row)
            } catch (e: AppException) {
                rowException(badPlace(row), e)
                continue
            } catch (e: Exception) {
                rowException(badPlace(row), e)
                continue
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun updateEntity(data: UpdaterData) {
        try {
            updater.update(data.data)
        } catch (e: AppException) {
            rowException(data.place, e)
        } catch (e: Exception) {
            rowException(data.place, e)
        }
    }

    private suspend fun processRow(row: Row) {
        val rowFiledSet = findRowFieldSet(row)
        if (rowFiledSet.mainSet) {
            val data = getData(row, rowFiledSet.fieldToAttr).toMutableMap()
            plusAdditionalData(data)
            validateKeyFieldData(data, rowFiledSet.fieldToAttr)
            updateChanel.send(UpdaterData(row, data, badPlace(row) + loadConfig.entity))
        } else {
            val data = getData(row, rowFiledSet.fieldToAttr)
            extraData[rowFiledSet.name] = data
        }
    }

    private fun rowException(place: DomainExceptionPlace, e: Exception) {
        when (e) {
            is AppConfigException -> log.error("{} {}", e.message, e.place.placeInfo())
            is AppDataException -> log.error("{} {}", e.message, e.place.placeInfo())
            else -> {
                log.error("{} {}", e.message, place.placeInfo())
                log.trace("Internal error: {}", e.message)
                log.trace("Thread: {}", Thread.currentThread().name)
                log.trace("Exception: ", e)
            }
        }
    }

    private fun plusAdditionalData(data: MutableMap<Attribute, Any>) =
        extraData.forEach { (_, gData) -> data.putAll(gData) }

    private fun checkEmptyRow(
        row: Row,
        emptyRowBehavior: EmptyRowBehavior
    ): EmptyRowTestResult {
        return if (row.countCell() == 0 || row.all { it == null || it.cellType == CellType.BLANK }) {
            when (emptyRowBehavior) {
                EmptyRowBehavior.STOP -> {
                    log.info(
                        "Stop workbook processing according to configuration:{})",
                        row.sheet.workbook.name,
                        row.sheet.name,
                        row.rowNum + 1
                    )
                    EmptyRowTestResult.STOP
                }
                EmptyRowBehavior.IGNORE -> {
                    log.info(
                        "Skip empty row according to configuration({}:{}:{})",
                        row.sheet.workbook.name,
                        row.sheet.name,
                        row.rowNum + 1
                    )
                    EmptyRowTestResult.SKIP
                }
            }
        } else {
            EmptyRowTestResult.PROCESS
        }
    }

    private fun validateKeyFieldData(
        data: Map<Attribute, Any?>,
        fields: FieldAttributeMap
    ) {
        val keyFields = fields.filter { it.value.isKey }
        keyFields.forEach { (_, attr) ->
            val v = data[attr]
            if (v == null || (v is String && v.isBlank())) {
                throw AppDataException(badPlace(attr), "Attribute is key but has no value.")
            }
        }
    }

    @Suppress("ComplexMethod", "ThrowsCount")
    private fun getData(row: Row, fields: FieldAttributeMap): Map<Attribute, Any> {
        val data: MutableMap<Attribute, Any> = mutableMapOf()

        fields.forEach { (field, attr) ->
            val cell = row[field.column]
                ?: if (attr.isNullable) row.getOrEmptyCell(field.column) else null
                    ?: throw AppDataException(
                        badPlace(row) + field + attr,
                        "There is no requested cell."
                    )
            testFieldRegex(field, cell)
            @Suppress("UNCHECKED_CAST")
            (attr.reader as AttributeReader<Any?>).read(attr, cell).also {
                if (it == null && (!attr.isNullable || attr.isKey)) {
                    throw AppDataException(
                        badPlace(attr) + field + cell,
                        "Attribute is not nullable and cannot be null."
                    )
                } else if (it != null) {
                    data[attr] = it
                }
            }
        }
        return data
    }

    private fun testFieldRegex(field: Field, cell: Cell) {
        if (!field.isMatchToPattern(cell.asString())) {
            throw AppDataException(badPlace(field) + cell, "Field does not match required regular expression.")
        }
    }

    private fun findRowFieldSet(row: Row): FieldSet =
        (if (fieldSets.count() > 1) {
            var fittedSet: FieldSet? = null
            for (set in fieldSets) {
                if (testRowAgainstFieldSet(row, set)) {
                    fittedSet = set
                    break
                }
            }
            fittedSet
        } else {
            null
        } ?: fieldSets.mainSet)

    private fun testRowAgainstFieldSet(row: Row, set: FieldSet): Boolean {
        var fit = true
        for (field in set.fields.filter { field -> field.regex != null }) {
            val cell = row[field.column]
                ?: throw AppDataException(
                    badPlace(field) + row,
                    "The cell does not exist, but it's required for row classification."
                )
            if (!field.isMatchToPattern(cell.asString())) fit = false
        }
        return fit
    }

    private data class UpdaterData(
        val row: Row,
        val data: Map<Attribute, Any>,
        val place: DomainExceptionPlace
    )

    companion object {
        private const val CHANEL_CAPACITY = 500
    }
}
