package com.mkobit.jenkins.pipelines

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import testsupport.NotImplementedYet
import testsupport.writeRelativeFile

@Tag("integration")
internal class SharedLibraryPluginIntegrationTest {
  @Test
  internal fun `main Groovy code is compiled`() {
    val projectDir = createTempDir().apply { deleteOnExit() }
    projectDir.writeRelativeFile(fileName = "build.gradle") {
      groovyBuildScript()
    }
    projectDir.writeRelativeFile("src", "com", "mkobit", fileName = "MyLib.groovy") {
      """
package com.mkobit
class MyLib {
  int add(int a, int b) {
    return a + b
  }
}
"""
    }
    projectDir.writeRelativeFile("test", "unit", "groovy", "com", "mkobit", fileName = "MyLibTest.groovy") {
      """
package com.mkobit

import org.junit.Assert
import org.junit.Test

class MyLibTest {

  @Test
  void checkAddition() {
    def myLib = new MyLib()
    Assert.assertEquals(myLib.add(1, 2), 3)
  }
}
"""
    }

    val buildResult: BuildResult = GradleRunner.create()
      .withPluginClasspath()
      .withArguments("compileGroovy")
      .withProjectDir(projectDir)
      .build()

    val task = buildResult.task(":compileGroovy")
    assertThat(task?.outcome)
      .describedAs("compileGroovy task outcome")
      .isNotNull()
      .isEqualTo(TaskOutcome.SUCCESS)
  }

  @Test
  internal fun `can unit test code in src`() {
    val projectDir = createTempDir().apply { deleteOnExit() }
    projectDir.writeRelativeFile(fileName = "build.gradle") {
      groovyBuildScript()
    }
    projectDir.writeRelativeFile("src", "com", "mkobit", fileName = "MyLib.groovy") {
      """
package com.mkobit
class MyLib {
  int add(int a, int b) {
    return a + b
  }
}
"""
    }
    projectDir.writeRelativeFile("test", "unit", "groovy", "com", "mkobit", fileName = "MyLibTest.groovy") {
      """
package com.mkobit

import org.junit.Assert
import org.junit.Test

class MyLibTest {

  @Test
  void checkAddition() {
    def myLib = new MyLib()
    Assert.assertEquals(myLib.add(1, 2), 3)
  }
}
"""
    }

    val buildResult: BuildResult = GradleRunner.create()
      .withPluginClasspath()
      .withArguments("test")
      .withProjectDir(projectDir)
      .build()

    val task = buildResult.task(":test")
    assertThat(task?.outcome)
      .describedAs("test task outcome")
      .withFailMessage("Build output: ${buildResult.output}")
      .isNotNull()
      .isEqualTo(TaskOutcome.SUCCESS)
  }

  @NotImplementedYet
  @Test
  internal fun `can integration test code in src using @JenkinsRule`() {
  }

  @NotImplementedYet
  @Test
  internal fun `can use declared plugin dependencies in integration test`() {
  }

  @NotImplementedYet
  @Test
  internal fun `@NonCPS can be used in source code`() {
  }

  @NotImplementedYet
  @Test
  internal fun `@Grab in source is supported for trusted libraries`() {
  }

  @NotImplementedYet
  @Test
  internal fun `@Grab not supported for untrusted libraries`() {
  }

  @NotImplementedYet
  @Test
  internal fun `Groovydoc JAR can be generated`() {
  }

  @NotImplementedYet
  @Test
  internal fun `Groovy sources JAR can be generated`() {
  }

  private fun groovyBuildScript(): String = """
plugins {
  id 'com.mkobit.jenkins.pipelines.shared-library'
}

repositories {
  jcenter()
}

dependencies {
  testImplementation(group: 'junit', name: 'junit', version: '4.12')
}
"""

  private fun kotlinBuildScript(): String = """
plugins {
  id("com.mkobit.jenkins.pipelines.shared-library")
}

repositories {
  jcenter()
}

dependencies {
  testImplementation("junit", "junit", "4.12")
}
"""
}