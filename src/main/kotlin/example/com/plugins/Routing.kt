package example.com.plugins

import example.com.model.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.openapi.openAPI
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureRouting() {
    val dbName = environment.config.property("ktor.database.url").getString()
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml")
        get("/") {
            Database.connect("jdbc:mysql://localhost:3306/sample_kotlin_app", driver = "com.mysql.cj.jdbc.Driver", user = "root", password = "password")

            val userCount = transaction {
                Users.selectAll().count()
            }
            call.respondText(userCount.toString())
        }
    }
}
