package example.com.interfaces.controller

import at.favre.lib.crypto.bcrypt.BCrypt
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
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

@Resource("/users")
class UserResources() {
    @Resource("{id}")
    class Show(val parent: UserResources = UserResources(), val id: Int)
    @Resource("{id}")
    class Update(val parent: UserResources = UserResources(), val id: Int)
}

class UserController {
    suspend fun post(call: ApplicationCall) {
        val userRequest = call.receive<UserRequest>()
        val userValidation = UserValidation(userRequest)

        if(userValidation.hasError()) {
            call.respond(HttpStatusCode.BadRequest, userValidation.errorMessages.joinToString(separator = ", "))
            return
        }

        val hashedPassword = BCrypt.withDefaults().hashToString(12, userRequest.password!!.toCharArray())

        transaction {
            Users.insert {
                it[name] = userRequest.name!!
                it[email] = userRequest.email?.lowercase()!!
                it[password] = hashedPassword
            }
        }
        call.respond(HttpStatusCode.Created)
    }

    suspend fun index(call: ApplicationCall) {
        val users = transaction {
            Users
                .selectAll()
                .map { row ->
                    UserResponse(
                        name = row[Users.name],
                        email = row[Users.email]
                    )
                }
        }
        call.respond(HttpStatusCode.OK, users)
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

    suspend fun update(call: ApplicationCall, id: Int) {
        val userRequest = call.receive<UserRequest>()
        val userValidation = UserValidation(userRequest)
        if(userValidation.hasError()) {
            call.respond(HttpStatusCode.BadRequest, userValidation.errorMessages.joinToString(separator = ", "))
            return
        }

        val hashedPassword = BCrypt.withDefaults().hashToString(12, userRequest.password!!.toCharArray())

        transaction {
            Users.update({ Users.id eq(id) }) {
                it[name] = userRequest.name!!
                it[email] = userRequest.email!!
                it[password] = hashedPassword
            }
        }

        call.respond(HttpStatusCode.OK)
    }
}
