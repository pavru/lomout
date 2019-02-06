package net.pototskiy.apps.magemediation

import com.beust.jcommander.Parameter

object Args {
    @Parameter(
        names = ["--help"],
        description = "show help",
        help = true
    )
    var help: Boolean = false
    @Parameter(
        names = ["-c", "--conf"],
        description = "specify configuration file",
        arity = 1
    )
    var configFile: String = "config/config.conf.kts"
    @Parameter(
        names = ["-l", "--log-level"],
        description = "log level: fatal, error, warn, info, trace",
        arity = 1
    )
    var logLevel: String = "warn"
}
