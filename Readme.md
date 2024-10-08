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

### Embedded types

Types can be composed

```graphql
type Address{
    zip: Int!
    city: String!
    street: String!
    houseNumber: Int!
}

type User{
    id: ID!
    address: Address
}
```

Source code

```kotlin
data class Address(
    @JsonProperty("zip")
    val zip: Int,
    @JsonProperty("city")
    val city: String,
    @JsonProperty("street")
    val street: String,
    @JsonProperty("houseNumber")
    val houseNumber: Int,
)

data class User(
    @JsonProperty("id")
    val id: String,
    val address: Address? = null,
)
```

### Lists

Both the list and the list elements can be marked as non-nullable

```graphql
type User{
    friends: [User!]!
}
```

Source code

```kotlin
data class User(
    val friends: List<User>,
)
```

### Comments

Fields and method might commented.   
The comments are generated into the documentation

```graphql
input UserInput{
    """ Full Name """
    name: String!
    """ Email address """
    email: String!
}
```

### Directives

Directive decorates the schema.   
The server can read them and perform custom logics, like input validation, data manipulation...  
DSG has many built in validator directives

```graphql
directive @Min(value : Int! = 0, message : String = "graphql.validation.Min.message") on ARGUMENT_DEFINITION | INPUT_FIELD_DEFINITION

type User{
    id: ID!
    age: Int! @Min(value: 18) @Max(value: 99)
}
```

### Interface

Specifies a set of fields that multiple types can include

```graphql
interface Vehicle{
    producer: String
    type: String!
    enginePower: Int!
}

type Car implements Vehicle{
    producer: String
    type: String!
    enginePower: Int!

    wheels: Int!
}
```

### Union

Used for multiple return type.  
Similar to interface without common fields

```graphql
type Movie{
    title: String!
    length: Int!
}

type Series{
    title: String!
    season: Int!
}

union Film = Movie | Series
```

### Custom scalar

Custom scalar can be defined and implemented

```graphql
scalar Instant
```