package com.zlrx.graphql.scalars

import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

class InstantScalar {

    companion object {

        @JvmStatic
        fun create(name: String? = null) = GraphQLScalarType.newScalar()
            .name(name ?: "Instant")
            .description("An instantaneous point on the time-line")
            .coercing(GraphqlInstantCoercing())
            .build()
    }
}

class GraphqlInstantCoercing() : Coercing<Instant, String> {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun serialize(input: Any, graphQLContext: GraphQLContext, locale: Locale): String = when (input) {
        is Instant -> DateTimeFormatter.ISO_INSTANT.format(input)
        else -> parseStringOrThrow(input)
    }

    override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): Instant =
        parseInstantOrThrow(input)

    override fun parseLiteral(
        input: Value<*>,
        variables: CoercedVariables,
        graphQLContext: GraphQLContext,
        locale: Locale
    ): Instant = if (input is StringValue) {
        parseInstantOrThrow(input)
    } else {
        throw CoercingSerializeException("Invalid Instant format $input")
    }

    private fun convert(input: Any): Instant? {
        return when (input) {
            is String -> {
                try {
                    Instant.parse(input)
                } catch (e: DateTimeParseException) {
                    logger.error("Cannot parse $input to Instant")
                    null
                }
            }

            is Instant -> input
            else -> null
        }
    }

    private fun parseStringOrThrow(input: Any): String {
        val instant = convert(input)
        return if (instant != null) {
            DateTimeFormatter.ISO_INSTANT.format(instant)
        } else {
            throw CoercingSerializeException("Cannot convert $input to Instant")
        }
    }

    private fun parseInstantOrThrow(input: Any): Instant {
        return convert(input) ?: throw CoercingSerializeException("Invalid Instant value $input")
    }
}