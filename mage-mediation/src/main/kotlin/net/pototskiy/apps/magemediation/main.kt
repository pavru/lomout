package net.pototskiy.apps.magemediation

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import net.pototskiy.apps.magemediation.database.initDatabase
import net.pototskiy.apps.magemediation.loader.DataLoader
import net.pototskiy.apps.magemediation.source.mage.MageProductLoader
import org.apache.log4j.BasicConfigurator
import org.joda.time.DateTime
import kotlin.contracts.ExperimentalContracts
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sign

const val LOG_NAME = "Import"
val IMPORT_DATETIME = DateTime()

@ExperimentalContracts
fun main(args: Array<String>) {
    BasicConfigurator.configure()
    val jCommander = JCommander.Builder()
        .addObject(Args)
        .build()
    try {
        jCommander.parse(*args)
    } catch (e: ParameterException) {
        println(e.message)
        System.exit(1)
    }
    if (Args.help || Args.files.isEmpty()) {
        jCommander.usage()
    }
    initDatabase()
    DataLoader.load()
}

