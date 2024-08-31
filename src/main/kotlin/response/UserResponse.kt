package example.com.response

import kotlinx.serialization.Serializable

@Serializable
class UserResponse(val name: String, val email: String)
