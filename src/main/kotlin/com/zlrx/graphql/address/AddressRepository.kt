package com.zlrx.graphql.address

import org.springframework.stereotype.Component

@Component
class AddressRepository {

    fun findByIdsIn(ids: Iterable<String>) = AddressDB.addresses.filter {
        ids.contains(it.key)
    }.values.toList()

}