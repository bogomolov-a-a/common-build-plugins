package org.artembogomolova.build.plugins

import java.nio.charset.StandardCharsets
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.sonarqube.gradle.SonarQubeExtension
import org.sonarqube.gradle.SonarQubePlugin

internal class ExternalToolIntegrationPlugin : Plugin<Project> {
    private val sonarApplier: SonarApplier = SonarApplier()

    override fun apply(target: Project) {
        sonarApplier.apply(target)
    }
}

private class SonarApplier : PluginApplier<SonarQubePlugin>(SonarQubePlugin::class.java) {

    companion object {
        const val PROJECT_KEY_PROPERTY = "sonar.projectKey"
        const val LOGIN_PROPERTY = "sonar.login"
        const val HOST_URL_PROPERTY = "sonar.host.url"
        const val ORGANIZATION_PROPERTY = "sonar.organization"
        const val PROJECT_NAME_PROPERTY = "sonar.projectName"
        const val LANGUAGE_PROPERTY = "sonar.language"
        const val VERBOSE_PROPERTY = "sonar.verbose"
        const val SOURCE_ENCODING_PROPERTY = "sonar.sourceEncoding"
        const val CHECKSTYLE_REPORT_PATH_PROPERTY = "sonar.java.checkstyle.reportPaths"
        const val PMD_REPORT_PATH_PROPERTY = "sonar.java.pmd.reportPaths"
        const val SPOTBUGS_REPORT_PATH_PROPERTY = "sonar.java.spotbugs.reportPaths"
        const val DETEKT_REPORT_PATH_PROPERTY = "sonar.kotlin.detekt.reportPaths"
        const val JACOCO_REPORT_PATH_PROPERTY = "sonar.coverage.jacoco.xmlReportPaths"
        const val REPORT_PATH_PATTERN = "build/reports/%s"
        const val PATH_COMMA_SEPARATED_LIST_PATTERN = "%s,%s"
    }

    override fun configureProperties(properties: MutableMap<String, Any>, target: Project) {
        super.configureProperties(properties, target)
        if (!target.hasProperty(PROJECT_KEY_PROPERTY)) {
            properties[PROJECT_KEY_PROPERTY] = target.rootProject.name
        }
        properties[PROJECT_NAME_PROPERTY] = target.rootProject.name
    }

    override fun isAllowReApplyPluginAfterConfigure(pluginClass: Class<SonarQubePlugin>): Boolean = true
    override fun configureExtensions(target: ExtensionContainer, properties: MutableMap<String, Any>) {
        super.configureExtensions(target, properties)
        configSonarQubeExtension(target, properties)
    }

    private fun configSonarQubeExtension(target: ExtensionContainer, properties: MutableMap<String, Any>) {
        val sonarqubeTask = target.getByName(SonarQubeExtension.SONARQUBE_TASK_NAME) as SonarQubeExtension
        with(sonarqubeTask) {
            properties {
                property(PROJECT_NAME_PROPERTY, properties[PROJECT_NAME_PROPERTY] as String)
                property(PROJECT_KEY_PROPERTY, properties[PROJECT_KEY_PROPERTY] as String)
                property(LOGIN_PROPERTY, properties[LOGIN_PROPERTY] as String)
                property(HOST_URL_PROPERTY, properties[HOST_URL_PROPERTY] as String)
                property(ORGANIZATION_PROPERTY, properties[ORGANIZATION_PROPERTY] as String)
                property(LANGUAGE_PROPERTY, KOTLIN_LANGUAGE_NAME)
                property(VERBOSE_PROPERTY, true.toString())
                property(SOURCE_ENCODING_PROPERTY, StandardCharsets.UTF_8.name())
                val checkStyleReportPaths = PATH_COMMA_SEPARATED_LIST_PATTERN.format(
                    REPORT_PATH_PATTERN.format("checkstyle/main.xml"),
                    REPORT_PATH_PATTERN.format("checkstyle/test.xml")
                )
                property(CHECKSTYLE_REPORT_PATH_PROPERTY, checkStyleReportPaths)
                val pmdReportPaths = PATH_COMMA_SEPARATED_LIST_PATTERN.format(
                    REPORT_PATH_PATTERN.format("pmd/main.xml"),
                    REPORT_PATH_PATTERN.format("pmd/test.xml")
                )
                val detektReportPath = REPORT_PATH_PATTERN.format("detekt/detekt.xml")
                property(DETEKT_REPORT_PATH_PROPERTY, detektReportPath)
                property(PMD_REPORT_PATH_PROPERTY, pmdReportPaths)
                val spotbugsReportPaths = PATH_COMMA_SEPARATED_LIST_PATTERN.format(
                    REPORT_PATH_PATTERN.format("spotbugs/main.xml"),
                    REPORT_PATH_PATTERN.format("spotbugs/test.xml")
                )
                property(SPOTBUGS_REPORT_PATH_PROPERTY, spotbugsReportPaths)
                val jacocoReportPath = REPORT_PATH_PATTERN.format("jacoco/jacoco.xml")
                property(JACOCO_REPORT_PATH_PROPERTY, jacocoReportPath)

            }
        }
    }

}
