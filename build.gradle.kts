plugins {
    java
    id("com.gradle.plugin-publish") version "0.11.0"
    `java-gradle-plugin`
}

group = "org.evera.nan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("nanReporterPlugin") {
            id = "org.evera.nan.reporter"
            implementationClass = "org.evera.nan.reporter.ReporterPlugin"
        }
    }
}

pluginBundle {
    // These settings are set for the whole plugin bundle
    website = "https://github.com/NotJustANumber"
    vcsUrl = "https://github.com/NotJustANumber/nan-gradle-plugin"
    description = "To upload results to nan server !"


    (plugins) {
        "nanReporterPlugin" {
            // id is captured from java-gradle-plugin configuration
            displayName = "Nan gradle plugin"
            tags = listOf("Tests", "reports", "junit", "testng")
            version = "1.0"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.3")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}