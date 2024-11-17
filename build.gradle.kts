// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
}
buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
    }
}