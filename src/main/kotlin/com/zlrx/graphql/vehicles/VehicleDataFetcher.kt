package com.zlrx.graphql.vehicles

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsDataLoader
import com.netflix.graphql.dgs.DgsQuery
import com.zlrx.graphql.codegen.types.AirPlane
import com.zlrx.graphql.codegen.types.Car
import com.zlrx.graphql.codegen.types.Ship
import com.zlrx.graphql.codegen.types.Vehicle
import org.dataloader.MappedBatchLoader
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.random.Random

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

data class VehicleEntity(
    val producer: String,
    val type: String,
    val enginePower: Int,
    val wheels: Int,

    val userId: String
) {
    fun toVehicle() = Car(
        producer = producer,
        type = type,
        enginePower = enginePower,
        wheels = wheels
    )
}

@Service
class VehicleMockWebservice() {
    fun loadVehiclesByUserIds(userIds: Iterable<String>): List<VehicleEntity> {
        return userIds.flatMap { userId ->
            (0..Random.nextInt(5)).map { index ->
                VehicleEntity(
                    producer = "Vehicle $userId",
                    type = "Type $index",
                    wheels = 4,
                    userId = userId,
                    enginePower = Random.nextInt(100),
                )
            }
        }
    }
}

@DgsDataLoader(name = "VehiclesByUserId")
class VehicleDataLoader(
    private val service: VehicleMockWebservice,
    private val dgsTaksExecutor: AsyncTaskExecutor

) : MappedBatchLoader<String, List<Vehicle>> {
    override fun load(userIds: Set<String>): CompletionStage<Map<String, List<Vehicle>>> =
        CompletableFuture.supplyAsync({
            service.loadVehiclesByUserIds(userIds)
                .groupBy { it.userId }
                .mapValues { it.value.map { ve -> ve.toVehicle() } }
        }, dgsTaksExecutor)

}