import org.jetbrains.kotlin.gradle.plugin.mpp.Framework.BitcodeEmbeddingMode.BITCODE
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
  kotlin("multiplatform") version "1.7.10"
  kotlin("native.cocoapods") version "1.7.10"
  kotlin("plugin.serialization") version "1.7.10"
  id("io.gitlab.arturbosch.detekt") version "1.20.0"
  id("com.android.library") version "7.2.0"
  id("maven-publish")
  id("io.codearte.nexus-staging") version "0.22.0"
  id("signing")
}

detekt {
  toolVersion = "1.20.0"
  config = files("detekt.yml")
  buildUponDefaultConfig = true
  source = files("src/commonMain/kotlin")
}

repositories {
  google()
  mavenCentral()
}

kotlin {
  android {
    publishAllLibraryVariants()
  }
  android()
  iosX64()
  iosArm64()
  iosSimulatorArm64()

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
    val serializationVersion = "1.3.3"
    val coroutinesVersion = "1.6.3"
    val ktorVersion = "2.0.3"
    val commonMain by getting {
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        implementation("io.ktor:ktor-client-core:$ktorVersion")
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
        implementation(kotlin("test-common"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
        implementation("io.ktor:ktor-client-mock:$ktorVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
      }
    }
    val androidMain by getting {
      dependencies {
        implementation("com.google.android.material:material:1.6.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
        implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
      }
    }
    val androidTest by getting {
      dependsOn(commonTest)
      dependencies {
        implementation(kotlin("test-junit"))
      }
    }
    val iosX64Main by getting
    val iosArm64Main by getting
    val iosSimulatorArm64Main by getting
    val iosX64Test by getting
    val iosArm64Test by getting
    val iosSimulatorArm64Test by getting
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
      iosX64Test.dependsOn(this)
      iosArm64Test.dependsOn(this)
      iosSimulatorArm64Test.dependsOn(this)
    }
  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions{
    kotlin.sourceSets.all {
      languageSettings.optIn("kotlin.RequiresOptIn")
    }
  }
}

tasks.register<Copy>("copyiOSTestResources") {
  from("src/commonTest/resources")
  into("build/bin/iosX64/debugTest/resources")
}

tasks.findByName("iosX64Test")!!.dependsOn("copyiOSTestResources")

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
// TODO Extract the code below to another file
// ------------------------- Publication configuration --------------------- //
val releaseVersion = !version.toString().endsWith("-SNAPSHOT")
val sonatypeUsername = System.getenv("ORG_GRADLE_PROJECT_mavenCentralUsername") ?: ""
val sonatypePassword = System.getenv("ORG_GRADLE_PROJECT_mavenCentralPassword") ?: ""
val versionName = System.getenv("VERSION_NAME") ?: project.property("VERSION_NAME").toString()
val signinKeyId = System.getenv("ORG_GRADLE_PROJECT_SIGNINGKEYID") ?: ""
val signinPassword = System.getenv("ORG_GRADLE_PROJECT_SIGNINGPASSWORD") ?: ""

//POM Configuration
val pomGroup = project.property("POM_GROUP").toString()
val pomName = project.property("POM_NAME").toString()
val pomDescription = project.property("POM_DESCRIPTION").toString()
val pomYear = project.property("POM_INCEPTION_YEAR").toString()
val pomLicenseDist = project.property("POM_LICENCE_DIST").toString()
val pomLicenseName = project.property("POM_LICENCE_NAME").toString()
val pomLicenseUrl = project.property("POM_LICENCE_URL").toString()
val pomIssuesUrl = project.property("POM_ISSUES_URL").toString()
val pomIssuesSystem = project.property("POM_ISSUES_SYSTEM").toString()
val pomDevId = project.property("POM_DEVELOPER_ID").toString()
val pomDevName = project.property("POM_DEVELOPER_NAME").toString()
val pomDevUrl = project.property("POM_DEVELOPER_URL").toString()
val pomUrl = project.property("POM_URL").toString()
val pomScmConnection = project.property("POM_SCM_CONNECTION").toString()
val pomScmDevConnection = project.property("POM_SCM_DEV_CONNECTION").toString()
val pomScmUrl = project.property("POM_SCM_URL").toString()

/*
Needed to generate the nexus staging metadata, this uses the gpg file selected
on ~/.gradle/gradle.properties to sign the artifact before send to maven central
 */
extra["signing.keyId"] = signinKeyId
extra["signing.password"] = signinPassword

group = pomGroup
version = versionName

publishing {
  repositories {
    maven {
      name="oss"
      val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
      val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
      url = if (releaseVersion) releasesRepoUrl else snapshotsRepoUrl
      credentials {
        username = sonatypeUsername
        password = sonatypePassword
      }
    }
  }
  publications {
    withType<MavenPublication> {
      pom {
        name.set(pomName)
        description.set(pomDescription)
        url.set(pomUrl)
        inceptionYear.set(pomYear)
        licenses {
          license {
            distribution.set(pomLicenseDist)
            name.set(pomLicenseName)
            url.set(pomLicenseUrl)
          }
        }
        issueManagement {
          system.set(pomIssuesSystem)
          url.set(pomIssuesUrl)
        }
        scm {
          developerConnection.set(pomScmDevConnection)
          connection.set(pomScmConnection)
          url.set(pomScmUrl)
        }
        developers {
          developer {
            id.set(pomDevId)
            name.set(pomDevName)
            email.set(pomDevUrl)
          }
        }
      }
    }
  }
}

signing {
  setRequired {
    // signing is required if this is a release version and the artifacts are to be published
    // do not use hasTask() as this require realization of the tasks that maybe are not necessary
    releaseVersion && gradle.taskGraph.allTasks.any { it is PublishToMavenRepository }
  }
  @Suppress("UnstableApiUsage")
  sign(publishing.publications)
}

nexusStaging {
  username = sonatypeUsername
  password = sonatypePassword
  packageGroup = "br.com.zup"
}
// ------------------------- End of Publication configuration --------------------- //
