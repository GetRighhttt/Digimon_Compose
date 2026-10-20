plugins {
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kmp.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.compose.multiplatform) apply false
}

val kotlinFormatter = configurations.create("kotlinFormatter")

dependencies {
    add(kotlinFormatter.name, "com.pinterest.ktlint:ktlint-cli:1.8.0:all@jar")
}

val kotlinSources =
    fileTree(rootDir) {
        include("**/*.kt", "**/*.kts")
        exclude("**/build/**", "**/.gradle/**", "**/.kotlin/**", "**/.idea/**")
    }

tasks.register<JavaExec>("formatKotlin") {
    group = "formatting"
    description = "Formats Kotlin sources and build scripts."
    classpath = kotlinFormatter
    mainClass.set("com.pinterest.ktlint.Main")
    args("--format", "--relative")
    args(kotlinSources.files.sorted().map { it.absolutePath })
}

tasks.register<JavaExec>("checkKotlinFormatting") {
    group = "verification"
    description = "Checks Kotlin formatting without changing files."
    classpath = kotlinFormatter
    mainClass.set("com.pinterest.ktlint.Main")
    args("--relative")
    args(kotlinSources.files.sorted().map { it.absolutePath })
}
