package example.com

import example.com.plugins.configureRouting
import example.com.utilities.configureDatabase
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabase()
    configureRouting()
}
