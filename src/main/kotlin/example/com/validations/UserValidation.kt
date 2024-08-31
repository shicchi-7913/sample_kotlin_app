package example.com.validations

import example.com.model.Users
import example.com.requests.UserRequest
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserValidation(private val userRequest: UserRequest) {
    private val emailRegex = Regex("^[\\w+\\-.]+@[a-z\\d-]+(\\.[a-z\\d-]+)*\\.[a-z]+\$", RegexOption.IGNORE_CASE)
    val errorMessages: MutableList<String> = mutableListOf()

    fun hasError(): Boolean {
        validateName()
        validateEmail()
        validatePassword()

        return errorMessages.isNotEmpty()
    }

    private fun validateName() {
        val name = userRequest.name
        if(name.isNullOrBlank()) {
            errorMessages.add("name cannot be empty")
            return
        }

        if (name.length > 255) {
            errorMessages.add("name cannot be longer than 255 characters")
        }
    }

    private fun validateEmail() {
        val email = userRequest.email

        if(email.isNullOrBlank()) {
            errorMessages.add("email cannot be empty")
            return
        }

        when {
            email.length > 255 -> {
                errorMessages.add("email cannot be longer than 255 characters")
            }
            !emailRegex.matches(email) -> {
                errorMessages.add("Invalid email format")
            }
            else -> {
                val user = transaction {
                    Users.selectAll().where { Users.email eq(email) }.singleOrNull()
                }
                if(user != null) {
                    errorMessages.add("email has already registered")
                }
            }
        }
    }

    private fun validatePassword() {
        val password = userRequest.password

        if(password.isNullOrBlank()) {
            errorMessages.add("password cannot be empty")
            return
        }

        if(password.length < 6) {
            errorMessages.add("password cannot be shorter than 6 characters")
        }
    }
}
