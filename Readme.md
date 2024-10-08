# Schema

### Query and Mutation

Special types. Entry points for queries.  
File: `base.graphql`

```graphql
type Query{

}

type Mutation{

}
```

### Methods

We can extend queries and mutations in the domain files.  
Methods may have inputs and must have a return value.  
File: `user.graphql`

```graphql
extend type Query{
    users: Users!
    user(id: ID!): User!
}

extend type Mutation{
    saveUser(id: ID, user: UserInput!): User!
    deleteUser(id: ID!): Boolean!
}
```

Source code:

```kotlin
@DgsQuery
fun users(): Users = Users(repository.users())

@DgsQuery
fun user(id: String): User {
    return repository.user(id)
}

@DgsMutation
fun saveUser(id: String?, user: UserInput): User = repository.save(id, user)

@DgsMutation
fun deleteUser(id: String): Boolean = repository.delete(id)
```

### Object types

Object type represents a kind of object we can fetch from a service.
The object type has a name and field.
A field might nullable or non nullable represented by the exclamation mark.

```graphql
type User{
    id: ID!
    name: String!
    email: String
}

```

Source code

```kotlin
data class User(

    @JsonProperty("id")
    val id: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("email")
    val email: String? = null,
)

```

### Scalar types

Scalar types are the basic primitives

* Int
* Float
* String
* Boolean
* ID: It represents a unique identifier. Serialized same way as a String

### Enum

```graphql
enum SortDirection{
    ASC, DESC
}
```

Source code

```kotlin
enum class SortDirection {
    ASC, DESC
}

```

### Input types

Input for queries and mutations.   
Query: usually query parameters, like filters, sorting...  
Mutation: object to save

```graphql
input UserInput{
    name: String!
    email: String
}
```

Source code

```kotlin
data class UserInput(

    @JsonProperty("name")
    val name: String,

    @JsonProperty("email")
    val email: String? = null,
)
```

//TODO

* list - null field null content
* embedded type
* comments - documentation
* directive
* interface
* union
* scalar