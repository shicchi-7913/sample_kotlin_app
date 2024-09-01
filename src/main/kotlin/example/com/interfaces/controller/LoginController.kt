package example.com.interfaces.controller

import at.favre.lib.crypto.bcrypt.BCrypt
import example.com.interfaces.form.LoginRequest
import example.com.model.Users
import example.com.plugins.UserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class CurrentUser(val id: Int, val password: String)

class LoginController {
    suspend fun post(loginRequest: LoginRequest, call: ApplicationCall) {
        val currentUser = transaction {
            Users
                .selectAll()
                .where { Users.email.eq(loginRequest.email!!) }
                .firstOrNull()
                ?.let { row ->
                    CurrentUser(
                        id = row[Users.id],
                        password = row[Users.password]
                    )
                }
        }

        if (currentUser == null) {
            call.respond(HttpStatusCode.NotFound)
            return
        }

        val result: BCrypt.Result = BCrypt.verifyer().verify(loginRequest.password?.toCharArray(), currentUser.password)

        if (result.verified) {
            call.sessions.set(UserSession(id = currentUser.id))
            call.respondText("OK")
        } else {
            call.respond(HttpStatusCode.Unauthorized, "email or password is invalid")
        }
    }
}
