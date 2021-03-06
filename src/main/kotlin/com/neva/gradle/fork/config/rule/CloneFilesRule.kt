package com.neva.gradle.fork.config.rule

import com.neva.commons.gitignore.GitIgnore
import com.neva.gradle.fork.ForkException
import com.neva.gradle.fork.config.AbstractRule
import com.neva.gradle.fork.config.Config
import org.gradle.api.Action
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.util.PatternSet
import java.io.File

class CloneFilesRule(config: Config) : AbstractRule(config) {

  var defaultFilters = true

  var gitIgnores = true

  val filter = PatternSet()

  private val gitIgnore by lazy { GitIgnore(config.sourceDir) }

  private val sourceTree: FileTree
    get() = config.sourceTree.matching(filter)

  override fun validate() {
    if (config.targetDir.exists()) {
      throw ForkException("Clone target directory already exists: ${config.targetDir.canonicalPath}")
    }
  }

  override fun execute() {
    if (defaultFilters) {
      configureDefaultFilters()
    }

    cloneFiles()
  }

  private fun configureDefaultFilters() {
    filter.exclude(listOf(
      "**/build",
      "**/build/*",
      "**/.gradle",
      "**/.gradle/*",
      "**/.git",
      "**/.git/*"
    ))
  }

  private fun cloneFiles() {
    logger.info("Cloning files from ${config.sourceDir} to ${config.targetDir}")

    visitFiles(sourceTree) { handler, details ->
      if (gitIgnores && gitIgnore.isExcluded(handler.file)) {
        logger.debug("Skipping file ignored by Git: ${handler.file}")
        return@visitFiles
      }

      val target = File(config.targetDir, details.relativePath.pathString)

      handler.copy(target)
    }
  }

  fun filter(options: Action<in PatternSet>) {
    options.execute(filter)
  }

  override fun toString(): String {
    return "CloneFilesRule(gitIgnores=$gitIgnores)"
  }
}
