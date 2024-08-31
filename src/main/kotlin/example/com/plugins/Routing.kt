package example.com.plugins

import example.com.interfaces.controller.LoginController
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
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
        post<UserResources> {
            UserController().post(call)
        }
        get<UserResources.Show> { user ->
            UserController().get(call, user.id)
        }
        authenticate("auth-session") {
            get("/hello") {
                val userSession = call.principal<UserSession>()
                call.respondText("Hello, session id is ${userSession?.id}.")
            }
        }
    }
}
