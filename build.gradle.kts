import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.10"
    id("fabric-loom") version "1.15-SNAPSHOT"
    id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("littlehexxers") {
            sourceSet("main")
            sourceSet("client")
        }
    }

    runs.getByName("client") {
        programArgs("--username", "glidingspider", "--uuid", "8c1b1b7a-fa01-490e-b607-f4c30eb6ac9b")
    }
}

fabricApi {
    configureDataGeneration {
        client = true
    }
}

repositories {
    mavenCentral()

    maven { url = uri("https://maven.blamejared.com") }
    maven { url = uri("https://maven.shedaniel.me/") }
    maven { url = uri("https://maven.terraformersmc.com/releases/") }
    maven { url = uri("https://maven.terraformersmc.com/") }
    maven { url = uri("https://api.modrinth.com/maven") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://maven.ladysnake.org/releases") }
    maven { url = uri("https://maven.jamieswhiteshirt.com/libs-release") }
    maven { url = uri("https://mvn.devos.one/snapshots/") }
    maven { url = uri("https://pool.net.eu.org") }
    maven { url = uri("https://maven.hexxy.media/") }
}

// ... (keep your imports and plugins as they are)

dependencies {
    // 1. Declare variables using the property delegates
    // These must match the keys in your gradle.properties exactly
    val minecraftVersion: String by project
    val yarnMappingsVersion: String by project
    val fabricLoaderVersion: String by project
    val fabricApiVersion: String by project
    val ccaVersion: String by project
    val hexcastingVersion: String by project // Should be 0.11.3 in gradle.properties
    val fabricKotlinVersion: String by project

    // 2. Core Minecraft & Fabric
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappingsVersion:v2")
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabricKotlinVersion")

    // 3. Hex Casting & Primary Dependencies
    // Using 0.11.3 stable stack
    modImplementation("at.petra-k.hexcasting:hexcasting-fabric-$minecraftVersion:$hexcastingVersion") {
        exclude(group = "me.jellysquid.mods", module = "phosphor")
        exclude(group = "me.jellysquid.mods", module = "lithium")
        exclude(group = "dev.emi", module = "emi")
    }

    // Paucal and Patchouli versions aligned for Hex 0.11.3
    modImplementation("at.petra-k:paucal:0.6.1-pre-7+1.20.1-fabric")

    modImplementation("vazkii.patchouli:Patchouli:1.20.1-84-FABRIC")

    // Serialization Hooks 0.4.31 is the fix for the Ingredient Mixin crash
// The 'loom.remap' part is CRITICAL for local JARs
    modLocalRuntime("io.github.tropheusj:serialization-hooks:0.4.99999")

    // 4. Cardinal Components (Required for Hex data/casting)
    modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-base:$ccaVersion")
    modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:$ccaVersion")
    modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-item:$ccaVersion")
    modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-block:$ccaVersion")

    // 5. Additional Utilities
    modImplementation("com.samsthenerd.inline:inline-fabric:1.20.1-1.2.2-12")
    modApi("me.shedaniel.cloth:cloth-config-fabric:11.1.136")

    // Audio/Video Processing
    implementation("ws.schild:jave-core:3.5.0")
    implementation("ws.schild:jave-nativebin-win64:3.5.0")
}

// ... (keep tasks.processResources and compile tasks as they are)
tasks.processResources {
    // 1. Pull the values using the exact names from your gradle.properties
    val minVersion: String by project
    val loaderVersion: String by project
    val kotlinVersion: String by project // This matches 'fabricKotlinVersion' via delegate if named correctly,
    // but let's be safer with findProperty:

    val mVersion = project.findProperty("minecraftVersion") ?: "1.20.1"
    val lVersion = project.findProperty("fabricLoaderVersion") ?: "0.16.10"
    val kVersion = project.findProperty("fabricKotlinVersion") ?: "1.10.20+kotlin.1.9.24"

    inputs.property("version", project.version)
    inputs.property("minecraft_version", mVersion)
    inputs.property("loader_version", lVersion)
    inputs.property("kotlin_loader_version", kVersion)

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to mVersion,
            "loader_version" to lVersion,
            "kotlin_loader_version" to kVersion
        )
    }
}

// Followed by your other task configurations
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    // This MUST be inside the publishing block
    repositories {
        // Add publishing target here if needed
    }
}