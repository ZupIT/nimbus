# Nimbus
The Nimbus SDUI is:

1. A solution for applications that need to have some of its user interface (UI) driven by the backend, i.e. Server Driven UI (SDUI).
1. A protocol for serializing the content and behavior of a UI into JSON so it can be sent by a backend server and interpreted by the front-end.
1. A set of libraries that implements this protocol.

An application that uses Nimbus will have:
1. A backend that provides the JSON describing the UI.
1. A frontend application that will interpret the JSON sent by the backend and show the UI.

Nimbus is currently available for iOS and Android and is built on top of SwiftUI and Jetpack Compose, respectively.

To know more about Nimbus SDUI, please check our [documentation](https://github.com/ZupIT/nimbus-docs/blob/main/readme.md).

# Nimbus Core
This repository contains the core code for all Nimbus frontend libraries. It's built using
[Kotlin Mobile Platform (KMM)](https://kotlinlang.org/lp/mobile/) and concentrates all the code common to both
[Nimbus Compose](https://github.com/ZupIT/nimbus-compose) and [Nimbus SwiftUI](https://github.com/ZupIT/nimbus-swiftui).

# Development stage
Nimbus is currently in beta.

# Useful links
- [Introductory article](https://medium.com/p/9a0d95686fd9/): blog post introducing Nimbus SDUI.
- [Documentation](https://github.com/ZupIT/nimbus-docs): the documentation for both the frontend and backend libraries. This is not in a website format yet, but you can read everything through GitHub.
- [Nimbus](https://github.com/ZupIT/nimbus): the common code between Nimbus SwiftUI and Nimbus Compose. This has been built using Kotlin Multiplatform Mobile (KMM).
- [Nimbus Compose](https://github.com/ZupIT/nimbus-compose): all modules necessary to run Nimbus in a Jetpack Compose project.
- [Nimbus SwiftUI](https://github.com/ZupIT/nimbus-swiftui): all modules necessary to run Nimbus in a SwiftUI project.
- [Nimbus Compose Layout](https://github.com/ZupIT/nimbus-layout-compose): layout components for Nimbus Compose.
- [Nimbus SwiftUI Layout](https://github.com/ZupIT/nimbus-layout-swiftui): layout components for Nimbus SwiftUI.
- [Nimbus Backend TS](https://github.com/ZupIT/nimbus-backend-ts): modules for the backend in Typescript.

## **License**
[**Apache License 2.0**](https://github.com/ZupIT/nimbus-core/blob/main/LICENSE.txt).
