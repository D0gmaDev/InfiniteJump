plugins {
   java
    id("io.github.goooler.shadow") version "8.1.8"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

group = "fr.d0gma"
version = "0.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.opencollab.dev/maven-releases/")

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    implementation("fr.d0gma:core:0.1")

    implementation("xyz.xenondevs.invui:invui:1.33")
    // compileOnly("org.incendo:cloud-paper:2.0.0-beta.8")
    implementation("me.catcoder:bukkit-sidebar:6.2.6-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}