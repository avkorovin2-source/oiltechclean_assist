# OilTech Assistant — Полный исходный код Android-приложения (MVP) и инструкция по сборке

**Стек технологий:** Kotlin, Jetpack Compose, Material 3, Android SDK 26–35 (100% офлайн-работа).  
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
    val tanDelta90CMax: Double
)

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

    data class HargResult(
        val faultTitle: String,
        val severity: String,
        val explanation: String,
        val recommendedAction: String
    )

    fun analyzeDissolvedGases(
        h2: Double,
        ch4: Double,
        c2h2: Double,
        c2h4: Double,
        c2h6: Double
    ): HargResult {
        val safeH2 = if (h2 <= 0) 0.1 else h2
        val safeCh4 = if (ch4 <= 0) 0.1 else ch4
        val safeC2h4 = if (c2h4 <= 0) 0.1 else c2h4
        val safeC2h6 = if (c2h6 <= 0) 0.1 else c2h6

        val ratioC2h2ToC2h4 = c2h2 / safeC2h4
        val ratioCh4ToH2 = ch4 / safeH2
        val ratioC2h4ToC2h6 = c2h4 / safeC2h6

        return when {
            c2h2 > 5.0 && ratioC2h2ToC2h4 >= 0.1 -> HargResult(
                faultTitle = "Электрическая дуга высокой энергии",
                severity = "КРИТИЧЕСКИЙ УРОВЕНЬ",
                explanation = "Обнаружена повышенная концентрация ацетилена (C2H2 = $c2h2 ppm). Характерно для дуговых разрядов и пробоев изоляции.",
                recommendedAction = "Требуется немедленный вывод трансформатора из работы, ревизия активной части и полная дегазация масла на УВСМ."
            )
            ratioCh4ToH2 < 0.1 && h2 > 100.0 -> HargResult(
                faultTitle = "Частичные разряды (ЧР)",
                severity = "ПОВЫШЕННОЕ ВНИМАНИЕ",
                explanation = "Преобладание водорода (H2 = $h2 ppm) над метаном указывает на ионизацию и частичные разряды в газовых включениях изоляции.",
                recommendedAction = "Провести глубокую вакуумную дегазацию масла на установке УВСМ для удаления растворенных газов и влаги."
            )
            ratioC2h4ToC2h6 > 3.0 && c2h4 > 50.0 -> HargResult(
                faultTitle = "Высокотемпературный перегрев (> 700 °C)",
                severity = "ВЫСОКИЙ РИСК",
                explanation = "Высокое соотношение этилена к этану свидетельствует о сильном локальном перегреве токоведущих соединений или магнитопровода.",
                recommendedAction = "Проверить контактные соединения, систему охлаждения и выполнить регенерацию масла блоком БРМ."
            )
            ratioC2h4ToC2h6 in 1.0..3.0 && ch4 > 80.0 -> HargResult(
                faultTitle = "Термический дефект средней температуры (300–700 °C)",
                severity = "УМЕРЕННЫЙ РИСК",
                explanation = "Повышенное выделение метана и этилена говорит о перегреве маслобарьерной изоляции.",
                recommendedAction = "Учащенный контроль ХАРГ и плановая очистка/дегазация масла."
            )
            else -> HargResult(
                faultTitle = "Явных признаков развивающегося дефекта не выявлено",
                severity = "НОРМА / СТАБИЛЬНО",
                explanation = "Соотношения ключевых газов находятся в допустимых пределах для нормального старения изоляции.",
                recommendedAction = "Продолжать плановый мониторинг согласно регламенту РД."
            )
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

    val gostStandards = listOf(
        GostStandardRow("До 35 кВ (включительно)", 35.0, 25.0, 2.0, 0.10, 15.0),
        GostStandardRow("110 – 150 кВ", 55.0, 15.0, 1.0, 0.05, 10.0),
        GostStandardRow("220 кВ", 60.0, 10.0, 0.5, 0.05, 8.0),
        GostStandardRow("330 – 500 кВ", 65.0, 10.0, 0.5, 0.03, 5.0),
        GostStandardRow("750 кВ", 70.0, 8.0, 0.3, 0.02, 3.0)
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
