package example.com.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

const val MAX_VARCHAR_LENGTH = 255

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", MAX_VARCHAR_LENGTH)
    val email = varchar("email", MAX_VARCHAR_LENGTH)
    val password = varchar("password", MAX_VARCHAR_LENGTH)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}
