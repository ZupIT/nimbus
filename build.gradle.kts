import org.jetbrains.kotlin.gradle.plugin.mpp.Framework.BitcodeEmbeddingMode.BITCODE
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
  kotlin("multiplatform") version "1.6.21"
  kotlin("native.cocoapods") version "1.6.21"
  kotlin("plugin.serialization") version "1.6.21"
  id("com.android.library") version "7.2"
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
  google()
  mavenCentral()
}

kotlin {
  android()
  iosX64()
  iosArm64()
  iosSimulatorArm64()

  cocoapods {
    version = "1.0"
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
    val serializationVersion = "1.3.2"
    val coroutinesVersion = "1.6.1"
    val ktorVersion = "2.0.0"
    val commonMain by getting {
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        implementation("io.ktor:ktor-client-core:$ktorVersion")
      }
    }
    val commonTest by getting {
      dependsOn(commonMain)
      dependencies {
        implementation(kotlin("test"))
        implementation(kotlin("test-common"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
      }
    }
    val androidMain by getting {
      dependencies {
        implementation("com.google.android.material:material:1.5.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
        implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
      }
    }
    val androidTest by getting {
      dependsOn(commonTest)
      dependencies {
        implementation(kotlin("test-junit"))
        implementation("junit:junit:4.13.2")
      }
    }
    val iosX64Main by getting
    val iosArm64Main by getting
    val iosSimulatorArm64Main by getting
    val iosMain by creating {
      dependsOn(commonMain)
      iosX64Main.dependsOn(this)
      iosArm64Main.dependsOn(this)
      iosSimulatorArm64Main.dependsOn(this)
      dependencies {
        implementation("io.ktor:ktor-client-darwin:$ktorVersion")
      }
    }
    val iosTest by creating {
      dependsOn(commonTest)
    }
  }

  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions{
      kotlin.sourceSets.all {
        languageSettings.optIn("kotlin.RequiresOptIn")
      }
    }
  }
}

android {
  compileSdk = 31
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig {
    minSdk = 21
    targetSdk = 31
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}
