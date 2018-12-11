import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

object CliOptions {
    data class OptDescription(
        val LONG: String,
        val SHORT: Char,
        val DESCRIPTION: String
    )

    val OPT_HELP = OptDescription("help", 'h', "show this help")
    val OPT_SHEET = OptDescription("dataSheet", 's', "excel dataSheet to import")

    val options: Options
        get() {
            val cli = Options()
            cli.addOption(OPT_HELP.SHORT.toString(), OPT_HELP.LONG, false, "show this help")
            cli.addOption(
                Option.builder(OPT_SHEET.SHORT.toString())
                    .hasArg()
                    .longOpt(OPT_SHEET.LONG)
                    .desc(OPT_SHEET.DESCRIPTION)
                    .build()
            )
            return cli
        }
}