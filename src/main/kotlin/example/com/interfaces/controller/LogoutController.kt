package example.com.interfaces.controller

import example.com.plugins.UserSession
import io.ktor.http.*
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.*
import io.ktor.server.sessions.*

class LogoutController {
    suspend fun delete(call: ApplicationCall) {
        call.sessions.clear<UserSession>()
        call.respond(HttpStatusCode.OK)
    }
}
