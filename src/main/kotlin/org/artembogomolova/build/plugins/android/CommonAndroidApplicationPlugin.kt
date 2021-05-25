package org.artembogomolova.build.plugins.android

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.SigningConfig
import com.android.build.gradle.AppPlugin
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.plugins.PluginContainer

class CommonAndroidApplicationPlugin : CommonAndroidPlugin<AppPlugin>(AppPlugin::class.java) {
    companion object {
        const val TARGET_SDK_PROP = "androidTargetSdkVersion"
        const val APPLICATION_ID_PROP = "androidApplicationId"

        /*other plugin ids*/
        const val ANDROID_SAFEARGS_KOTLIN_PLUGIN_ID = "androidx.navigation.safeargs.kotlin"
    }

    override fun configureSpecificExtension(
        extension: Any,
        properties: Map<String, Any>
    ) {
        if (extension !is ApplicationExtension<*, *, *, *, *>) {
            return
        }
        val appExtension = extension as ApplicationExtension<AndroidSourceSet,
                ApplicationBuildType<SigningConfig>,
                ApplicationDefaultConfig<SigningConfig>,
                ApplicationProductFlavor<SigningConfig>,
                SigningConfig>
        with(appExtension) {
            configureAppDefaultConfig(defaultConfig, properties)
            configureBuildTypes(buildTypes, properties)
            configureProductFlavors(productFlavors, properties)
            configureJacoco(jacoco, properties)
            flavorDimensions.add(PRODUCT_FLAVOR_VERSION_DIMENSION)
        }
    }

    private fun configureProductFlavors(productFlavors: NamedDomainObjectContainer<ApplicationProductFlavor<SigningConfig>>, properties: Map<String, Any>) {
        with(productFlavors) {
            create(PRODUCT_FLAVOR_DEMO) {
                applicationIdSuffix = ".$PRODUCT_FLAVOR_DEMO"
                versionNameSuffix = "-$PRODUCT_FLAVOR_DEMO"
                dimension = PRODUCT_FLAVOR_VERSION_DIMENSION
            }
            create(PRODUCT_FLAVOR_FREE) {
                applicationIdSuffix = ".$PRODUCT_FLAVOR_FREE"
                versionNameSuffix = "-$PRODUCT_FLAVOR_FREE"
                dimension = PRODUCT_FLAVOR_VERSION_DIMENSION
                isDefault = true
            }
            create(PRODUCT_FLAVOR_PAYED) {
                applicationIdSuffix = ".$PRODUCT_FLAVOR_PAYED"
                versionNameSuffix = "-$PRODUCT_FLAVOR_PAYED"
                dimension = PRODUCT_FLAVOR_VERSION_DIMENSION
            }
        }
    }

    private fun configureBuildTypes(buildTypes: NamedDomainObjectContainer<ApplicationBuildType<SigningConfig>>, properties: Map<String, Any>) {
        with(buildTypes) {
            getByName(BUILD_TYPE_RELEASE)
            {
                isMinifyEnabled = true
            }
            getByName(BUILD_TYPE_DEBUG) {
                isMinifyEnabled = false
                isDefault = true
            }
        }
    }

    private fun configureAppDefaultConfig(
        defaultConfig: ApplicationDefaultConfig<SigningConfig>,
        properties: Map<String, Any>
    ) {
        with(defaultConfig) {
            multiDexEnabled = true
            targetSdk = Integer.valueOf(properties[TARGET_SDK_PROP] as String)
            applicationId = properties[APPLICATION_ID_PROP] as String
        }
    }

    override fun applyAdditionalPlugins(plugins: PluginContainer, properties: MutableMap<String, Any>) {
        super.applyAdditionalPlugins(plugins, properties)
        plugins.apply(ANDROID_SAFEARGS_KOTLIN_PLUGIN_ID)
    }


}