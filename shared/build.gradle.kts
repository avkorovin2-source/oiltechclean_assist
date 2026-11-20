plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
}

kotlin {
    // Android-таргет нужен только для переиспользования логики в :app.
    android {
        namespace = "ru.oiltechclean.shared"
        compileSdk = 37
        minSdk = 29
        withHostTest {}
    }

    // JVM-таргет: быстрый прогон общих тестов на любой ОС (в т.ч. на Windows).
    jvm()

    // iOS-таргеты: framework Shared.framework собирается на macOS/CI (Codemagic).
    listOf(iosArm64(), iosSimulatorArm64(), iosX64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
