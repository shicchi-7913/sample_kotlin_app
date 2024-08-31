package example.com.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.*

fun Application.configureSession() {
    val secureSetting = environment.config.property("ktor.cookie.secure").toString().toBoolean()

    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.httpOnly = true
            cookie.secure = secureSetting
            cookie.extensions["SameSite"] = "lax"
            cookie.maxAgeInSeconds = 86400 // 1 day
        }
    }
}
