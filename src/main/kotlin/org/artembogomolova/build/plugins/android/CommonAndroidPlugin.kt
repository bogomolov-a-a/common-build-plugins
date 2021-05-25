package org.artembogomolova.build.plugins.android

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.CompileOptions
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.JacocoOptions
import com.android.build.api.dsl.LintOptions
import com.android.build.api.dsl.ProductFlavor
import com.android.build.api.dsl.SigningConfig
import com.android.build.api.variant.Variant
import com.android.build.api.variant.VariantProperties
import java.nio.charset.StandardCharsets
import org.artembogomolova.build.plugins.BUILD_DIR_PATH_PROPERTY_NAME
import org.artembogomolova.build.plugins.DetektApplier
import org.artembogomolova.build.plugins.PluginApplier
import org.artembogomolova.build.plugins.SpotBugsApplier
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer

abstract class CommonAndroidPlugin<T : Plugin<out Any>>(clazz: Class<T>) : PluginApplier<T>(clazz) {

    companion object {
        const val BUILD_TOOLS_VERSION_PROP = "androidBuildToolVersion"
        const val COMPILE_SDK_PROP = "androidCompileSdkVersion"
        const val MIN_SDK_PROP = "androidMinSdkVersion"
        const val PROGUARD_RULES_FILE_PATTERN = "%s/proguard-rules.pro"
        const val BUILD_TYPE_RELEASE = "release"
        const val BUILD_TYPE_DEBUG = "debug"
        const val PRODUCT_FLAVOR_DEMO = "demo"
        const val PRODUCT_FLAVOR_FREE = "free"
        const val PRODUCT_FLAVOR_PAYED = "payed"

        /*other plugin ids*/
        const val COMMON_KOTLIN_PLUGIN_ID = "common-kotlin-language-plugin"
        const val KOTLIN_ANDROID_PLUGIN_ID = "kotlin-android"

        /*Configuration list*/
        const val ANDROID_TEST_IMPLEMENTATION_CONFIGURATION = "androidTestImplementation"
        const val ANDROID_TEST_RUNTIME_ONLY_CONFIGURATION = "androidTestRuntimeOnly"

        /*test runnner and it's parameters*/
        const val ANDROID_JUNIT_INSTRUMENTATION_RUNNER_CLASS_NAME = "androidx.test.runner.AndroidJUnitRunner"
        const val RUNNER_BUILDER_PROP_NAME = "runnerBuilder"
        const val RUNNER_BUILDER_VALUE_CLASS_NAME = "de.mannodermaus.junit5.AndroidJUnit5Builder"
        const val JACOCO_VERSION_PROP = "jacocoVersion"

        /*dependency list*/
        const val JUNIT_VERSION_PROP = "junitVersion"
        const val TEST_JUNIT5_DEPENDENCY_NOTATION = "org.junit.jupiter:junit-jupiter-api:%s"
        const val ANDROIDX_TEST_RUNNER_VERSION_PROP = "androidTestRunnerVersion"
        const val ANDROIDX_TEST_RUNNER_NOTATION = "androidx.test:runner:%s"
        const val JUNIT5_ANDROID_TEST_VERSION_RPOP = "androidJunit5TestVersion"
        const val JUNIT5_ANDROID_TEST_RUNNER_NOTATION = "de.mannodermaus.junit5:android-test-runner:%s"
        const val JUNIT5_ANDROID_TEST_CORE_NOTATION = "de.mannodermaus.junit5:android-test-core:%s"
    }

    final override fun isAllowReApplyPluginAfterConfigure(pluginClass: Class<T>): Boolean = true
    final override fun configureExtensions(target: ExtensionContainer, properties: MutableMap<String, Any>) {
        super.configureExtensions(target, properties)
        configureAndroidExtension(target, properties)
    }

    private fun configureAndroidExtension(target: ExtensionContainer, properties: MutableMap<String, Any>) {
        val extension = target.getByName("android")
        if (extension !is CommonExtension<*, *, *, *, *, *, *, *>)
            return
        val commonExtension = extension as CommonExtension<AndroidSourceSet,
                BuildFeatures,
                BuildType,
                DefaultConfig,
                ProductFlavor,
                SigningConfig,
                Variant<VariantProperties>,
                VariantProperties>
        configureCommonSettings(commonExtension, properties)
        configureSpecificExtension(extension, properties)
    }

