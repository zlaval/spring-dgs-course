package com.zlrx.graphql.user

import com.zlrx.graphql.codegen.types.User
import com.zlrx.graphql.codegen.types.UserFilter
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Component


@Component
class UserRepository {

    fun users(filter: UserFilter?, pr: PageRequest): Page<User> {
        val filteredUsers = if (filter == null) {
            UserDB.users.values.toList()
        } else {
            UserDB.users.values.filter {
                (filter.name == null || it.name == filter.name) &&
                        (filter.sex == null || it.sex == filter.sex) &&
                        (filter.email == null || filter.email.contains(it.email))
            }
        }

        val page = filteredUsers.drop(pr.pageNumber * pr.pageSize).take(pr.pageSize)

        return PageableExecutionUtils.getPage(page, pr) {
            UserDB.users.size.toLong()
        }
    }


    fun findById(id: String) = UserDB.users[id]

}