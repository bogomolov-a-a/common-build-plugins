package org.artembogomolova.build.plugins.android

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.LibraryBuildType
import com.android.build.api.dsl.LibraryDefaultConfig
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.LibraryProductFlavor
import com.android.build.api.dsl.SigningConfig
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.JavaPlugin

open class CommonAndroidLibraryPlugin : CommonAndroidPlugin<LibraryPlugin>(LibraryPlugin::class.java) {
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

open class CommonAndroidUILibraryPlugin : CommonAndroidLibraryPlugin() {

    companion object {
        const val MATERIAL_DESIGN_MIKEPENZ_DRAWER_DEPENDENCY_NOTATION = "com.mikepenz:materialdrawer:%s"
        const val MATERIAL_DESIGN_MIKEPENZ_DRAWER_DEPENDENCY_VERSION_PROP = "materialDrawerVersion"
        const val APPCOMPAT_CORE_DEPENDENCY_NOTATION = "androidx.appcompat:appcompat:%s"
        const val APPCOMPAT_CORE_DEPENDENCY_VERSION_PROP = "appcompatVersion"
        const val CONSTRAINT_LAYOUT_DEPENDENCY_NOTATION = "androidx.constraintlayout:constraintlayout:%s"
        const val CONSTRAINT_LAYOUT_DEPENDENCY_VERSION_PROP = "constraintLayoutVersion"
        const val RECYCLE_VIEW_DEPENDENCY_NOTATION = "androidx.recyclerview:recyclerview:%s"
        const val RECYCLE_VIEW_DEPENDENCY_VERSION_PROP = "recycleViewVersion"
        const val CARD_VIEW_DEPENDENCY_NOTATION = "androidx.cardview:cardview:%s"
        const val CARD_VIEW_DEPENDENCY_VERSION_PROP = "cardViewsVersion"
        const val SLIDING_PANEL_LAYOUT_DEPENDENCY_NOTATION = "androidx.slidingpanelayout:slidingpanelayout:%s"
        const val SLIDING_PANEL_LAYOUT_DEPENDENCY_VERSION_PROP = "slidingPanelLayoutVersion"
    }

    override fun configureDependencies(target: DependencyHandler, properties: MutableMap<String, Any>) {
        super.configureDependencies(target, properties)
        val materialDrawerDependency =
            MATERIAL_DESIGN_MIKEPENZ_DRAWER_DEPENDENCY_NOTATION.format(properties[MATERIAL_DESIGN_MIKEPENZ_DRAWER_DEPENDENCY_VERSION_PROP] as String)
        target.add(JavaPlugin.API_CONFIGURATION_NAME, materialDrawerDependency)
        val appcompatDependency = APPCOMPAT_CORE_DEPENDENCY_NOTATION.format(properties[APPCOMPAT_CORE_DEPENDENCY_VERSION_PROP] as String)
        target.add(JavaPlugin.API_CONFIGURATION_NAME, appcompatDependency)
        val constraintLayoutDependency = CONSTRAINT_LAYOUT_DEPENDENCY_NOTATION.format(properties[CONSTRAINT_LAYOUT_DEPENDENCY_VERSION_PROP] as String)
        target.add(JavaPlugin.API_CONFIGURATION_NAME, constraintLayoutDependency)
        val recycleViewDependency = RECYCLE_VIEW_DEPENDENCY_NOTATION.format(properties[RECYCLE_VIEW_DEPENDENCY_VERSION_PROP] as String)
        target.add(JavaPlugin.API_CONFIGURATION_NAME, recycleViewDependency)
        val cardViewDependency = CARD_VIEW_DEPENDENCY_NOTATION.format(properties[CARD_VIEW_DEPENDENCY_VERSION_PROP] as String)
        target.add(JavaPlugin.API_CONFIGURATION_NAME, cardViewDependency)
        val slidingPanelLayoutDependency = SLIDING_PANEL_LAYOUT_DEPENDENCY_NOTATION.format(properties[SLIDING_PANEL_LAYOUT_DEPENDENCY_VERSION_PROP] as String)
        target.add(JavaPlugin.API_CONFIGURATION_NAME, slidingPanelLayoutDependency)
    }
}

class AndroidMenuUILibraryPlugin : CommonAndroidUILibraryPlugin() {
    companion object {
        const val ICONICS_VIEWS_DEPENDENCY_NOTATION = "com.mikepenz:iconics-views:%s"
        const val ICONICS_VIEWS_DEPENDENCY_VERSION_PROP = "iconicsViewsVersion"
    }

    override fun configureDependencies(target: DependencyHandler, properties: MutableMap<String, Any>) {
        super.configureDependencies(target, properties)
        val iconicsViewsDependency = ICONICS_VIEWS_DEPENDENCY_NOTATION.format(properties[ICONICS_VIEWS_DEPENDENCY_VERSION_PROP] as String)
        target.add(JavaPlugin.API_CONFIGURATION_NAME, iconicsViewsDependency)
    }
}