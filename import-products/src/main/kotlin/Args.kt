import com.beust.jcommander.Parameter

object Args {
    @Parameter(
        description = "excel file to import"
    )
    var files: List<String> = mutableListOf()
    @Parameter(
        names = ["--help"],
        description = "show help",
        help = true
    )
    var help: Boolean = false
    @Parameter(
        names = ["-c", "--configuration"],
        description = "specify import configuration file",
        arity = 1
    )
    var configFile: String = "config.yml"
    @Parameter(
        names = ["-l", "--log-level"],
        description = "log level: fatal, error, warn, info",
        arity = 1
    )
    var logLevel: String = "warn"
}