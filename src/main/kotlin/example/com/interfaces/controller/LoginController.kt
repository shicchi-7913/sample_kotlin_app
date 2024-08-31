package example.com.interfaces.controller

import example.com.interfaces.form.LoginRequest
import example.com.plugins.UserSession
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.*
import io.ktor.server.sessions.*

class LoginController {
    suspend fun post(loginRequest: LoginRequest, call: ApplicationCall) {
        if (loginRequest.email == "hoge@example.com" && loginRequest.password == "foobar") {
            call.sessions.set(UserSession(id = loginRequest.email))
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.Unauthorized, "email or password is invalid")
        }
    }
}
