package example.com.interfaces.controller

import example.com.model.Users
import example.com.response.UserResponse
import example.com.requests.UserRequest
import example.com.validations.UserValidation
import io.ktor.http.*
import io.ktor.resources.Resource
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

@Resource("/users")
class UserResources() {
    @Resource("{id}")
    class Show(val parent: UserResources = UserResources(), val id: Int)
}

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
                it[email] = userRequest.email?.lowercase()!!
                it[password] = userRequest.password!!
            }
        }
        call.respond(HttpStatusCode.Created)
    }

    suspend fun get(call: ApplicationCall, id: Int) {
        val user = transaction {
            Users
                .select(Users.name, Users.email)
                .where { Users.id eq id }
                .singleOrNull()
                ?.let {
                    UserResponse(
                        name = it[Users.name],
                        email = it[Users.email]
                    )
                }
        }
        if(user == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(HttpStatusCode.OK, user)
        }
    }
}
