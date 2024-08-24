package example.com.interfaces.controller

import example.com.model.Users
import example.com.requests.UserRequest
import example.com.validations.UserValidation
import io.ktor.http.*
import io.ktor.resources.Resource
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

@Resource("/users")
class UserResources()

class UserController {
    suspend fun post(call: ApplicationCall) {
        val userRequest = call.receive<UserRequest>()
        val userValidation = UserValidation(userRequest)

        if(userValidation.hasError()) {
            call.respond(HttpStatusCode.BadRequest, userValidation.errorMessages.joinToString(separator = ", "))
            return
        }

        transaction {
            Users.insert {
                it[name] = userRequest.name!!
                it[email] = userRequest.email!!
            }
        }
        call.respond(HttpStatusCode.Created)
    }
}
