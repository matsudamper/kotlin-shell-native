import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("maven-publish")
}

group = "net.matsudamper.command"
version = "1.0"

dependencies {
    testImplementation(kotlin("test"))

    val kotestVersion = "5.5.4"
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    kotlinOptions.freeCompilerArgs += "-Xexplicit-api=strict"
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("GitHubPackages") {
            groupId = "net.matsudamper"
            artifactId = "command"
            version = project.version.toString()
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/matsudamper/kotlin-shell-native")
            credentials {
                username = "matsudamper"
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
