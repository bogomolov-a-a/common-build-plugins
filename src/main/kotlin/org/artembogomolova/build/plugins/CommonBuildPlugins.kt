package org.artembogomolova.build.plugins

import org.artembogomolova.build.tasks.TaskRegistration
import org.gradle.api.Plugin
import org.gradle.api.Project

class CommonBuildPlugin : CommonBuildWithoutExternToolPlugin() {

    private val commonExternToolPlugin: CommonExternToolPlugin = CommonExternToolPlugin()
    override fun apply(target: Project) {
        super.apply(target)
        commonExternToolPlugin.apply(target)
    }
}

open class CommonBuildWithoutExternToolPlugin : AbstractBuildPlugin() {
    private val projectLanguagesPlugin: ProjectLanguagesPlugin = ProjectLanguagesPlugin()
    private val staticAnalysisPlugin: StaticAnalysisPlugin = StaticAnalysisPlugin()
    private val codeCoveragePlugin: CodeCoveragePlugin = CodeCoveragePlugin()
    private val documentationPlugin: DocumentationPlugin = DocumentationPlugin()

    override fun apply(target: Project) {
        super.apply(target)
        projectLanguagesPlugin.apply(target)
        staticAnalysisPlugin.apply(target)
        codeCoveragePlugin.apply(target)
        documentationPlugin.apply(target)
    }

}

class CommonExternToolPlugin : AbstractBuildPlugin() {
    private val externalToolIntegrationPlugin: ExternalToolIntegrationPlugin = ExternalToolIntegrationPlugin()

    override fun apply(target: Project) {
        super.apply(target)
        /*'base' plugin applied for task 'build' register*/
        target.plugins.apply("base")
        externalToolIntegrationPlugin.apply(target)
    }

}

open class AbstractBuildPlugin : Plugin<Project> {
    private val repositoryApplier: Plugin<Project> = RepositoryApplier()
    private val taskRegistrations: TaskRegistration = TaskRegistration()

    override fun apply(target: Project) {
        repositoryApplier.apply(target)
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
