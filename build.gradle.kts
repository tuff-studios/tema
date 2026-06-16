plugins {
    alias(libs.plugins.kotlin.gradle.plugin)
    alias(libs.plugins.shadow.jar)
    alias(libs.plugins.run.paper)
}

// there is no multi-module structure. not necessary to be moved to settings.gradle.kts
repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly(libs.paper)

    implementation(libs.cloud.core)
    implementation(libs.cloud.paper)
    implementation(libs.gson)
}

val javaVersion = 25

tasks {
    runServer {
        minecraftVersion(libs.versions.minecraft.get())
    }

    shadowJar {
        archiveBaseName = "TEMA"
        archiveVersion = version.toString()
        archiveClassifier = "release" // replace
        mergeServiceFiles()
    }

    processResources {
        val projectVersion = version.toString()
        //filesMatching("paper-plugin.yml") {    FYM PLUGIN.YML
        filesMatching("plugin.yml") {
            expand("version" to projectVersion)
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
}

kotlin {
    jvmToolchain(javaVersion)
}