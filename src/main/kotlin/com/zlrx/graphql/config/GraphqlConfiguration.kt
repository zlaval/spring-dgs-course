package com.zlrx.graphql.config

import com.netflix.graphql.dgs.autoconfig.ValidationRulesBuilderCustomizer
import graphql.validation.constraints.AbstractDirectiveConstraint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GraphqlConfiguration(
    private val rules: List<AbstractDirectiveConstraint>
) {

    @Bean
    fun validationRules() = ValidationRulesBuilderCustomizer { builder ->
        builder.addRules(rules)
    }
}