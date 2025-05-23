package com.zlrx.graphql.user

import com.zlrx.graphql.codegen.types.Sex
import com.zlrx.graphql.codegen.types.User
import java.time.Instant

object UserDB {

    val users = mutableMapOf(
        "1" to User(
            id = "1",
            name = "Joe Doe",
            email = "joe@test.com",
            sex = Sex.MALE,
            createdAt = Instant.now(),
            addressId = "1",
            friendIds = listOf("2", "3")
        ),
        "2" to User(
            id = "2",
            name = "Jane Doe",
            email = "jane@test.com",
            sex = Sex.FEMALE,
            createdAt = Instant.now(),
            friendIds = listOf("1")
        ),
        "3" to User(id = "3", name = "Adrian", email = "adrien@test.com", createdAt = Instant.now(), addressId = "2")
    )
}