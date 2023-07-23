import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.ktor.plugin.features.JreVersion
import io.ktor.plugin.features.DockerImageRegistry

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

application {
    mainClass.set("ru.beeline.user.ApplicationKt")
}

ktor {

    docker {
        val java_version: String by project

        localImageName.set(project.name)
        imageTag.set(project.version.toString())
        jreVersion.set(JreVersion.valueOf("JRE_$java_version"))
        externalRegistry.set(
            DockerImageRegistry.dockerHub(
                appName = provider { "hl-hw-app" },
                username = providers.environmentVariable("DOCKER_HUB_USERNAME"),
                password = providers.environmentVariable("DOCKER_HUB_PASSWORD")
            )
        )
    }
}

dependencies {
    val ktor_version: String by project
    val logback_version: String by project
    val exposed_version: String by project
    val postgres_version: String by project
    val prometheus_version: String by project
    val logback_appenders_version: String by project
    val kotlin_dt_version: String by project

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlin_dt_version")

    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-jetty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cio-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")
    implementation("io.ktor:ktor-server-swagger:$ktor_version")
    implementation("io.ktor:ktor-server-openapi:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")

    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("io.ktor:ktor-server-metrics-micrometer:$ktor_version")
    implementation("com.sndyuk:logback-more-appenders:$logback_appenders_version")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheus_version")
}

tasks {
    @Suppress("UnstableApiUsage")
    withType<ProcessResources>().configureEach {
        from("$rootDir/specs") {
            into("specs")
        }
    }
}