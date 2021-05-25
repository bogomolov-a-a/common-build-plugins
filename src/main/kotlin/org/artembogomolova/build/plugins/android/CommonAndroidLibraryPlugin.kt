package org.artembogomolova.build.plugins.android

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.LibraryBuildType
import com.android.build.api.dsl.LibraryDefaultConfig
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.LibraryProductFlavor
import com.android.build.api.dsl.SigningConfig
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.NamedDomainObjectContainer

class CommonAndroidLibraryPlugin : CommonAndroidPlugin<LibraryPlugin>(LibraryPlugin::class.java) {
    override fun configureSpecificExtension(extension: Any, properties: Map<String, Any>) {
        if (extension !is LibraryExtension<*, *, *, *, *>) {
            return
        }
        val appExtension = extension as LibraryExtension<AndroidSourceSet,
                LibraryBuildType<SigningConfig>,
                LibraryDefaultConfig<SigningConfig>,
                LibraryProductFlavor<SigningConfig>,
                SigningConfig>
        with(appExtension) {
            configureLibraryDefaultConfig(defaultConfig, properties)
            configureBuildTypes(buildTypes, properties)
            configureJacoco(jacoco, properties)
        }
    }

    private fun configureLibraryDefaultConfig(defaultConfig: LibraryDefaultConfig<SigningConfig>, properties: Map<String, Any>) {
        with(defaultConfig) {
            multiDexEnabled = true
        }

    }


    private fun configureBuildTypes(buildTypes: NamedDomainObjectContainer<LibraryBuildType<SigningConfig>>, properties: Map<String, Any>) {
        with(buildTypes) {
            getByName(BUILD_TYPE_RELEASE)
            {
                isMinifyEnabled = true
            }
            getByName(BUILD_TYPE_DEBUG) {
                isMinifyEnabled = false
                isTestCoverageEnabled = true
            }
        }
    }
}