import java.net.URI
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version ("1.4.31")
    id("java")
    `kotlin-dsl`
    id("publishing")
    id("maven-publish")
}
group = "org.artembogomolova.common"
version = getProjectVersion()
println("project version is $version")
val kotlinVersion = "1.4.31"
val springBootVersion = "2.4.2"
val springDependencyManagementVersion = "1.0.11.RELEASE"
val classGraphVersion = "4.8.98"
val detektPluginVersion = "1.15.0"
val spotbugsPluginVersion = "4.6.0"
val jacocoVersion = "0.8.6"
val sonarPluginVersion = "3.1"
val dokkaVersion = "1.4.30"
val javaVersion = "15"
repositories {
    mavenCentral()
    gradlePluginPortal()
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
val COMMON_BUILD_BASE_PLUGIN_ID = "common-no-ext-tool-build-plugin"
val COMMON_BASE_PLUGIN_ID = "common-base-plugin"
val COMMON_SPRING_BOOT_WEB_PLUGIN_ID = "common-spring-boot-web-plugin"
val CORE_SPRING_BOOT_PLUGIN_ID = "core-spring-boot-plugin"
val COMMON_SPRING_BOOT_TEST_PLUGIN_ID = "common-spring-boot-test-plugin"
gradlePlugin {
    plugins {
        create(COMMON_BUILD_PLUGIN_ID) {
            id = COMMON_BUILD_PLUGIN_ID
            displayName = "Kotlin/Java Common build plugin, extended"
            description = "Common build plugin for Java or Kotlin projects(with extern tool integration)"
            implementationClass = "org.artembogomolova.build.plugins.CommonBuildPlugin"
        }

        create(COMMON_BASE_PLUGIN_ID) {
            id = COMMON_BASE_PLUGIN_ID
            displayName = "Common base plugin(with extern tool integration)"
            description = "Common base plugin(with extern tool integration)"
            implementationClass = "org.artembogomolova.build.plugins.CommonExternToolPlugin"
        }

        create(COMMON_BUILD_BASE_PLUGIN_ID) {
            id = COMMON_BUILD_BASE_PLUGIN_ID
            displayName = "Common base plugin(without extern tool integration)"
            description = "Common base plugin(without extern tool integration)"
            implementationClass = "org.artembogomolova.build.plugins.CommonBuildWithoutExternToolPlugin"
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
val mavenPackageRegistryUri: String = System.getenv("MAVEN_PACKAGE_REGISTRY_URL") + System.getenv("GITHUB_REPOSITORY")

val publicationName = "gpr"
publishing {
    repositories {
        maven {
            url = URI(mavenPackageRegistryUri)
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>(publicationName) {
            from(components["java"])
        }
    }
}
/*ignore*/
tasks.named<Task>("publish${publicationName[0].toUpperCase() + publicationName.substring(1)}PublicationToMavenRepository") {
    enabled = false
}


fun getProjectVersion(): String {
    val projectVersion: String? = System.getenv("GITHUB_REF")
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
            /*branch-commit_sha*/
            projectVersion.substring(headsIndex + HEADS_PREFIX.length) + "-" + System.getenv("GITHUB_SHA")
        }
    }

}
