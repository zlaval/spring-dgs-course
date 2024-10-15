package com.zlrx.graphql.user

import com.netflix.graphql.dgs.*
import com.zlrx.graphql.address.AddressDataLoader
import com.zlrx.graphql.codegen.DgsConstants
import com.zlrx.graphql.codegen.types.*
import com.zlrx.graphql.vehicles.VehicleDataLoader
import org.dataloader.DataLoader
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.concurrent.CompletableFuture

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

    @DgsMutation
    fun saveUser(id: String?, user: UserInput): User = repository.save(id, user)

    @DgsMutation
    fun deleteUser(id: String): Boolean = repository.delete(id)

    @DgsData(parentType = DgsConstants.USER.TYPE_NAME)
    fun address(dfe: DgsDataFetchingEnvironment): CompletableFuture<Address> {
        val dataLoader: DataLoader<String, Address> = dfe.getDataLoader(AddressDataLoader::class.java)
        val id = dfe.getSource<User>()?.addressId
        return if (id == null) {
            CompletableFuture.completedFuture(null)
        } else {
            dataLoader.load(id)
        }
    }

    @DgsData(parentType = DgsConstants.USER.TYPE_NAME, field = "vehicles")
    fun cars(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Vehicle>> {
        val dataLoader: DataLoader<String, List<Vehicle>> = dfe.getDataLoader(VehicleDataLoader::class.java)
        val id = dfe.getSource<User>()?.id
        return dataLoader.load(id)
    }

}