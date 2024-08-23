package example.com.interfaces.controller

import example.com.model.Users
import io.ktor.http.*
import io.ktor.resources.Resource
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

@Resource("/users")
class UserResources()

@Serializable
data class UserRequest(val name: String, val email: String)

class UserController {
    suspend fun post(call: ApplicationCall) {
        val userRequest = call.receive<UserRequest>()
        transaction {
            Users.insert {
                it[name] = userRequest.name
                it[email] = userRequest.email
            }
        }
        call.respond(HttpStatusCode.Created)
    }
}
