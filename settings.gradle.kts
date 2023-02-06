
rootProject.name = "london-unit-tests-example"

dependencyResolutionManagement {
    versionCatalogs {
        create("tools") {
            version("kotlin", "1.8.0")
            version("jvm", "17")

            plugin("kotlin-lang", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
            plugin("kotlin-kapt", "org.jetbrains.kotlin.kapt").versionRef("kotlin")
            plugin("kotlin-allopen", "org.jetbrains.kotlin.plugin.allopen").versionRef("kotlin")
            plugin("shadow", "com.github.johnrengelman.shadow").version("7.1.2")

            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin")
            library("kotlin-stdlib", "org.jetbrains.kotlin", "kotlin-stdlib-jdk8").versionRef("kotlin")
        }

        create("libs") {
            version("kotest", "5.5.5")

            library("guava", "com.google.guava:guava:31.1-jre")
            library("kotlinlogging", "io.github.microutils:kotlin-logging-jvm:3.0.5")
            library("mockito", "org.mockito.kotlin:mockito-kotlin:4.1.0")
            library("slf4j-logback", "ch.qos.logback:logback-classic:1.4.5")
            library("test.kotest.runner", "io.kotest", "kotest-runner-junit5-jvm").versionRef("kotest")
            library("test.kotest.assertions", "io.kotest", "kotest-assertions-core").versionRef("kotest")

            bundle("kotest", listOf("test.kotest.runner", "test.kotest.assertions"))
        }
    }
}


