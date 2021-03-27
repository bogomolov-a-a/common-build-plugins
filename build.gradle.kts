import java.net.URI
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version ("1.4.20")
    id("java")
    `kotlin-dsl`
    id("publishing")
    id("maven-publish")
}
group = "org.artembogomolova.common"
version = getProjectVersion()
println("project version is $version")
val kotlinVersion = "1.4.20"
val springBootVersion = "2.4.2"
val springDependencyManagementVersion = "1.0.11.RELEASE"
val classGraphVersion = "4.8.98"
val detektPluginVersion = "1.15.0"
val spotbugsPluginVersion = "4.6.0"
val jacocoVersion = "0.8.6"
val sonarPluginVersion = "3.1"
val dokkaVersion = kotlinVersion
val javaVersion = "15"
repositories {
    mavenCentral()
    gradlePluginPortal()
    jcenter()
}
plugins.apply(org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper::class.java)
dependencies {
/*spring*/
    api("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
    api("io.spring.gradle:dependency-management-plugin:$springDependencyManagementVersion")
    api("io.github.classgraph:classgraph:$classGraphVersion")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")
/*quality*/
    api("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detektPluginVersion")
    api("gradle.plugin.com.github.spotbugs.snom:spotbugs-gradle-plugin:$spotbugsPluginVersion")
/*test reports*/
    api("org.jacoco:org.jacoco.core:$jacocoVersion")
/*sonar*/
    api("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:$sonarPluginVersion")
/*documentation*/
    api("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = javaVersion
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = javaVersion
}
/*plugin ids*/
val COMMON_BUILD_PLUGIN_ID = "common-build-plugin"
val COMMON_SPRING_BOOT_WEB_PLUGIN_ID = "common-spring-boot-web-plugin"
val CORE_SPRING_BOOT_PLUGIN_ID = "core-spring-boot-plugin"
val COMMON_SPRING_BOOT_TEST_PLUGIN_ID = "common-spring-boot-test-plugin"
gradlePlugin {
    plugins {
        create(COMMON_BUILD_PLUGIN_ID) {
            id = COMMON_BUILD_PLUGIN_ID
            displayName = "Kotlin/Java Common build plugin"
            description = "Common build plugin for Java or Kotlin projects"
            implementationClass = "org.artembogomolova.build.plugins.CommonBuildPlugin"
        }
        create(CORE_SPRING_BOOT_PLUGIN_ID) {
            id = CORE_SPRING_BOOT_PLUGIN_ID
            displayName = "Spring boot core build plugin"
            description = "Spring boot core plugin for Java or Kotlin projects"
            implementationClass = "org.artembogomolova.build.plugins.CoreSpringBootPlugin"
        }
        create(COMMON_SPRING_BOOT_WEB_PLUGIN_ID) {
            id = COMMON_SPRING_BOOT_WEB_PLUGIN_ID
            displayName = "Spring boot web build plugin"
            description = "Spring boot  plugin for Java or Kotlin projects"
            implementationClass = "org.artembogomolova.build.plugins.SpringBootWebPlugin"
        }
        create(COMMON_SPRING_BOOT_TEST_PLUGIN_ID) {
            id = COMMON_SPRING_BOOT_TEST_PLUGIN_ID
            displayName = "Spring boot test build plugin"
            description = "Spring boot test plugin for Java or Kotlin projects"
            implementationClass = "org.artembogomolova.build.plugins.SpringBootTestPlugin"
        }
    }
}
val githubRepository = System.getenv("GITHUB_REPOSITORY")
publishing {
    repositories {
        if (null != githubRepository) {
            maven {
                url = URI(githubRepository)
                credentials {
                    username = System.getenv("USERNAME")
                    password = System.getenv("TOKEN")
                }
            }
        } else {
            mavenLocal()
        }
    }
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
if (null != githubRepository) {
/*ignore*/
    tasks.getByName("publishGprPublicationToMavenRepository") {
        enabled = false
    }
}

fun getProjectVersion(): String {
    val projectVersion = System.getenv("GITHUB_REF")
    return if (null == projectVersion) {
        "local-SNAPSHOT"
    } else {
        val TAG_PREFIX = "refs/tags/v"
        val tagIndex = projectVersion.indexOf(TAG_PREFIX)
        if (tagIndex > -1) {
            projectVersion.substring(tagIndex + TAG_PREFIX.length)
        } else {
            val HEADS_PREFIX = "refs/heads/"
            val headsIndex = projectVersion.indexOf(HEADS_PREFIX)
            projectVersion.substring(headsIndex + HEADS_PREFIX.length)
        }
    }

}