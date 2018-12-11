package configuration

import Args
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.nio.file.Files
import java.nio.file.Paths

fun readConfig(): ConfigDto {
    val mapper = ObjectMapper(YAMLFactory())
    mapper.registerModule(KotlinModule())
    val path =  Paths.get(Args.configFile)
    return Files.newBufferedReader(path).use {
        mapper.readValue(it, ConfigDto::class.java)
    }
}
