package com.neva.gradle.fork.config

import org.apache.commons.io.FileUtils
import org.gradle.api.file.FileVisitDetails
import org.gradle.util.GFileUtils
import java.io.File

class FileHandler(config: Config, val details: FileVisitDetails) {

  private val logger = config.project.logger

  val file = details.file

  val filePath = details.relativePath.pathString

  val actions = mutableListOf<() ->Unit>()

  fun copy(target: File) {
    actions += {
      logger.info("Copying file from $file to $target")

      GFileUtils.parentMkdirs(target)
      FileUtils.copyFile(file, target)
    }
  }

  fun move(targetPath: String) {
    move(File(targetPath))
  }

  fun move(target: File) {
    if (target.exists()) {
      return
    }

    actions += {
      logger.info("Moving file from $file to $target")

      GFileUtils.parentMkdirs(target)
      FileUtils.moveFile(file, target)
    }
  }

  fun read(): String {
    return file.inputStream().bufferedReader().use { it.readText() }
  }

  fun write(content: String) {
    file.printWriter().use { it.print(content) }
  }

  fun replace(search: String, replace: String) {
    val content = read()
    if (content.contains(search)) {
      actions += {
        logger.info("Replacing '$search' with '$replace' in file $file")

        val updatedContent = content.replace(search, replace)
        write(updatedContent)
      }
    }
  }

  fun invoke() {
    actions.forEach { it.invoke() }
    actions.clear()
  }

  override fun toString(): String {
    return file.toString()
  }
}
