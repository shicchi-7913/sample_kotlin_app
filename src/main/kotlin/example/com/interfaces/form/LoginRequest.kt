package example.com.interfaces.form

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String? = null, val password: String? = null)
