import org.jetbrains.kotlin.gradle.plugin.mpp.Framework.BitcodeEmbeddingMode.BITCODE
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform") version "1.6.20"
  id("com.android.application")
  kotlin("native.cocoapods") version "1.6.20"
  kotlin("plugin.serialization") version "1.6.20"
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
  google()
  mavenCentral()
}

kotlin {
  val serializationVersion = "1.3.2"
  val ktorVersion = "1.6.8"

  js(BOTH) {
    browser {
      commonWebpackConfig {
        cssSupport.enabled = true
      }
    }
  }

  android()

  iosArm64 {
    binaries {
      framework {
        baseName = "NimbusCore"
      }
    }
  }

  val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
    if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
      ::iosArm64
    else
      ::iosX64

  iosTarget("ios") {}

  cocoapods {
    version = "1.0.0-beta.1"
    summary = "Nimbus Core Library"
    homepage = "Link to the Shared Module homepage"

    framework {
      baseName = "NimbusCore"
      isStatic = false
      transitiveExport = false
      embedBitcode(BITCODE)
    }
    // Maps custom Xcode configuration to NativeBuildType
    xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
    xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
        implementation("io.ktor:ktor-client-core:$ktorVersion")
        implementation("io.ktor:ktor-client-serialization:$ktorVersion")
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }
    val jsMain by getting
    val jsTest by getting
    val androidMain by getting {
      dependencies {
        implementation("com.google.android.material:material:1.5.0")
        implementation("io.ktor:ktor-client-android:$ktorVersion")
      }
    }
    val androidTest by getting {
      dependencies {
        implementation("junit:junit:4.13.2")
      }
    }
    val iosArm64Main by getting {
      dependencies {
        implementation("io.ktor:ktor-client-ios:$ktorVersion")
      }
    }
    val iosArm64Test by getting
  }
}

android {
  compileSdk = 31
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig {
    applicationId = "me.user.library"
    minSdk = 21
    targetSdk = 31
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}
