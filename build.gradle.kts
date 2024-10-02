// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript{
    repositories { google() }


dependencies{
    classpath(libs.androidx.navigation.safe.args.gradle.plugin)
    classpath (libs.jetbrains.kotlin.serialization)


}
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.android.library") version "7.3.1" apply false
    id("com.google.devtools.ksp")version "1.9.0-1.0.13" apply false
    id ("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"

}