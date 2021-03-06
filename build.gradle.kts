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
val androidBuildToolsPluginVersion = "4.1.1"
val navigationSafeArgsPluginVersion = "2.3.5"
val mannodermausAndroidJunit5Version = "1.7.1.1"
repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
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
    /*android*/
    api("com.android.tools.build:gradle:$androidBuildToolsPluginVersion")
    api("androidx.navigation:navigation-safe-args-gradle-plugin:$navigationSafeArgsPluginVersion")
    api("de.mannodermaus.gradle.plugins:android-junit5:${mannodermausAndroidJunit5Version}")
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
val COMMON_KOTLIN_PLUGIN_ID = "common-kotlin-language-plugin"
val COMMON_ANDROID_APP_PLUGIN = "common-android-app-plugin"
val COMMON_ANDROID_LIB_PLUGIN = "common-android-lib-plugin"
val COMMON_SONAR_PLUGIN_ID = "common-sonar-plugin"
val COMMMON_ANDROID_UI_LIB_PLUGIN = "common-android-ui-plugin"
val COMMMON_ANDROID_MENU_UI_LIB_PLUGIN = "common-android-menu-ui-plugin"
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
        create(COMMON_KOTLIN_PLUGIN_ID)
        {
            id = COMMON_KOTLIN_PLUGIN_ID
            displayName = "Kotlin build plugin"
            description = "Common build plugin for Kotlin projects"
            implementationClass = "org.artembogomolova.build.plugins.KotlinPluginApplier"
        }
        create(COMMON_ANDROID_APP_PLUGIN) {
            id = COMMON_ANDROID_APP_PLUGIN
            displayName = "Android app common plugin"
            description = "Android app common plugin"
            implementationClass = "org.artembogomolova.build.plugins.android.CommonAndroidApplicationPlugin"
        }
        create(COMMON_ANDROID_LIB_PLUGIN) {
            id = COMMON_ANDROID_LIB_PLUGIN
            displayName = "Android lib common plugin"
            description = "Android lib common plugin"
            implementationClass = "org.artembogomolova.build.plugins.android.CommonAndroidLibraryPlugin"
        }
        create(COMMON_SONAR_PLUGIN_ID) {
            id = COMMON_SONAR_PLUGIN_ID
            implementationClass = "org.artembogomolova.build.plugins.SonarApplier"
        }
        create(COMMMON_ANDROID_UI_LIB_PLUGIN) {
            id = COMMMON_ANDROID_UI_LIB_PLUGIN
            displayName = "Android ui lib common plugin"
            description = "Android ui lib common plugin"
            implementationClass = "org.artembogomolova.build.plugins.android.CommonAndroidUILibraryPlugin"
        }
        create(COMMMON_ANDROID_MENU_UI_LIB_PLUGIN) {
            id = COMMMON_ANDROID_MENU_UI_LIB_PLUGIN
            displayName = "Android menu ui lib common plugin"
            description = "Android menu ui lib common plugin"
            implementationClass = "org.artembogomolova.build.plugins.android.AndroidMenuUILibraryPlugin"

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
