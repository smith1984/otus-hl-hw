import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.ktor.plugin.features.JreVersion
import io.ktor.plugin.features.DockerImageRegistry

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

application {
    mainClass.set("ru.beeline.client.ApplicationKt")
}

ktor {

    docker {
        val java_version: String by project

        localImageName.set(project.name)
        imageTag.set(project.version.toString())
        jreVersion.set(JreVersion.valueOf("JRE_$java_version"))
        externalRegistry.set(
            DockerImageRegistry.dockerHub(
                appName = provider { "hl-hw-client" },
                username = providers.environmentVariable("DOCKER_HUB_USERNAME"),
                password = providers.environmentVariable("DOCKER_HUB_PASSWORD")
            )
        )
    }
}

dependencies {
    val ktor_version: String by project
    val logback_version: String by project
    val kotlin_dt_version: String by project

    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-java:$ktor_version")
    implementation("io.ktor:ktor-client-apache5:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlin_dt_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}
