package example.com.interfaces.controller

import at.favre.lib.crypto.bcrypt.BCrypt
import example.com.interfaces.form.LoginRequest
import example.com.model.Users
import example.com.model.Users.email
import example.com.model.Users.password
import example.com.plugins.UserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class LoginController {
    suspend fun post(loginRequest: LoginRequest, call: ApplicationCall) {
        val user = transaction {
            Users
                .selectAll()
                .where { Users.email.eq(loginRequest.email!!) }
                .firstOrNull()
                ?.let { row ->
                    mapOf(
                        "email" to row[Users.email],
                        "password" to row[Users.password]
                    )
                }
        }

        if (user == null) {
            call.respond(HttpStatusCode.NotFound)
            return
        }

        val result: BCrypt.Result = BCrypt.verifyer().verify(loginRequest.password?.toCharArray(), user["password"])

        if (result.verified) {
            call.sessions.set(UserSession(id = user["email"]!!))
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.Unauthorized, "email or password is invalid")
        }
    }
}
