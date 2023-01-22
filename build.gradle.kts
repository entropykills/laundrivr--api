import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.serialization") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "com.laundrivr.api"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.squareup:square:24.0.0.20220921")
    implementation("io.github.jan-tennert.supabase:functions-kt-jvm:0.7.5")
    implementation("io.github.jan-tennert.supabase:postgrest-kt-jvm:0.7.5")
    implementation("io.ktor:ktor-client-cio-jvm:2.2.2")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("io.javalin:javalin:5.3.1")
    implementation("org.slf4j:slf4j-simple:2.0.3")
    implementation("com.beust:klaxon:5.5")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("com.laundrivr.api.MainKt")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())
}
