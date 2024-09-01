package example.com.response

import kotlinx.serialization.Serializable

@Serializable
class UserResponse(val name: String, val email: String)

@Serializable
class UsersResponse<UserResponse>(
    val users: List<UserResponse>,
    val currentPage: Long,
    val perPage: Int,
    val totalPages: Long,
    val totalCount: Long,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
)
