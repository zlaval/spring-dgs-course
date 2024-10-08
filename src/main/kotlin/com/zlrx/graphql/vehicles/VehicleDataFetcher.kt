package com.zlrx.graphql.vehicles

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.zlrx.graphql.codegen.types.AirPlane
import com.zlrx.graphql.codegen.types.Car
import com.zlrx.graphql.codegen.types.Ship
import com.zlrx.graphql.codegen.types.Vehicle

@DgsComponent
class VehicleDataFetcher {

    @DgsQuery
    fun vehicles(): List<Vehicle> = listOf(
        Car("Toyota", "Corolla", 140, 4),
        AirPlane("Boeing", "737", 30000, 2),
        Car("Ford", "Mustang", 450, 4),
        Ship("Daewoo", "Algeciras", 100000, 300000)
    )
}