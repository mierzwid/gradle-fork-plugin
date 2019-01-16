package com.neva.gradle.fork

import com.neva.gradle.fork.tasks.Fork
import com.neva.gradle.fork.tasks.Props
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Allows to define and execute forking configurations and also use base API.
 *
 * Dedicated to be used only at root project. For subprojects, apply plugin 'com.neva.fork.props'.
 */
open class ForkPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    with(project) {
      plugins.apply(PropsPlugin::class.java)

      extensions.create(
        ForkExtension.NAME, ForkExtension::class.java,
        project, project.extensions.getByType(PropsExtension::class.java)
      )

      tasks.register(Fork.NAME, Fork::class.java)
      tasks.register(Props.NAME, Props::class.java)
    }
  }

}
