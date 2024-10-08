package com.zlrx.graphql.config

import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.ValidationRulesBuilderCustomizer
import com.zlrx.graphql.scalars.InstantScalar
import graphql.schema.GraphQLScalarType
import graphql.validation.constraints.AbstractDirectiveConstraint
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.task.AsyncTaskExecutor
import java.time.Duration

@Configuration
class GraphqlConfiguration(
    private val rules: List<AbstractDirectiveConstraint>
) {

    @Bean
    fun validationRules() = ValidationRulesBuilderCustomizer { builder ->
        builder.addRules(rules)
    }

    @Bean
    fun graphqlInstantScalar() = InstantScalar.create()

    @Bean
    @Primary
    fun extendedScalarRegistrar() = ExtendedRegistrar(graphqlInstantScalar())

    @Bean
    fun dgsTaskExecutor(): AsyncTaskExecutor {
        val executor = ThreadPoolTaskExecutorBuilder()
            .threadNamePrefix("dgs-")
            .corePoolSize(5)
            .maxPoolSize(10)
            .allowCoreThreadTimeOut(true)
            .awaitTermination(true)
            .awaitTerminationPeriod(Duration.ofSeconds(5))
            .build()

        executor.initialize()
        return executor
    }

}

class ExtendedRegistrar(
    private val graphqlInstantScalar: GraphQLScalarType
) : DgsExtendedScalarsAutoConfiguration.AbstractExtendedScalarRegistrar() {

    override fun getScalars(): List<GraphQLScalarType> {
        return listOf(graphqlInstantScalar)
    }

}