package org.artembogomolova.build.tasks

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.ConventionTask
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.TaskAction
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoMerge
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.sonarqube.gradle.SonarQubeExtension

class TaskRegistration {
    companion object {
        /**
         * Task for develop with test and code coverage
         */
        const val BUILD_WITH_COVERAGE_TASK_NAME = "buildWithCoverage"
    }

    fun registerTasks(project: Project) {
        project.tasks.register(BUILD_WITH_COVERAGE_TASK_NAME, BuildWithCoverage::class.java)

    }

}

open class BuildWithCoverage : ConventionTask() {
    init {
        addBasicDependTasks()
        if (isAllowSonarUse()) {
            addSonarSpecifiedTasks()
        }
    }

    private fun addSonarSpecifiedTasks() {
        dependsOn.add(SonarQubeExtension.SONARQUBE_TASK_NAME)
        val subprojectBuildTaskCollection = mutableListOf<Task>()
        project.subprojects.forEach {
            subprojectBuildTaskCollection.add(it.tasks.getByName(BasePlugin.BUILD_GROUP))
        }
        project.tasks.getByName(SonarQubeExtension.SONARQUBE_TASK_NAME) {
            dependsOn.addAll(subprojectBuildTaskCollection)
        }
    }

    private fun addBasicDependTasks() {
        dependsOn.add(JavaBasePlugin.BUILD_TASK_NAME)
        val reportTasks = project.tasks.withType(JacocoReport::class.java)
        dependsOn.addAll(reportTasks)
        val mergeTask = project.tasks.withType(JacocoMerge::class.java)
        dependsOn.addAll(mergeTask)
        val verificationTasks = project.tasks.withType(JacocoCoverageVerification::class.java)
        dependsOn.addAll(verificationTasks)
        verificationTasks.forEach {
            it.dependsOn.addAll(reportTasks)
        }
    }

    private fun isAllowSonarUse(): Boolean = project.rootProject == project

    @TaskAction
    fun execute() {
        println("'${TaskRegistration.BUILD_WITH_COVERAGE_TASK_NAME}' task passed")
    }
}