package example.com.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

const val MAX_VARCHAR_LENGTH = 255

object Users : IntIdTable("users") {
    val name = varchar("name", MAX_VARCHAR_LENGTH)
    val email = varchar("email", MAX_VARCHAR_LENGTH)
    val password = varchar("password", MAX_VARCHAR_LENGTH)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}
