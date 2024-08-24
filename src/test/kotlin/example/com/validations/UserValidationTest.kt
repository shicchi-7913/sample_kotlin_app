package example.com.validations

import example.com.requests.UserRequest
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.Test

class UserValidationTest {
    @Test
    fun `should return error if name is empty`() {
        val userRequest = UserRequest(name = "", email = "test@example.com")
        val userValidation = UserValidation(userRequest)

        val hasError = userValidation.hasError()
        val errors = userValidation.errorMessages

        assertEquals(true, hasError)
        assertEquals(listOf("name cannot be empty"), errors)
    }

    @Test
    fun `should return error if name is too long`() {
        val longName = "a".repeat(256)
        val userRequest = UserRequest(name = longName, email = "test@example.com")
        val userValidation = UserValidation(userRequest)

        val hasError = userValidation.hasError()
        val errors = userValidation.errorMessages

        assertEquals(true, hasError)
        assertEquals(listOf("name cannot be longer than 255 characters."), errors)
    }

    @Test
    fun `should return error if email is null`() {
        val userRequest = UserRequest(name = "Valid Name", email = null)
        val userValidation = UserValidation(userRequest)

        val hasError = userValidation.hasError()
        val errors = userValidation.errorMessages

        assertEquals(true, hasError)
        assertEquals(listOf("email cannot be empty"), errors)
    }

    @Test
    fun `should return error if email is too long`() {
        val longEmail = "a".repeat(255)
        val userRequest = UserRequest(name = "aaa", email = "$longEmail@example.com")
        val userValidation = UserValidation(userRequest)

        val hasError = userValidation.hasError()
        val errors = userValidation.errorMessages

        assertEquals(true, hasError)
        assertEquals(listOf("email cannot be longer than 255 characters."), errors)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "plainaddress          | true",
            "@missingusername.com  | true",
            "username@.com         | true",
            "username@domain       | true",
            "username@domain..com  | true",
            "username@domain.com   | false"
        ],
        delimiter = '|',
    )
    fun `checks email is correct format or not`(
        email: String,
        expected: Boolean
    ) {
        val userRequest = UserRequest(name = "Valid Name", email = email)
        val userValidation = UserValidation(userRequest)

        val hasError = userValidation.hasError()
        val errors = userValidation.errorMessages

        kotlin.test.assertEquals(message = "email is $email", expected = expected, actual = hasError)
        if(hasError) {
            assertEquals(listOf("Invalid email format"), errors)
        }
    }

    @Test
    fun `should not return error if name and email are valid`() {
        val userRequest = UserRequest(name = "Valid Name", email = "test@example.com")
        val userValidation = UserValidation(userRequest)

        val hasError = userValidation.hasError()
        val errors = userValidation.errorMessages

        assertEquals(false, hasError)
        assertEquals(emptyList<String>(), errors)
    }
}
