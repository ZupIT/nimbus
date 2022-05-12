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
