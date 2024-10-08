package com.zlrx.graphql.config

import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.ValidationRulesBuilderCustomizer
import com.zlrx.graphql.scalars.InstantScalar
import graphql.schema.GraphQLScalarType
import graphql.validation.constraints.AbstractDirectiveConstraint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

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

}

class ExtendedRegistrar(
    private val graphqlInstantScalar: GraphQLScalarType
) : DgsExtendedScalarsAutoConfiguration.AbstractExtendedScalarRegistrar() {

    override fun getScalars(): List<GraphQLScalarType> {
        return listOf(graphqlInstantScalar)
    }

}