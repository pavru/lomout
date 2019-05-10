package net.pototskiy.apps.lomout.jcommander

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters

/**
 * CLI main command to process config and data files
 *
 * @property configFile String
 * @property logLevel String
 * @property sqlLogLevel String
 * @property scriptCacheDir String
 * @property doNotUseScriptCache Boolean
 */
@Parameters(
    commandNames = ["--process"],
    commandDescription = "Process files"
)
class CommandMain {
    @Parameter(
        description = "configuration file",
        required = true
    )
    var configFile: MutableList<String> = mutableListOf()
    @Parameter(
        names = ["-l", "--log-level"],
        description = "log level: fatal, error, warn, info, trace",
        arity = 1
    )
    var logLevel: String = "warn"
    @Parameter(
        names = ["-s", "--sql-log-level"],
        description = "log level: fatal, error, warn, info, trace",
        arity = 1
    )
    var sqlLogLevel: String = "error"
    @Parameter(
        names = ["--script-cache-dir", "-d"],
        description = "Directory where cached script store",
        arity = 1
    )
    var scriptCacheDir: String = "tmp/config/cache"
    @Parameter(
        names = ["--do-not-use-cache", "-n"],
        description = "Do not use cached script",
        arity = 0
    )
    var doNotUseScriptCache: Boolean = false
}
