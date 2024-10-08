package com.zlrx.graphql.address

import com.zlrx.graphql.codegen.types.Address

object AddressDB {

    val addresses = mapOf(
        "1" to Address(
            id = "1",
            zip = 1234,
            city = "Rivergate",
            street = "Main street",
            houseNumber = 12
        ),
        "2" to Address(
            id = "2",
            zip = 7777,
            city = "Neverland",
            street = "Dream street",
            houseNumber = 16
        ),
    )

}