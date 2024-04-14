pluginManagement {
    repositories {

        maven { url = uri("https://maven.aliyun.com/repository/public/'") }
        maven { url = uri("https://maven.aliyun.com/repository/spring/") }
        maven { url = uri("https://maven.aliyun.com/repository/google/") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin/") }
        maven { url = uri("https://maven.aliyun.com/repository/spring-plugin/") }
        maven { url = uri("https://maven.aliyun.com/repository/grails-core/") }
        maven { url = uri("https://maven.aliyun.com/repository/apache-snapshots/") }

        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "viewbinding_migration"