    abstract fun configureSpecificExtension(extension: Any, properties: Map<String, Any>)

    private fun configureCommonSettings(
        extension: CommonExtension<AndroidSourceSet, BuildFeatures, BuildType, DefaultConfig, ProductFlavor,
                SigningConfig, Variant<VariantProperties>, VariantProperties>,
        properties: MutableMap<String, Any>
    ) {
        with(extension) {
            configureCompileOptions(compileOptions)
            buildToolsVersion = properties[BUILD_TOOLS_VERSION_PROP] as String
            compileSdk = Integer.valueOf(properties[COMPILE_SDK_PROP] as String)
            configureDefaultConfig(defaultConfig, properties)
            configureLintOptions(lintOptions, properties)
        }
    }

    private fun configureLintOptions(lintOptions: LintOptions, properties: MutableMap<String, Any>) {
        with(lintOptions) {
            isAbortOnError = true
            isShowAll = true
            isCheckDependencies = true
            isWarningsAsErrors = true
            isIgnoreWarnings = false
            isCheckTestSources = true
            isCheckReleaseBuilds = true
        }
    }

    private fun configureDefaultConfig(defaultConfig: DefaultConfig, properties: MutableMap<String, Any>) {
        with(defaultConfig) {
            minSdk = Integer.valueOf(properties[MIN_SDK_PROP] as String)
            proguardFiles(
                PROGUARD_RULES_FILE_PATTERN.format(properties[BUILD_DIR_PATH_PROPERTY_NAME])
            )
            testInstrumentationRunner = ANDROID_JUNIT_INSTRUMENTATION_RUNNER_CLASS_NAME
            testInstrumentationRunnerArguments[RUNNER_BUILDER_PROP_NAME] = RUNNER_BUILDER_VALUE_CLASS_NAME
        }
    }

    private fun configureCompileOptions(compileOptions: CompileOptions) {
        with(compileOptions) {
            encoding = StandardCharsets.UTF_8.name()
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    override fun configureDependencies(target: DependencyHandler, properties: MutableMap<String, Any>) {
        super.configureDependencies(target, properties)
        val junitVersion = properties[JUNIT_VERSION_PROP] as String
        val junitDependency = TEST_JUNIT5_DEPENDENCY_NOTATION.format(junitVersion)
        target.add(ANDROID_TEST_IMPLEMENTATION_CONFIGURATION, junitDependency)
        val androidXTestRunnerVersion = properties[ANDROIDX_TEST_RUNNER_VERSION_PROP] as String
        val androidXTestRunnerDependency = ANDROIDX_TEST_RUNNER_NOTATION.format(androidXTestRunnerVersion)
        target.add(ANDROID_TEST_IMPLEMENTATION_CONFIGURATION, androidXTestRunnerDependency)
        val junit5AndroidTestVersion = properties[JUNIT5_ANDROID_TEST_VERSION_RPOP] as String
        val junit5AndroidTestCoreDependency = JUNIT5_ANDROID_TEST_CORE_NOTATION.format(junit5AndroidTestVersion)
        target.add(ANDROID_TEST_IMPLEMENTATION_CONFIGURATION, junit5AndroidTestCoreDependency)
        val junit5AndroidTestRunnerDependency = JUNIT5_ANDROID_TEST_RUNNER_NOTATION.format(junit5AndroidTestVersion)
        target.add(ANDROID_TEST_RUNTIME_ONLY_CONFIGURATION, junit5AndroidTestRunnerDependency)
    }

    override fun applyAdditionalPlugins(plugins: PluginContainer, properties: MutableMap<String, Any>) {
        super.applyAdditionalPlugins(plugins, properties)
        plugins.apply(KOTLIN_ANDROID_PLUGIN_ID)
        /*for all project static analysis plugins*/
        plugins.apply(DetektApplier::class.java)
        plugins.apply(SpotBugsApplier::class.java)
    }

    protected fun configureJacoco(jacoco: JacocoOptions, properties: Map<String, Any>) {
        with(jacoco) {
            version = properties[JACOCO_VERSION_PROP] as String
        }
    }
}
