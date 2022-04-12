# Nimbus Core
Nimbus is the codename (or actual name) for the next version of Beagle (Zup's Server Driven UI library).

Nimbus will be first released with support for SwiftUI and Compose. Everything shared by the 2 libs will be written
in Kotlin (KMM) and placed in the Core library (this repo).

## Development remarks

### iOS interoperability

1. Don't ever use default values other than null. Example:

```kotlin
data class Person {
  val name: String? // ok
  val age: Int? = null // ok
  val maritalStatus: MaritalStatus = MaritalStatus.single // invalid: ObjectiveC doesn't support default values
}

fun start(httpClient: HttpClient?) { // ok
  // ...
}

fun start(httpClient: HttpClient? = null) { // ok, but see the next topic
  // ...
}

fun start(httpClient: HttpClient? = HttpClient()) { // invalid: ObjectiveC doesn't support default values
  // ...
}
```

2. Prefer method overload instead of default null values

```kotlin
fun paint(tree: ServerDrivenNode, anchor: String? = null, mode: TreeUpdateMode? = null) {
  // ...
}
```

The function above is ok. It can be called in iOS, but since ObjectiveC doesn't support optional method parameters,
it would need to be called like this:

```swift
paint(tree, null, null)
```

To avoid this problem, in kotlin, always prefer method overloading:

```kotlin
fun paint(tree: ServerDrivenNode, anchor: String?, mode: TreeUpdateMode) {
  // ...
}

fun paint(tree: ServerDrivenNode, anchor: String) {
  paint(tree, anchor, TreeUpdateMode.REPLACE_ITSELF)
}

fun paint(tree: ServerDrivenNode, mode: TreeUpdateMode? = null) {
  paint(tree, null, mode)
}
```

This way, in iOS, you can call any of the three signatures.

3. Avoid using generics. Generics are not always correctly converted to ObjectiveC.
