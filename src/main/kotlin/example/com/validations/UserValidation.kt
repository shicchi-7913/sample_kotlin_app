package example.com.validations

import example.com.requests.UserRequest

class UserValidation(private val userRequest: UserRequest) {
    private val emailRegex = Regex("^[\\w+\\-.]+@[a-z\\d\\-.]+\\.[a-z]+\$", RegexOption.IGNORE_CASE)
    val errorMessages: MutableList<String> = mutableListOf()

    fun hasError(): Boolean {
        validateName()
        validateEmail()

        return errorMessages.isNotEmpty()
    }

    private fun validateName() {
        if(userRequest.name.isNullOrBlank()) {
            errorMessages.add("name cannot be empty")
        }

        if (!userRequest.name.isNullOrBlank() && userRequest.name.length > 255) {
            errorMessages.add("name cannot be longer than 255 characters.")
        }
    }

    private fun validateEmail() {
        if(userRequest.email.isNullOrBlank()) {
            errorMessages.add("email cannot be empty")
        }

        if (!userRequest.email.isNullOrBlank() && userRequest.email.length > 255) {
            errorMessages.add("email cannot be longer than 255 characters.")
        }

        if (!userRequest.email.isNullOrBlank() && !emailRegex.matches(userRequest.email)) {
            errorMessages.add("Invalid email format")
        }
    }
}
