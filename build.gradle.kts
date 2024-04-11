plugins {
  id("java")
  id("org.jetbrains.intellij") version "1.17.3"
  id("org.jetbrains.kotlin.jvm") version "1.9.23"
}

group = "com.ylz"
version = "1.0-SNAPSHOT"

repositories {
  maven { url = uri("https://maven.aliyun.com/repository/public/'") }
  maven { url = uri("https://maven.aliyun.com/repository/spring/") }
  maven { url = uri("https://maven.aliyun.com/repository/google/") }
  maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin/") }
  maven { url = uri("https://maven.aliyun.com/repository/spring-plugin/") }
  maven { url = uri("https://maven.aliyun.com/repository/grails-core/") }
  maven { url = uri("https://maven.aliyun.com/repository/apache-snapshots/") }
  mavenCentral()
}

dependencies {
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
}

// See https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2023.2.6")
  plugins.set(listOf( "org.jetbrains.android"))
}

tasks {
  buildSearchableOptions {
    enabled = false
  }

  patchPluginXml {
    version.set("${project.version}")
    sinceBuild.set("232")
    untilBuild.set("241.*")
  }

  compileKotlin {
    kotlinOptions.jvmTarget = "17"
  }

  compileTestKotlin {
    kotlinOptions.jvmTarget = "17"
  }
}