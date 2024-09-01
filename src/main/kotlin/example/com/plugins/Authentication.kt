package example.com.plugins

import example.com.model.Users
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.session
import io.ktor.server.auth.Principal
import io.ktor.server.response.respond
import org.jetbrains.exposed.sql.transactions.transaction

data class UserSession(val id: Int) : Principal

fun Application.configureAuthentication() {
    install(Authentication) {
        session<UserSession>("auth-session") {
            validate { session ->
                val userId = transaction {
                    Users
                        .select(Users.id)
                        .where { Users.id.eq(session.id) }
                        .firstOrNull()
                }
                if (userId != null) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized, "Login is required")
            }
        }
    }
}
