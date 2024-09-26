package com.zlrx.graphql.user

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.zlrx.graphql.codegen.types.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

@DgsComponent
class UserDataFetcher(
    private val repository: UserRepository
) {

    @DgsQuery
    fun users(
        @InputArgument("offset") os: Int?,
        limit: Int?,
        filter: UserFilter?,
        sort: UserSort?
    ): UserPageable {

        val off = os ?: 0
        val lim = limit ?: 10

        val page = if (off == 0) 0 else off / lim

        val pr = PageRequest.of(page, lim)

        if (sort != null) {
            val s = sort.fields.map {
                if (it.direction == SortDirection.ASC) {
                    Sort.Order.asc(it.name)
                } else {
                    Sort.Order.desc(it.name)
                }
            }
            pr.withSort(Sort.by(s))
        }

        val userPage = repository.users(filter, pr)
        return UserPageable(
            items = userPage.content,
            offset = off,
            limit = lim,
            total = userPage.totalElements.toInt()
        )
    }

    @DgsQuery
    fun user(id: String): User? = repository.findById(id)
}