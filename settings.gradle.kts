/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
  }
  resolutionStrategy {
    eachPlugin {
      if (requested.id.namespace == "com.android" || requested.id.name == "kotlin-android-extensions") {
        useModule("com.android.tools.build:gradle:7.1.3")
      }
    }
  }
}

plugins {
  id("com.gradle.enterprise") version("3.10")
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}

rootProject.name = "nimbus-core"

enableFeaturePreview("VERSION_CATALOGS")
