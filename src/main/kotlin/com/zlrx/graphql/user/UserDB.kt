package com.zlrx.graphql.user

import com.zlrx.graphql.codegen.types.Sex
import com.zlrx.graphql.codegen.types.User

object UserDB {

    val users = mutableMapOf(
        "1" to User(id = "1", name = "Joe Doe", email = "joe@test.com", sex = Sex.MALE),
        "2" to User(id = "2", name = "Jane Doe", email = "jane@test.com", sex = Sex.FEMALE),
        "3" to User(id = "3", name = "Adrian", email = "adrien@test.com")
    )
}