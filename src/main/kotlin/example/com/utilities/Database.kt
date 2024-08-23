package example.com.utilities

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() {
    val databaseUrl = getDatabaseInformation("url")
    val databaseUser = getDatabaseInformation("user")
    val databasePassword = getDatabaseInformation("password")
    Database.connect(databaseUrl, driver = "com.mysql.cj.jdbc.Driver", user = databaseUser, password = databasePassword)
}

private fun Application.getDatabaseInformation(property: String): String {
    return environment.config.property("ktor.database.$property").getString()
}
