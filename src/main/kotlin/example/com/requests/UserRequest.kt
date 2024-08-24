package example.com.requests

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(val name: String? = null, val email: String? = null)

