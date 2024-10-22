package com.zlrx.graphql.user

import com.netflix.graphql.dgs.*
import com.zlrx.graphql.address.AddressDataLoader
import com.zlrx.graphql.codegen.DgsConstants
import com.zlrx.graphql.codegen.types.*
import com.zlrx.graphql.vehicles.VehicleDataLoader
import org.dataloader.DataLoader
import org.dataloader.MappedBatchLoader
import org.slf4j.LoggerFactory
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestHeader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@DgsComponent
class UserDataFetcher(
    private val repository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

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
    fun user(
        @RequestHeader(required = false) headers: Map<String, String>?,
        @RequestHeader(name = "x-first-header") firstHeader: String?,
        @CookieValue alpha: String,
        id: String
    ): User? {
        logger.info(alpha)
        return repository.findById(id)
    }

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

    @DgsData(parentType = DgsConstants.USER.TYPE_NAME)
    fun friends(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<User>> {
        val dataLoader: DataLoader<List<String>, List<User>> = dfe.getDataLoader(FriendsDataLoader::class.java)
        val ids = dfe.getSource<User>()?.friendIds ?: return CompletableFuture.completedFuture(emptyList())
        return dataLoader.load(ids)
    }

}

@DgsDataLoader
class FriendsDataLoader(
    private val repository: UserRepository,
    private val dgsTaskExecutor: AsyncTaskExecutor

) : MappedBatchLoader<List<String>, List<User>> {
    override fun load(friendIdsList: Set<List<String>>): CompletionStage<Map<List<String>, List<User>>> =
        CompletableFuture.supplyAsync({
            val users = repository.usersByIds(friendIdsList.flatten().distinct()).associateBy { it.id }

            friendIdsList.associateWith { friendIds ->
                friendIds.mapNotNull { id ->
                    users[id]
                }
            }
        }, dgsTaskExecutor)
}