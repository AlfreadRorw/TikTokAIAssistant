pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://artifact.bytedance.com/repository/AwemeOpenSDK")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://artifact.bytedance.com/repository/AwemeOpenSDK")
    }
}
rootProject.name = "TikTokAIAssistant"
include(":app")
