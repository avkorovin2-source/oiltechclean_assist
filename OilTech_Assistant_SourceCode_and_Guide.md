# OilTech Assistant — Полный исходный код Android-приложения (MVP) и инструкция по сборке

**Стек технологий:** Kotlin, Jetpack Compose, Material 3, Android SDK 26–35 (офлайн-работа; сеть нужна только для отправки обратной связи — см. `FEEDBACK.md`).  
**Пакет приложения:** `ru.oiltechclean.assistant`

---

## Содержание
1. [Пошаговая инструкция по сборке APK в Android Studio](#1-пошаговая-инструкция-по-сборке-apk-в-android-studio)
2. [Структура файлов проекта](#2-структура-файлов-проекта)
3. [Файлы конфигурации Gradle и Манифест](#3-файлы-конфигурации-gradle-и-манифест)
4. [Исходный код приложения (Kotlin + Jetpack Compose)](#4-исходный-код-приложения-kotlin--jetpack-compose)

---

## 1. Пошаговая инструкция по сборке APK в Android Studio

### Шаг 1. Подготовка среды
1. Скачайте и установите **Android Studio** (версию Ladybug / Meerkat или новее) с официального сайта разработчиков Android.
2. При первом запуске дождитесь загрузки **Android SDK** (минимум API 26, целевой API 34/35).

### Шаг 2. Создание нового проекта
1. Откройте Android Studio и нажмите **New Project**.
2. Выберите шаблон **Empty Activity** (с иконкой Jetpack Compose) и нажмите **Next**.
3. Заполните параметры проекта:
   * **Name:** `OilTech Assistant`
   * **Package name:** `ru.oiltechclean.assistant`
   * **Language:** `Kotlin`
   * **Minimum SDK:** `API 26 ("Oreo"; Android 8.0)`
   * **Build configuration language:** `Kotlin DSL (build.gradle.kts)`
4. Нажмите **Finish** и дождитесь завершения первичной синхронизации Gradle (1–3 минуты).

### Шаг 3. Замена конфигурации и добавление кода
1. Переключите дерево проекта слева в режим **Project** (вместо *Android*).
2. Замените содержимое файлов `AndroidManifest.xml`, `settings.gradle.kts`, корневого `build.gradle.kts` и `app/build.gradle.kts` кодом из **Раздела 3**.
3. Нажмите кнопку **Sync Now** в верхней плашке редактора.
4. Перейдите в папку `app/src/main/java/ru/oiltechclean/assistant/` и создайте подпапки и файлы согласно структуре из **Раздела 2**, скопировав в них соответствующий код из **Раздела 4**.

### Шаг 4. Сборка установочного APK-файла
1. В верхнем меню Android Studio выберите **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
2. После завершения сборки в правом нижнем углу появится уведомление *APK(s) generated successfully*. Нажмите **locate**.
3. Готовый файл `app-debug.apk` находится по пути:  
   `app/build/outputs/apk/debug/app-debug.apk`
4. Скопируйте `app-debug.apk` на любой Android-смартфон и установите (разрешив установку из неизвестных источников).

---

## 2. Структура файлов проекта

```text
OilTechAssistant/
├── settings.gradle.kts
├── build.gradle.kts
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        └── java/ru/oiltechclean/assistant/
            ├── MainActivity.kt
            ├── ui/theme/
            │   └── Theme.kt
            ├── model/
            │   └── DomainModels.kt
            ├── domain/
            │   ├── EngineeringCalculators.kt
            │   └── HargDiagnostics.kt
            ├── data/
            │   └── OfflineCatalogRepository.kt
            └── ui/screens/
                ├── CalculatorsScreen.kt
                ├── DiagnosticsScreen.kt
                ├── EquipmentScreen.kt
                └── JournalScreen.kt
```

---

## 3. Файлы конфигурации Gradle и Манифест

### Файл: `settings.gradle.kts`
```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "OilTechAssistant"
include(":app")
```

### Файл: `build.gradle.kts` (корневой)
```kotlin
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}
```

### Файл: `app/build.gradle.kts`
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "ru.oiltechclean.assistant"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.oiltechclean.assistant"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0-MVP"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
}
```

### Файл: `app/src/main/AndroidManifest.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:label="OilTech Assistant"
        android:supportsRtl="true"
        android:theme="@android:style/Theme.Material.Light.NoActionBar">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

---

## 4. Исходный код приложения (Kotlin + Jetpack Compose)

### Файл: `app/src/main/java/ru/oiltechclean/assistant/ui/theme/Theme.kt`
```kotlin
package ru.oiltechclean.assistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val IndustrialNavy = Color(0xFF0F2942)
val IndustrialBlue = Color(0xFF1E56A0)
val IndustrialAmber = Color(0xFFD97706)
val SurfaceLight = Color(0xFFF4F6F9)
val CardWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF111827)
val TextSecondary = Color(0xFF4B5563)

private val LightColors = lightColorScheme(
    primary = IndustrialNavy,
    onPrimary = Color.White,
    secondary = IndustrialBlue,
    onSecondary = Color.White,
    tertiary = IndustrialAmber,
    background = SurfaceLight,
    surface = CardWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFE5E7EB)
)

@Composable
fun OilTechTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
```

### Файл: `app/src/main/java/ru/oiltechclean/assistant/model/DomainModels.kt`
```kotlin
package ru.oiltechclean.assistant.model

data class EquipmentUnit(
    val id: String,
    val name: String,
    val series: String,
    val capacityM3H: Double,
    val heaterPowerKw: Double,
    val residualMoisturePpm: Double,
    val residualGasPercent: Double,
    val description: String,
    val startupChecklist: List<String>,
    val troubleshooting: List<Pair<String, String>>
)

data class GostStandardRow(
    val voltageClass: String,
    val breakdownVoltageKvMin: Double,
    val moisturePpmMax: Double,
    val gasContentPercentMax: Double,
    val acidNumberMax: Double,
    val tanDelta90CMax: Double,
    /** Категория масла, для которой действуют пределы строки. */
    val category: String
)

/** Масло в работе на оборудовании — пределы РД 34.45-51.300-97. */
const val GOST_CATEGORY_OPERATION = "В эксплуатации"
/** Свежее масло при приёмке перед заливкой — пределы ГОСТ 982-80 / приёмка. */
const val GOST_CATEGORY_FRESH_OIL = "Перед заливкой / свежее масло"

data class FieldLogEntry(
    val id: Long,
    val substationName: String,
    val transformerName: String,
    val unitModel: String,
    val moistureBefore: Double,
    val moistureAfter: Double,
    val breakdownBefore: Double,
    val breakdownAfter: Double,
    val timestamp: String
)
```

### Файл: `app/src/main/java/ru/oiltechclean/assistant/domain/EngineeringCalculators.kt`
```kotlin
package ru.oiltechclean.assistant.domain

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max

object EngineeringCalculators {

    data class UvsmCalculationResult(
        val recommendedCycles: Int,
        val totalHours: Double,
        val totalVolumeProcessedM3: Double
    )

    fun calculateUvsmProcessing(
        oilVolumeM3: Double,
        initialMoisturePpm: Double,
        targetMoisturePpm: Double,
        unitCapacityM3H: Double,
        passEfficiency: Double = 0.65
    ): UvsmCalculationResult {
        if (oilVolumeM3 <= 0 || unitCapacityM3H <= 0 || initialMoisturePpm <= targetMoisturePpm) {
            return UvsmCalculationResult(1, oilVolumeM3 / max(unitCapacityM3H, 0.1), oilVolumeM3)
        }
        val safeTarget = max(targetMoisturePpm, 1.0)
        val rawCycles = ln(initialMoisturePpm / safeTarget) / ln(1.0 / (1.0 - passEfficiency))
        val cycles = max(1, kotlin.math.ceil(rawCycles).toInt())
        val totalVolume = oilVolumeM3 * cycles
        val hours = totalVolume / unitCapacityM3H
        return UvsmCalculationResult(
            recommendedCycles = cycles,
            totalHours = hours,
            totalVolumeProcessedM3 = totalVolume
        )
    }

    fun calculateSorbentMassKg(
        oilMassTons: Double,
        initialAcidNumber: Double,
        targetAcidNumber: Double,
        isZeoliteDryingOnly: Boolean
    ): Double {
        if (oilMassTons <= 0) return 0.0
        return if (isZeoliteDryingOnly) {
            oilMassTons * 1000.0 * 0.014
        } else {
            val deltaAcid = max(0.01, initialAcidNumber - targetAcidNumber)
            val percent = (deltaAcid / 0.05) * 1.5
            oilMassTons * 1000.0 * (percent / 100.0).coerceIn(0.01, 0.08)
        }
    }

    fun calculateVacuumTimeMinutes(
        tankFreeVolumeM3: Double,
        pumpSpeedM3H: Double,
        targetPressurePa: Double
    ): Double {
        if (tankFreeVolumeM3 <= 0 || pumpSpeedM3H <= 0) return 0.0
        val pStart = 101325.0
        val pEnd = targetPressurePa.coerceIn(10.0, pStart)
        val hours = 2.4 * (tankFreeVolumeM3 / pumpSpeedM3H) * ln(pStart / pEnd)
        return hours * 60.0
    }

    fun convertTanDelta(
        measuredTanDelta: Double,
        measuredTempC: Double,
        targetTempC: Double
    ): Double {
        val factor = exp(0.034 * (targetTempC - measuredTempC))
        return measuredTanDelta * factor
    }

    fun convertPressureFromMmHg(mmHg: Double): Map<String, Double> {
        val pa = mmHg * 133.322
        return mapOf(
            "Па (Pa)" to pa,
            "мбар (mbar)" to pa / 100.0,
            "кПа (kPa)" to pa / 1000.0,
            "атм (atm)" to mmHg / 760.0
        )
    }
}
```

### Файл: `app/src/main/java/ru/oiltechclean/assistant/domain/HargDiagnostics.kt`
```kotlin
package ru.oiltechclean.assistant.domain

object HargDiagnostics {

    /** Граничные концентрации растворенных газов, ppm (Табл. 2 РД 153-34.0-46.302-00). */
    private const val BOUNDARY_H2_PPM = 100.0
    private const val BOUNDARY_CH4_PPM = 100.0
    private const val BOUNDARY_C2H2_PPM = 10.0
    private const val BOUNDARY_C2H4_PPM = 100.0
    private const val BOUNDARY_C2H6_PPM = 50.0
    private const val CO2_CO_PAPER_LIMIT = 13.0
    private const val HIGH_VOLTAGE_KV = 110

    private val SEVERITY_RANK = mapOf(
        "НОРМА / СТАБИЛЬНО" to 0,
        "ПОВЫШЕННОЕ ВНИМАНИЕ" to 1,
        "УМЕРЕННЫЙ РИСК" to 2,
        "ВЫСОКИЙ РИСК" to 3,
        "КРИТИЧЕСКИЙ УРОВЕНЬ" to 4
    )

    data class HargResult(
        val faultTitle: String,
        val severity: String,
        val explanation: String,
        val recommendedAction: String,
        val duvalZone: String,
        val duvalInterpretation: String,
        val boundaryExceedances: List<String>,
        val paperInsulationSuspected: Boolean
    )

    /**
     * Экспресс-диагностика по растворённым газам (ХАРГ).
     *
     * @param h2, ch4, c2h2, c2h4, c2h6 концентрации газов, ppm.
     * @param co, co2 концентрации оксидов углерода, ppm — нужны для оценки бумажной изоляции (CO2/CO).
     * @param voltageClassKv класс напряжения оборудования, кВ (0 — не указан).
     */
    fun analyzeDissolvedGases(
        h2: Double,
        ch4: Double,
        c2h2: Double,
        c2h4: Double,
        c2h6: Double,
        co: Double = 0.0,
        co2: Double = 0.0,
        voltageClassKv: Int = 0
    ): HargResult {
        val safeH2 = if (h2 <= 0) 0.1 else h2
        val safeCh4 = if (ch4 <= 0) 0.1 else ch4
        val safeC2h4 = if (c2h4 <= 0) 0.1 else c2h4
        val safeC2h6 = if (c2h6 <= 0) 0.1 else c2h6

        val ratioCh4ToH2 = ch4 / safeH2
        val ratioC2h4ToC2h6 = c2h4 / safeC2h6
        val ratioC2h2ToC2h4 = c2h2 / safeC2h4

        val exceedances = boundaryExceedances(h2, ch4, c2h2, c2h4, c2h6)
        val paperInsulationSuspected = co > 0.0 && co2 > 0.0 && co2 / co >= CO2_CO_PAPER_LIMIT

        // Треугольник Дюваля 1 (IEEE C57.104-2019 §6.2.3)
        val (duvalZoneCode, duvalInterpretation) = when {
            (ch4 + c2h4 + c2h2) > 0.0 -> {
                val sum = ch4 + c2h4 + c2h2
                duvalTriangle1(
                    ch4 * 100.0 / sum,
                    c2h4 * 100.0 / sum,
                    c2h2 * 100.0 / sum
                )
            }
            else -> "—" to "Невозможно рассчитать (CH4 + C2H4 + C2H2 = 0)"
        }

        val diagnosed = diagnose(
            h2, ch4, c2h2, c2h4, c2h6,
            ratioCh4ToH2, ratioC2h4ToC2h6, ratioC2h2ToC2h4,
            exceedances, voltageClassKv
        )
        val title = diagnosed.first
        val (severity, action, description) = diagnosed.second

        var finalSeverity = severity
        var finalTitle = title
        val explanationParts = mutableListOf<String>()
        explanationParts += description

        if (exceedances.isNotEmpty()) {
            explanationParts += "Превышены граничные концентрации Таблицы 2 РД 153-34.0-46.302-00: ${exceedances.joinToString(", ")}."
        }

        if (co > 0.0 && co2 > 0.0) {
            val ratio = co2 / co
            val paperVerdict = if (paperInsulationSuspected) {
                "подозрение на термическую деструкцию целлюлозы (поражение твёрдой изоляции)"
            } else {
                "твёрдая (бумажная) изоляция не поражена"
            }
            explanationParts += "Отношение CO₂/CO = ${formatNumber(ratio)} — $paperVerdict (порог по п. 5.3 РД: $CO2_CO_PAPER_LIMIT)."
            if (paperInsulationSuspected && SEVERITY_RANK[finalSeverity]!! < SEVERITY_RANK["ПОВЫШЕННОЕ ВНИМАНИЕ"]!!) {
                finalSeverity = "ПОВЫШЕННОЕ ВНИМАНИЕ"
                finalTitle = "Подозрение на поражение твёрдой изоляции (целлюлозы)"
            }
        } else {
            explanationParts += "CO/CO₂ не указаны — оценка твёрдой изоляции по отношению CO₂/CO невозможна."
        }

        explanationParts += "По Треугольнику Дюваля 1: $duvalInterpretation (зона $duvalZoneCode)."

        return HargResult(
            faultTitle = finalTitle,
            severity = finalSeverity,
            explanation = explanationParts.joinToString(" "),
            recommendedAction = action,
            duvalZone = duvalZoneCode,
            duvalInterpretation = duvalInterpretation,
            boundaryExceedances = exceedances,
            paperInsulationSuspected = paperInsulationSuspected
        )
    }

    /** Вызов без CO/CO2 и без привязки к классу напряжения — сохранён для обратной совместимости. */
    fun analyzeDissolvedGases(
        h2: Double,
        ch4: Double,
        c2h2: Double,
        c2h4: Double,
        c2h6: Double
    ): HargResult = analyzeDissolvedGases(
        h2 = h2, ch4 = ch4, c2h2 = c2h2, c2h4 = c2h4, c2h6 = c2h6,
        co = 0.0, co2 = 0.0, voltageClassKv = 0
    )

    private fun diagnose(
        h2: Double,
        ch4: Double,
        c2h2: Double,
        c2h4: Double,
        c2h6: Double,
        ratioCh4ToH2: Double,
        ratioC2h4ToC2h6: Double,
        ratioC2h2ToC2h4: Double,
        exceedances: List<String>,
        voltageClassKv: Int
    ): Pair<String, Triple<String, String, String>> { // title, (severity, action, description)
        val c2h2Note = if (c2h2 > 0.0) " (C₂H₂ = $c2h2 ppm)" else ""
        return when {
            // 1. Дуга высокой энергии — ацетилен выше граничной концентрации.
            c2h2 > BOUNDARY_C2H2_PPM -> "Электрическая дуга высокой энергии" to Triple(
                "КРИТИЧЕСКИЙ УРОВЕНЬ",
                "Требуется немедленный вывод трансформатора из работы, ревизия активной части и полная дегазация масла на УВСМ.",
                "Обнаружена повышенная концентрация ацетилена (C₂H₂ = $c2h2 ppm, выше границы ${BOUNDARY_C2H2_PPM} ppm). " +
                    "Характерно для дуговых разрядов и пробоев изоляции. Отношение C₂H₂/C₂H₄ = ${formatNumber(ratioC2h2ToC2h4)}."
            )
            // 2. Высоковольтное оборудование: любое присутствие ацетилена значимо.
            voltageClassKv >= HIGH_VOLTAGE_KV && c2h2 > 0.0 -> "Подозрение на электрические разряды" to Triple(
                "ПОВЫШЕННОЕ ВНИМАНИЕ",
                "Углубить контроль ХАРГ, проверить изоляцию активной части и заземление; при подтверждении роста ацетилена — вывод в ремонт.",
                "Для оборудования класса напряжения ${voltageClassKv} кВ любое присутствие ацетилена$c2h2Note считается значимым, " +
                    "даже ниже граничной концентрации ${BOUNDARY_C2H2_PPM} ppm."
            )
            // 3. Частичные разряды — преобладание водорода.
            ratioCh4ToH2 < 0.1 && h2 > 100.0 -> "Частичные разряды (ЧР)" to Triple(
                "ПОВЫШЕННОЕ ВНИМАНИЕ",
                "Провести глубокую вакуумную дегазацию масла на установке УВСМ для удаления растворенных газов и влаги.",
                "Преобладание водорода (H₂ = $h2 ppm) над метаном указывает на ионизацию и частичные разряды " +
                    "в газовых включениях изоляции.$c2h2Note"
            )
            // 4. Термический дефект высоких температур.
            ratioC2h4ToC2h6 > 3.0 && c2h4 > 50.0 -> "Высокотемпературный перегрев (> 700 °C)" to Triple(
                "ВЫСОКИЙ РИСК",
                "Проверить контактные соединения, систему охлаждения и выполнить регенерацию масла блоком БРМ.",
                "Высокое соотношение этилена к этану свидетельствует о сильном локальном перегреве " +
                    "токоведущих соединений или магнитопровода.$c2h2Note"
            )
            // 5. Термический дефект средних температур.
            ratioC2h4ToC2h6 in 1.0..3.0 && ch4 > 80.0 -> "Термический дефект средней температуры (300–700 °C)" to Triple(
                "УМЕРЕННЫЙ РИСК",
                "Учащенный контроль ХАРГ и плановая очистка/дегазация масла.",
                "Повышенное выделение метана и этилена говорит о перегреве маслобарьерной изоляции.$c2h2Note"
            )
            // 6. Ацетилен обнаружен, но ниже граничной концентрации — всегда значимо.
            c2h2 > 0.0 -> "Ацетилен обнаружен — ниже граничной концентрации" to Triple(
                "ПОВЫШЕННОЕ ВНИМАНИЕ",
                "Повторить анализ через краткий интервал для оценки динамики; при росте ацетилена — вывод в ремонт.",
                "Обнаружен ацетилен$c2h2Note. Присутствие ацетилена всегда значимо, даже ниже граничной " +
                    "концентрации ${BOUNDARY_C2H2_PPM} ppm."
            )
            // 7. Превышение иных граничных концентраций без конкретной картины дефекта.
            exceedances.isNotEmpty() -> "Превышение граничных концентраций газов" to Triple(
                "ПОВЫШЕННОЕ ВНИМАНИЕ",
                "Сравнить с предыдущими замерами; при подтверждении тренда — очистка/дегазация масла на УВСМ.",
                "Превышены граничные концентрации Таблицы 2 РД 153-34.0-46.302-00: ${exceedances.joinToString(", ")}."
            )
            else -> "Явных признаков развивающегося дефекта не выявлено" to Triple(
                "НОРМА / СТАБИЛЬНО",
                "Продолжать плановый мониторинг согласно регламенту РД.",
                "Соотношения ключевых газов находятся в допустимых пределах для нормального старения изоляции."
            )
        }
    }

    private fun boundaryExceedances(
        h2: Double, ch4: Double, c2h2: Double, c2h4: Double, c2h6: Double
    ): List<String> {
        val result = mutableListOf<String>()
        if (h2 > BOUNDARY_H2_PPM) result += "H₂ > ${BOUNDARY_H2_PPM} ppm"
        if (ch4 > BOUNDARY_CH4_PPM) result += "CH₄ > ${BOUNDARY_CH4_PPM} ppm"
        if (c2h2 > BOUNDARY_C2H2_PPM) result += "C₂H₂ > ${BOUNDARY_C2H2_PPM} ppm"
        if (c2h4 > BOUNDARY_C2H4_PPM) result += "C₂H₄ > ${BOUNDARY_C2H4_PPM} ppm"
        if (c2h6 > BOUNDARY_C2H6_PPM) result += "C₂H₆ > ${BOUNDARY_C2H6_PPM} ppm"
        return result
    }

    /**
     * Треугольник Дюваля 1 — границы зон в % от суммы CH4 + C2H4 + C2H2
     * (IEEE C57.104-2019, §6.2.3). Проверка по порядку, первое совпадение определяет зону.
     */
    private fun duvalTriangle1(pCh4: Double, pC2H4: Double, pC2H2: Double): Pair<String, String> =
        when {
            pCh4 >= 98.0 -> "PD" to "Частичные разряды (корона)"
            pCh4 < 98.0 && pC2H4 < 20.0 && pC2H2 < 4.0 -> "T1" to "Термический дефект ниже 300 °C"
            pC2H4 >= 20.0 && pC2H4 < 50.0 && pC2H2 < 4.0 -> "T2" to "Термический дефект 300–700 °C"
            pC2H4 >= 50.0 && pC2H2 < 15.0 -> "T3" to "Термический дефект выше 700 °C"
            pC2H4 < 50.0 && pC2H2 >= 4.0 && pC2H2 < 13.0 -> "DT" to "Смешанный термо-электрический дефект"
            pC2H4 >= 40.0 && pC2H4 < 50.0 && pC2H2 >= 13.0 && pC2H2 < 29.0 -> "DT" to "Смешанный термо-электрический дефект"
            pC2H4 >= 50.0 && pC2H2 >= 15.0 && pC2H2 < 29.0 -> "DT" to "Смешанный термо-электрический дефект"
            pC2H4 < 23.0 && pC2H2 >= 13.0 -> "D1" to "Разряды низкой энергии (искровые)"
            pC2H4 >= 23.0 && pC2H2 >= 29.0 -> "D2" to "Дуговые разряды высокой энергии"
            pC2H4 >= 23.0 && pC2H4 < 40.0 && pC2H2 >= 13.0 && pC2H2 < 29.0 -> "D2" to "Дуговые разряды высокой энергии"
            else -> "—" to "Зона не определена (пограничный случай)"
        }

    private fun formatNumber(value: Double): String {
        val rounded = kotlin.math.round(value * 100.0) / 100.0
        val asInt = rounded.toInt()
        return if (rounded == asInt.toDouble()) asInt.toString() else rounded.toString()
    }

    /**
     * Скорость нарастания концентраций газов между двумя замерами, ppm/сутки.
     * Пороги тревожного нарастания уточняются по п. 3.2 РД 153-34.0-46.302-00 под
     * конкретное оборудование; метод возвращает расчитанные значения без встроенных порогов.
     */
    data class GasGrowthRate(
        val gas: String,
        val currentPpm: Double,
        val previousPpm: Double,
        val days: Double,
        val ppmPerDay: Double
    )

    fun computeGrowthRates(
        current: Map<String, Double>,
        previous: Map<String, Double>,
        days: Double
    ): List<GasGrowthRate> {
        require(days > 0.0) { "Интервал между замерами должен быть больше нуля" }
        return current.entries
            .filter { it.value > 0 }
            .map { (gas, cur) ->
                val prev = previous[gas] ?: 0.0
                GasGrowthRate(gas, cur, prev, days, (cur - prev) / days)
            }
    }
}
```

### Файл: `app/src/main/java/ru/oiltechclean/assistant/data/OfflineCatalogRepository.kt`
```kotlin
package ru.oiltechclean.assistant.data

import ru.oiltechclean.assistant.model.EquipmentUnit
import ru.oiltechclean.assistant.model.GostStandardRow

object OfflineCatalogRepository {

    val equipmentList = listOf(
        EquipmentUnit(
            id = "uvsm_4",
            name = "УВСМ-4",
            series = "Дегазационная установка",
            capacityM3H = 4.0,
            heaterPowerKw = 45.0,
            residualMoisturePpm = 5.0,
            residualGasPercent = 0.1,
            description = "Установка вакуумной сушки, дегазации, нагрева и фильтрации трансформаторного масла производительностью 4 м³/ч.",
            startupChecklist = listOf(
                "Проверить уровень масла в смотровом окне вакуумного насоса.",
                "Убедиться в правильной фазировке подключения (направление вращения двигателей).",
                "Открыть входной и выходной шаровые краны на магистралях трансформатора.",
                "Проверить герметичность вакуумной колонны и закрытие дренажных клапанов.",
                "Включать ТЭНы нагревателя только после начала циркуляции масла."
            ),
            troubleshooting = listOf(
                "Не набирается рабочий вакуум" to "Проверить уровень и чистоту масла в вакуумном насосе, протянуть фланцевые соединения, проверить закрытие клапана напуска воздуха.",
                "Пенится масло в дегазационной колонне" to "Снизить глубину вакуума (приоткрыть напуск воздуха) на первые 15–20 минут до удаления основного объема газов и влаги.",
                "Сработала тепловая защита нагревателя" to "Проверить расход масла через установку (возможно засорение входного фильтра грубой очистки)."
            )
        ),
        EquipmentUnit(
            id = "uvsm_10",
            name = "УВСМ-10",
            series = "Дегазационная установка",
            capacityM3H = 10.0,
            heaterPowerKw = 96.0,
            residualMoisturePpm = 5.0,
            residualGasPercent = 0.1,
            description = "Высокопроизводительная установка для обслуживания силовых трансформаторов 110–750 кВ.",
            startupChecklist = listOf(
                "Проверить заземление корпуса установки контуром подстанции.",
                "Проверить уровень вакуумного масла в форвакуумном насосе и насосе Рутса.",
                "Проконтролировать фазировку питающего кабеля.",
                "Запустить циркуляцию масла и убедиться в отсутствии воздушных пробок.",
                "Включить ступени нагрева масла до заданной температуры (обычно 55–65 °C)."
            ),
            troubleshooting = listOf(
                "Падение производительности на выходе" to "Проверить перепад давления на фильтрах тонкой очистки. При перепаде > 0.25 МПа заменить картриджи.",
                "Повышенный шум вакуумного агрегата" to "Проверить наличие подсоса воздуха на всасывающей магистрали и уровень смазки в редукторе."
            )
        ),
        EquipmentUnit(
            id = "cum_4",
            name = "ЦУМ-4",
            series = "Цеолитовая установка",
            capacityM3H = 4.0,
            heaterPowerKw = 36.0,
            residualMoisturePpm = 3.0,
            residualGasPercent = 0.2,
            description = "Установка с насыпным сорбентом (цеолит NaA) для глубокой адсорбционной осушки масла без разложения присадок.",
            startupChecklist = listOf(
                "Убедиться, что загруженный цеолит прошел термовакуумную реактивацию.",
                "Проверить целостность сетчатых фильтров на выходе из адсорберов.",
                "Медленно заполнить адсорберы маслом снизу вверх для вытеснения воздуха."
            ),
            troubleshooting = listOf(
                "Влагосодержание на выходе не снижается" to "Сорбент насытился влагой. Требуется цикл термовакуумной регенерации цеолита при 300–350 °C."
            )
        ),
        EquipmentUnit(
            id = "brm_4",
            name = "БРМ-4",
            series = "Блок регенерации масла",
            capacityM3H = 4.0,
            heaterPowerKw = 36.0,
            residualMoisturePpm = 5.0,
            residualGasPercent = 0.2,
            description = "Установка для раскисления, осветления и восстановления диэлектрических свойств состаренного масла.",
            startupChecklist = listOf(
                "Загрузить прокаленный сорбент (силикагель КСКГ или отбеливающую глину) в колонны.",
                "Проверить герметичность крышек адсорберов.",
                "Установить фильтр тонкой очистки (1–3 мкм) на выходе для улавливания пыли сорбента."
            ),
            troubleshooting = listOf(
                "Помутнение масла после блока регенерации" to "Проверить целостность фильтроэлемента на выходе из адсорбера (проскок мелкой фракции сорбента)."
            )
        )
    )

    /**
     * Строки с GOST_CATEGORY_OPERATION — нормы для масла в эксплуатации по РД 34.45-51.300-97.
     * Строки с GOST_CATEGORY_FRESH_OIL — нормы для свежего масла при приёмке перед заливкой
     * (ГОСТ 982-80 / типовая практика входного контроля).
     */
    val gostStandards = listOf(
        // --- Масло в эксплуатации (РД 34.45-51.300-97) ---
        GostStandardRow("До 35 кВ (включительно)", 35.0, 25.0, 2.0, 0.10, 15.0, GOST_CATEGORY_OPERATION),
        GostStandardRow("110 – 150 кВ", 55.0, 15.0, 1.0, 0.05, 10.0, GOST_CATEGORY_OPERATION),
        GostStandardRow("220 кВ", 60.0, 10.0, 0.5, 0.05, 8.0, GOST_CATEGORY_OPERATION),
        GostStandardRow("330 – 500 кВ", 65.0, 10.0, 0.5, 0.03, 5.0, GOST_CATEGORY_OPERATION),
        GostStandardRow("750 кВ", 70.0, 8.0, 0.3, 0.02, 3.0, GOST_CATEGORY_OPERATION),
        // --- Свежее масло при приёмке перед заливкой (ГОСТ 982-80 / приёмка) ---
        GostStandardRow("Свежее масло, до 35 кВ", 35.0, 25.0, 2.0, 0.02, 2.5, GOST_CATEGORY_FRESH_OIL),
        GostStandardRow("Свежее масло, 110 кВ и выше", 55.0, 10.0, 1.0, 0.02, 2.5, GOST_CATEGORY_FRESH_OIL)
    )
}
```

### Файл: `app/src/main/java/ru/oiltechclean/assistant/ui/screens/CalculatorsScreen.kt`
```kotlin
package ru.oiltechclean.assistant.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.oiltechclean.assistant.domain.EngineeringCalculators
import java.util.Locale

@Composable
fun CalculatorsScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("УВСМ Циклы", "Сорбент", "Вакуум БВН", "tg δ / Конвертер")

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 12.dp) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                0 -> UvsmCalculatorCard()
                1 -> SorbentCalculatorCard()
                2 -> VacuumCalculatorCard()
                3 -> TempAndConverterCard()
            }
        }
    }
}

@Composable
private fun UvsmCalculatorCard() {
    var volume by remember { mutableStateOf("25.0") }
    var mStart by remember { mutableStateOf("35.0") }
    var mTarget by remember { mutableStateOf("8.0") }
    var capacity by remember { mutableStateOf("4.0") }

    val result = remember(volume, mStart, mTarget, capacity) {
        EngineeringCalculators.calculateUvsmProcessing(
            oilVolumeM3 = volume.toDoubleOrNull() ?: 0.0,
            initialMoisturePpm = mStart.toDoubleOrNull() ?: 0.0,
            targetMoisturePpm = mTarget.toDoubleOrNull() ?: 0.0,
            unitCapacityM3H = capacity.toDoubleOrNull() ?: 4.0
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Расчет циклов и времени обработки (УВСМ)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            NumberField("Объем масла в баке (м³)", volume) { volume = it }
            NumberField("Начальное влагосодержание (ppm / г/т)", mStart) { mStart = it }
            NumberField("Целевое влагосодержание (ppm / г/т)", mTarget) { mTarget = it }
            NumberField("Производительность установки (м³/ч)", capacity) { capacity = it }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text("Результат расчета:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("• Рекомендуемое число циклов: ${result.recommendedCycles} прог.")
            Text("• Ориентировочное время работы: ${String.format(Locale.US, "%.1f", result.totalHours)} ч.")
            Text("• Суммарный объем прокачки: ${String.format(Locale.US, "%.1f", result.totalVolumeProcessedM3)} м³")
        }
    }
}

@Composable
private fun SorbentCalculatorCard() {
    var massTons by remember { mutableStateOf("20.0") }
    var acidStart by remember { mutableStateOf("0.12") }
    var acidTarget by remember { mutableStateOf("0.03") }
    var isZeolite by remember { mutableStateOf(false) }

    val sorbentKg = remember(massTons, acidStart, acidTarget, isZeolite) {
        EngineeringCalculators.calculateSorbentMassKg(
            oilMassTons = massTons.toDoubleOrNull() ?: 0.0,
            initialAcidNumber = acidStart.toDoubleOrNull() ?: 0.0,
            targetAcidNumber = acidTarget.toDoubleOrNull() ?: 0.0,
            isZeoliteDryingOnly = isZeolite
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Расчет массы сорбента (ЦУМ / БРМ)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = !isZeolite, onClick = { isZeolite = false }, label = { Text("Силикагель (БРМ)") })
                FilterChip(selected = isZeolite, onClick = { isZeolite = true }, label = { Text("Цеолит (ЦУМ)") })
            }
            NumberField("Масса масла в трансформаторе (тонн)", massTons) { massTons = it }
            if (!isZeolite) {
                NumberField("Начальное кислотное число (мг КОН/г)", acidStart) { acidStart = it }
                NumberField("Целевое кислотное число (мг КОН/г)", acidTarget) { acidTarget = it }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text(
                "Необходимая загрузка сорбента: ${String.format(Locale.US, "%.0f", sorbentKg)} кг",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun VacuumCalculatorCard() {
    var tankVol by remember { mutableStateOf("35.0") }
    var pumpSpeed by remember { mutableStateOf("300.0") }
    var targetPa by remember { mutableStateOf("100.0") }

    val minutes = remember(tankVol, pumpSpeed, targetPa) {
        EngineeringCalculators.calculateVacuumTimeMinutes(
            tankFreeVolumeM3 = tankVol.toDoubleOrNull() ?: 0.0,
            pumpSpeedM3H = pumpSpeed.toDoubleOrNull() ?: 1.0,
            targetPressurePa = targetPa.toDoubleOrNull() ?: 100.0
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Расчет вакуумирования бака (БВН)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            NumberField("Свободный объем бака (м³)", tankVol) { tankVol = it }
            NumberField("Быстродействие вакуумного блока (м³/ч)", pumpSpeed) { pumpSpeed = it }
            NumberField("Требуемое остаточное давление (Па)", targetPa) { targetPa = it }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text(
                "Расчетное время откачки: ${String.format(Locale.US, "%.0f", minutes)} мин (${String.format(Locale.US, "%.1f", minutes / 60.0)} ч)",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TempAndConverterCard() {
    var tanMeasured by remember { mutableStateOf("1.2") }
    var tempMeasured by remember { mutableStateOf("45.0") }
    var tempTarget by remember { mutableStateOf("90.0") }
    var mmHgInput by remember { mutableStateOf("1.0") }

    val convertedTan = remember(tanMeasured, tempMeasured, tempTarget) {
        EngineeringCalculators.convertTanDelta(
            measuredTanDelta = tanMeasured.toDoubleOrNull() ?: 0.0,
            measuredTempC = tempMeasured.toDoubleOrNull() ?: 20.0,
            targetTempC = tempTarget.toDoubleOrNull() ?: 90.0
        )
    }
    val pressureMap = remember(mmHgInput) {
        EngineeringCalculators.convertPressureFromMmHg(mmHgInput.toDoubleOrNull() ?: 0.0)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Температурная коррекция tg δ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            NumberField("Измеренный tg δ (%)", tanMeasured) { tanMeasured = it }
            NumberField("Температура измерения (°C)", tempMeasured) { tempMeasured = it }
            NumberField("Привести к температуре (°C)", tempTarget) { tempTarget = it }
            Text(
                "Приведенный tg δ при $tempTarget °C: ${String.format(Locale.US, "%.2f", convertedTan)} %",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Конвертер вакуума (из мм рт. ст.)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            NumberField("Давление (мм рт. ст.)", mmHgInput) { mmHgInput = it }
            pressureMap.forEach { (unit, value) ->
                Text("• $unit: ${String.format(Locale.US, "%.4f", value)}")
            }
        }
    }
}

@Composable
fun NumberField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.replace(',', '.')) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
```

### Файл: `app/src/main/java/ru/oiltechclean/assistant/ui/screens/DiagnosticsScreen.kt`
```kotlin
package ru.oiltechclean.assistant.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.oiltechclean.assistant.data.OfflineCatalogRepository
import ru.oiltechclean.assistant.domain.HargDiagnostics

@Composable
fun DiagnosticsScreen() {
    var selectedSection by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedSection) {
            Tab(selected = selectedSection == 0, onClick = { selectedSection = 0 }, text = { Text("Анализ ХАРГ") })
            Tab(selected = selectedSection == 1, onClick = { selectedSection = 1 }, text = { Text("Нормы ГОСТ / РД") })
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedSection == 0) {
                HargAnalyzerBlock()
            } else {
                GostReferenceBlock()
            }
        }
    }
}

@Composable
private fun HargAnalyzerBlock() {
    var h2 by remember { mutableStateOf("45") }
    var ch4 by remember { mutableStateOf("30") }
    var c2h2 by remember { mutableStateOf("0.5") }
    var c2h4 by remember { mutableStateOf("20") }
    var c2h6 by remember { mutableStateOf("15") }

    val diagnosis = remember(h2, ch4, c2h2, c2h4, c2h6) {
        HargDiagnostics.analyzeDissolvedGases(
            h2 = h2.toDoubleOrNull() ?: 0.0,
            ch4 = ch4.toDoubleOrNull() ?: 0.0,
            c2h2 = c2h2.toDoubleOrNull() ?: 0.0,
            c2h4 = c2h4.toDoubleOrNull() ?: 0.0,
            c2h6 = c2h6.toDoubleOrNull() ?: 0.0
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Экспресс-диагностика по газам (ppm)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            NumberField("Водород (H₂)", h2) { h2 = it }
            NumberField("Метан (CH₄)", ch4) { ch4 = it }
            NumberField("Ацетилен (C₂H₂)", c2h2) { c2h2 = it }
            NumberField("Этилен (C₂H₄)", c2h4) { c2h4 = it }
            NumberField("Этан (C₂H₆)", c2h6) { c2h6 = it }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(diagnosis.severity, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(diagnosis.faultTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(diagnosis.explanation)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text("Рекомендация: ${diagnosis.recommendedAction}", fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun GostReferenceBlock() {
    Text(
        "Предельно допустимые значения показателей масла (РД 34.45-51.300-97)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    OfflineCatalogRepository.gostStandards.forEach { row ->
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(row.voltageClass, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("• Пробивное напряжение (не менее): ${row.breakdownVoltageKvMin} кВ")
                Text("• Влагосодержание (не более): ${row.moisturePpmMax} г/т (ppm)")
                Text("• Газосодержание (не более): ${row.gasContentPercentMax} %")
                Text("• Кислотное число (не более): ${row.acidNumberMax} мг КОН/г")
                Text("• Тангенс угла потерь при 90 °C (не более): ${row.tanDelta90CMax} %")
            }
        }
    }
}
```

### Файл: `app/src/main/java/ru/oiltechclean/assistant/ui/screens/EquipmentScreen.kt`
```kotlin
package ru.oiltechclean.assistant.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.oiltechclean.assistant.data.OfflineCatalogRepository
import ru.oiltechclean.assistant.model.EquipmentUnit

@Composable
fun EquipmentScreen() {
    var selectedUnit by remember { mutableStateOf<EquipmentUnit?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (selectedUnit == null) {
            Text(
                "Оборудование ОИЛТЕХКЛИН (Паспорта и Чек-листы)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OfflineCatalogRepository.equipmentList.forEach { unit ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedUnit = unit }
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${unit.name} — ${unit.series}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Производительность: ${unit.capacityM3H} м³/ч | Нагрев: ${unit.heaterPowerKw} кВт")
                        Text(unit.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        } else {
            val unit = selectedUnit!!
            OutlinedButton(onClick = { selectedUnit = null }) {
                Text("← Назад к списку установок")
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(unit.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(unit.series, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                    Text(unit.description)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("• Производительность: ${unit.capacityM3H} м³/ч")
                    Text("• Мощность нагревателя: ${unit.heaterPowerKw} кВт")
                    Text("• Остаточное влагосодержание: ≤ ${unit.residualMoisturePpm} ppm")
                    Text("• Остаточное газосодержание: ≤ ${unit.residualGasPercent} %")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Чек-лист предпусковой проверки", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    unit.startupChecklist.forEach { step ->
                        var checked by remember(step) { mutableStateOf(false) }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Checkbox(checked = checked, onCheckedChange = { checked = it })
                            Text(step, modifier = Modifier.padding(top = 12.dp))
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Аварийный помощник (Неисправности)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    unit.troubleshooting.forEach { (symptom, fix) ->
                        Text("Симптом: $symptom", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                        Text("Решение: $fix")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}
```

### Файл: `app/src/main/java/ru/oiltechclean/assistant/ui/screens/JournalScreen.kt`
```kotlin
package ru.oiltechclean.assistant.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.oiltechclean.assistant.model.FieldLogEntry
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun JournalScreen() {
    val context = LocalContext.current
    var substation by remember { mutableStateOf("ПС 220 кВ Северная") }
    var transformer by remember { mutableStateOf("АТ-1 (ТДЦ-125000/220)") }
    var unitModel by remember { mutableStateOf("УВСМ-10") }
    var mBefore by remember { mutableStateOf("28.0") }
    var mAfter by remember { mutableStateOf("6.5") }
    var kvBefore by remember { mutableStateOf("42.0") }
    var kvAfter by remember { mutableStateOf("68.0") }

    val entries = remember { mutableStateListOf<FieldLogEntry>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Полевой журнал замеров и протоколов", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = substation, onValueChange = { substation = it }, label = { Text("Объект / Подстанция") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = transformer, onValueChange = { transformer = it }, label = { Text("Трансформатор") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = unitModel, onValueChange = { unitModel = it }, label = { Text("Установка ОИЛТЕХКЛИН") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) { NumberField("Влага ДО (ppm)", mBefore) { mBefore = it } }
                    Box(Modifier.weight(1f)) { NumberField("Влага ПОСЛЕ (ppm)", mAfter) { mAfter = it } }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) { NumberField("Пробив. ДО (кВ)", kvBefore) { kvBefore = it } }
                    Box(Modifier.weight(1f)) { NumberField("Пробив. ПОСЛЕ (кВ)", kvAfter) { kvAfter = it } }
                }

                Button(
                    onClick = {
                        val time = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
                        entries.add(
                            0,
                            FieldLogEntry(
                                id = System.currentTimeMillis(),
                                substationName = substation,
                                transformerName = transformer,
                                unitModel = unitModel,
                                moistureBefore = mBefore.toDoubleOrNull() ?: 0.0,
                                moistureAfter = mAfter.toDoubleOrNull() ?: 0.0,
                                breakdownBefore = kvBefore.toDoubleOrNull() ?: 0.0,
                                breakdownAfter = kvAfter.toDoubleOrNull() ?: 0.0,
                                timestamp = time
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить замер в журнал")
                }
            }
        }

        entries.forEach { entry ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("${entry.substationName} — ${entry.transformerName}", fontWeight = FontWeight.Bold)
                    Text("Дата: ${entry.timestamp} | Установка: ${entry.unitModel}", style = MaterialTheme.typography.bodySmall)
                    Text("Влагосодержание: ${entry.moistureBefore} → ${entry.moistureAfter} ppm")
                    Text("Пробивное напряжение: ${entry.breakdownBefore} → ${entry.breakdownAfter} кВ")
                    OutlinedButton(
                        onClick = {
                            val reportText = """
                                ПРОТОКОЛ ОБРАБОТКИ ТРАНСФОРМАТОРНОГО МАСЛА (ОИЛТЕХКЛИН)
                                Дата: ${entry.timestamp}
                                Объект: ${entry.substationName}
                                Оборудование: ${entry.transformerName}
                                Установка: ${entry.unitModel}
                                Влагосодержание: ДО ${entry.moistureBefore} ppm -> ПОСЛЕ ${entry.moistureAfter} ppm
                                Пробивное напряжение: ДО ${entry.breakdownBefore} кВ -> ПОСЛЕ ${entry.breakdownAfter} кВ
                            """.trimIndent()
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Протокол обработки масла - ${entry.substationName}")
                                putExtra(Intent.EXTRA_TEXT, reportText)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Отправить протокол"))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Поделиться протоколом")
                    }
                }
            }
        }
    }
}
```

### Файл: `app/src/main/java/ru/oiltechclean/assistant/MainActivity.kt`
```kotlin
package ru.oiltechclean.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import ru.oiltechclean.assistant.ui.screens.CalculatorsScreen
import ru.oiltechclean.assistant.ui.screens.DiagnosticsScreen
import ru.oiltechclean.assistant.ui.screens.EquipmentScreen
import ru.oiltechclean.assistant.ui.screens.JournalScreen
import ru.oiltechclean.assistant.ui.theme.OilTechTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OilTechTheme {
                MainAppScaffold()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold() {
    var currentTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "OilTech Assistant",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = "Расчеты") },
                    label = { Text("Расчеты") }
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(Icons.Default.Science, contentDescription = "Диагностика") },
                    label = { Text("Диагностика") }
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = { Icon(Icons.Default.Build, contentDescription = "Оборудование") },
                    label = { Text("Установки") }
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = { Icon(Icons.Default.Description, contentDescription = "Журнал") },
                    label = { Text("Журнал") }
                )
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                0 -> CalculatorsScreen()
                1 -> DiagnosticsScreen()
                2 -> EquipmentScreen()
                3 -> JournalScreen()
            }
        }
    }
}
```
