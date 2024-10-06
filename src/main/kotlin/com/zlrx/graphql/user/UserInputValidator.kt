package com.zlrx.graphql.user

import com.zlrx.graphql.codegen.types.Sex
import graphql.GraphQLError
import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import graphql.validation.constraints.AbstractDirectiveConstraint
import graphql.validation.constraints.Documentation
import graphql.validation.rules.ValidationEnvironment
import org.springframework.stereotype.Component

@Component
class UserInputValidator : AbstractDirectiveConstraint("UserInput") {
    override fun getDocumentation(): Documentation = Documentation.newDocumentation()
        .messageTemplate(messageTemplate)
        .description("Validate user input")
        .directiveSDL("""directive @UserInput(message: String = "graphql.validation.UserInput.message") on ARGUMENT_DEFINITION | INPUT_FIELD_DEFINITION""")
        .build()

    override fun appliesToType(inputType: GraphQLInputType?): Boolean = inputType is GraphQLInputObjectType

    override fun runConstraint(env: ValidationEnvironment): MutableList<GraphQLError> {
        val values = env.validatedValue as LinkedHashMap<*, *>

        val personId = values["personId"] as String?

        val directive = env.getContextObject(GraphQLAppliedDirective::class.java)
        val personIdNullable = getBoolArg(directive, "personIdNullable")

        if (personId == null && !personIdNullable) {
            return mkError(env, "error", "PersonId cannot be null")
        }

        if (personId == null) {
            return mutableListOf()
        }

        val sex = values["sex"] as String? ?: return mutableListOf()

        if (Sex.valueOf(sex) == Sex.FEMALE) {
            if (!personId.startsWith("XX")) {
                return mkError(env, "error", "Female person id must start with XX")
            }
        } else if (Sex.valueOf(sex) == Sex.MALE) {
            if (!personId.startsWith("XY")) {
                return mkError(env, "error", "Male person id must start with XY")
            }
        }

        return mutableListOf()

    }

    override fun appliesToListElements(): Boolean = true

}