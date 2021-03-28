package org.artembogomolova.build.plugins

import org.artembogomolova.build.tasks.TaskRegistration
import org.gradle.api.Plugin
import org.gradle.api.Project

class CommonBuildPlugin : CommonBuildWithoutExternToolPlugin() {

    private val commonBasePlugin: CommonBasePlugin = CommonBasePlugin()
    override fun apply(target: Project) {
        commonBasePlugin.apply(target)
    }
}

open class CommonBuildWithoutExternToolPlugin : Plugin<Project> {
    private val projectLanguagesPlugin: ProjectLanguagesPlugin = ProjectLanguagesPlugin()
    private val staticAnalysisPlugin: StaticAnalysisPlugin = StaticAnalysisPlugin()
    private val codeCoveragePlugin: CodeCoveragePlugin = CodeCoveragePlugin()
    private val documentationPlugin: DocumentationPlugin = DocumentationPlugin()

    override fun apply(target: Project) {
        projectLanguagesPlugin.apply(target)
        staticAnalysisPlugin.apply(target)
        codeCoveragePlugin.apply(target)
        documentationPlugin.apply(target)

    }

}

class CommonBasePlugin : Plugin<Project> {
    private val repositoryApplier: Plugin<Project> = RepositoryApplier()
    private val externalToolIntegrationPlugin: ExternalToolIntegrationPlugin = ExternalToolIntegrationPlugin()
    private val taskRegistrations: TaskRegistration = TaskRegistration()

    override fun apply(target: Project) {
        repositoryApplier.apply(target)
        externalToolIntegrationPlugin.apply(target)
        taskRegistrations.registerTasks(target)
    }

}

internal class RepositoryApplier : Plugin<Project> {
    override fun apply(target: Project) {
        target.repositories.add(target.repositories.mavenCentral())
        target.repositories.add(target.repositories.gradlePluginPortal())
        target.repositories.add(target.repositories.jcenter())
    }

}
