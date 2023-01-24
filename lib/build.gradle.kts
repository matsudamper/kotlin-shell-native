plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation(kotlin("test"))

    val kotestVersion = "5.5.4"
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
