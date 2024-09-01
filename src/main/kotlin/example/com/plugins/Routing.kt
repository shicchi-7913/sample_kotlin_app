package example.com.plugins

import example.com.interfaces.controller.LoginController
import example.com.interfaces.controller.LogoutController
import example.com.interfaces.controller.UserResources
import example.com.interfaces.controller.UserController
import example.com.interfaces.form.LoginRequest
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.request.*
import io.ktor.server.resources.Resources
import io.ktor.server.resources.get as resourcesGet
import io.ktor.server.resources.post as resourcesPost
import io.ktor.server.resources.patch as resourcesPatch
import io.ktor.server.response.*
import io.ktor.server.routing.routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.post

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    install(ContentNegotiation) {
        json()
    }
    install(Resources)
    routing {
        openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml")
        post("/login") {
            val loginRequest = call.receive<LoginRequest>()
            LoginController().post(loginRequest, call)
        }
        resourcesPost<UserResources> {
            UserController().post(call)
        }
        authenticate("auth-session") {
            resourcesGet<UserResources.Show> { user ->
                val userSession = call.principal<UserSession>()
                if(userSession?.id == user.id) {
                    UserController().get(call, user.id)
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
            resourcesPatch<UserResources.Patch> { user ->
                val userSession = call.principal<UserSession>()
                if(userSession?.id == user.id) {
                    UserController().patch(call, user.id)
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
            delete("/logout") {
                val userSession = call.principal<UserSession>()
                if(userSession != null) {
                    LogoutController().delete(call)
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
    }
}
