package example.com.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.session
import io.ktor.server.auth.Principal
import io.ktor.server.response.respond

data class UserSession(val id: String) : Principal

fun Application.configureAuthentication() {
    install(Authentication) {
        session<UserSession>("auth-session") {
            validate { session ->
                if(session.id == "hoge@example.com") {
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
