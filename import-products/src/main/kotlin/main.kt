
import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import database.initDatabase
import importer.Importer
import org.apache.log4j.BasicConfigurator
import kotlin.contracts.ExperimentalContracts

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
    Importer().prepareImportFiles()
}

