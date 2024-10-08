package com.zlrx.graphql.address

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsDataLoader
import com.zlrx.graphql.codegen.types.Address
import org.dataloader.MappedBatchLoader
import org.slf4j.LoggerFactory
import org.springframework.core.task.AsyncTaskExecutor
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@DgsComponent
class AddressDataFetcher {
}

@DgsDataLoader(name = "address")
class AddressDataLoader(
    private val repository: AddressRepository,
    private val dgsTaskExecutor: AsyncTaskExecutor
) : MappedBatchLoader<String, Address> {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun load(addressIds: Set<String>): CompletionStage<Map<String, Address>> = CompletableFuture.supplyAsync(

        {
            logger.info("Loads addresses $addressIds")
            repository.findByIdsIn(addressIds).associateBy { it.id }
        }, dgsTaskExecutor
    )

}


