package ru.oiltechclean.assistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.BlurOn
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.oiltechclean.assistant.data.TransformerCatalog
import ru.oiltechclean.assistant.domain.EngineeringCalculators
import ru.oiltechclean.assistant.ui.components.DropdownField
import ru.oiltechclean.assistant.ui.components.HairLine
import ru.oiltechclean.assistant.ui.components.IconTile
import ru.oiltechclean.assistant.ui.components.NumberField
import ru.oiltechclean.assistant.ui.components.ResultTile
import ru.oiltechclean.assistant.ui.components.SectionCard
import ru.oiltechclean.assistant.ui.components.SectionHeader
import ru.oiltechclean.assistant.ui.components.SegmentedSelector
import ru.oiltechclean.assistant.ui.components.SpecRow
import ru.oiltechclean.assistant.ui.theme.BrandGradient
import java.util.Locale

@Composable
fun CalculatorsScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconTile(
                title = "Циклы",
                icon = Icons.Outlined.Speed,
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier.weight(1f)
            )
            IconTile(
                title = "Сорбент",
                icon = Icons.Outlined.BlurOn,
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier.weight(1f)
            )
            IconTile(
                title = "Вакуум",
                icon = Icons.Outlined.Air,
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                modifier = Modifier.weight(1f)
            )
            IconTile(
                title = "tg δ",
                icon = Icons.Outlined.Thermostat,
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconTile(
                title = "Точка росы",
                icon = Icons.Outlined.Opacity,
                selected = selectedTab == 4,
                onClick = { selectedTab = 4 },
                modifier = Modifier.weight(1f)
            )
            IconTile(
                title = "Вспышка",
                icon = Icons.Outlined.LocalFireDepartment,
                selected = selectedTab == 5,
                onClick = { selectedTab = 5 },
                modifier = Modifier.weight(1f)
            )
        }

        when (selectedTab) {
            0 -> UvsmCalculatorCard()
            1 -> SorbentCalculatorCard()
            2 -> VacuumCalculatorCard()
            3 -> TempAndConverterCard()
            4 -> DewPointCalculatorCard()
            else -> FlashPointCalculatorCard()
        }
    }
}

@Composable
private fun UvsmCalculatorCard() {
    var volume by remember { mutableStateOf("25.0") }
    var mStart by remember { mutableStateOf("35.0") }
    var mTarget by remember { mutableStateOf("8.0") }
    var capacity by remember { mutableStateOf("4.0") }
    var selectedTransformer by remember { mutableStateOf("") }

    val result = remember(volume, mStart, mTarget, capacity) {
        EngineeringCalculators.calculateUvsmProcessing(
            oilVolumeM3 = volume.toDoubleOrNull() ?: 0.0,
            initialMoisturePpm = mStart.toDoubleOrNull() ?: 0.0,
            targetMoisturePpm = mTarget.toDoubleOrNull() ?: 0.0,
            unitCapacityM3H = capacity.toDoubleOrNull() ?: 4.0
        )
    }

    SectionCard {
        SectionHeader(
            title = "Циклы и время обработки",
            subtitle = "УВСМ: вакуумная сушка и дегазация",
            icon = Icons.Outlined.Speed
        )
        DropdownField(
            label = "Тип трансформатора (подставить объём)",
            value = selectedTransformer.ifBlank { "Выбрать из справочника" },
            options = TransformerCatalog.transformers.map { "${it.label} — ${formatVolume(it.oilVolumeM3)} м³" },
            onSelect = { label ->
                selectedTransformer = label
                val type = TransformerCatalog.transformers.firstOrNull {
                    label.startsWith(it.label)
                }
                if (type != null) volume = String.format(Locale.US, "%.1f", type.oilVolumeM3)
            }
        )
        NumberField("Объем масла в баке", volume, { volume = it }, suffix = "м³")
        NumberField("Начальное влагосодержание", mStart, { mStart = it }, suffix = "ppm")
        NumberField("Целевое влагосодержание", mTarget, { mTarget = it }, suffix = "ppm")
        NumberField("Производительность установки", capacity, { capacity = it }, suffix = "м³/ч")

        HairLine()

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultTile(
                label = "Циклов",
                value = result.recommendedCycles.toString(),
                modifier = Modifier.weight(1f)
            )
            ResultTile(
                label = "Время",
                value = String.format(Locale.US, "%.1f", result.totalHours),
                unit = "ч",
                modifier = Modifier.weight(1f),
                gradient = BrandGradient
            )
        }
        SpecRow(
            label = "Суммарный объем прокачки",
            value = "${String.format(Locale.US, "%.1f", result.totalVolumeProcessedM3)} м³"
        )
    }
}

@Composable
private fun SorbentCalculatorCard() {
    var massTons by remember { mutableStateOf("20.0") }
    var acidStart by remember { mutableStateOf("0.12") }
    var acidTarget by remember { mutableStateOf("0.03") }
    var sorbentIndex by remember { mutableIntStateOf(0) }
    var selectedTransformer by remember { mutableStateOf("") }
    val isZeolite = sorbentIndex == 1

    val sorbentKg = remember(massTons, acidStart, acidTarget, isZeolite) {
        EngineeringCalculators.calculateSorbentMassKg(
            oilMassTons = massTons.toDoubleOrNull() ?: 0.0,
            initialAcidNumber = acidStart.toDoubleOrNull() ?: 0.0,
            targetAcidNumber = acidTarget.toDoubleOrNull() ?: 0.0,
            isZeoliteDryingOnly = isZeolite
        )
    }

    SectionCard {
        SectionHeader(
            title = "Масса сорбента",
            subtitle = "Силикагель или цеолит NaA",
            icon = Icons.Outlined.BlurOn
        )
        SegmentedSelector(
            options = listOf("Силикагель", "Цеолит"),
            selectedIndex = sorbentIndex,
            onSelect = { sorbentIndex = it }
        )
        DropdownField(
            label = "Тип трансформатора (подставить массу)",
            value = selectedTransformer.ifBlank { "Выбрать из справочника" },
            options = TransformerCatalog.transformers.map { "${it.label} — ${formatVolume(it.oilMassTons)} т" },
            onSelect = { label ->
                selectedTransformer = label
                val type = TransformerCatalog.transformers.firstOrNull {
                    label.startsWith(it.label)
                }
                if (type != null) massTons = String.format(Locale.US, "%.1f", type.oilMassTons)
            }
        )
        NumberField("Масса масла в трансформаторе", massTons, { massTons = it }, suffix = "т")
        if (!isZeolite) {
            NumberField("Начальное кислотное число", acidStart, { acidStart = it }, suffix = "мг КОН/г")
            NumberField("Целевое кислотное число", acidTarget, { acidTarget = it }, suffix = "мг КОН/г")
        }

        HairLine()

        ResultTile(
            label = "Необходимая загрузка",
            value = String.format(Locale.US, "%.0f", sorbentKg),
            unit = "кг",
            modifier = Modifier.fillMaxWidth()
        )
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

    SectionCard {
        SectionHeader(
            title = "Вакуумирование бака",
            subtitle = "Время откачки до заданного давления",
            icon = Icons.Outlined.Air
        )
        NumberField("Свободный объем бака", tankVol, { tankVol = it }, suffix = "м³")
        NumberField("Быстродействие вакуумного блока", pumpSpeed, { pumpSpeed = it }, suffix = "м³/ч")
        NumberField("Требуемое остаточное давление", targetPa, { targetPa = it }, suffix = "Па")

        HairLine()

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultTile(
                label = "Время откачки",
                value = String.format(Locale.US, "%.0f", minutes),
                unit = "мин",
                modifier = Modifier.weight(1f)
            )
            ResultTile(
                label = "В часах",
                value = String.format(Locale.US, "%.1f", minutes / 60.0),
                unit = "ч",
                modifier = Modifier.weight(1f),
                gradient = BrandGradient
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

    SectionCard {
        SectionHeader(
            title = "Температурная коррекция tg δ",
            subtitle = "Приведение к 90 °C",
            icon = Icons.Outlined.Thermostat
        )
        NumberField("Измеренный tg δ", tanMeasured, { tanMeasured = it }, suffix = "%")
        NumberField("Температура измерения", tempMeasured, { tempMeasured = it }, suffix = "°C")
        NumberField("Привести к температуре", tempTarget, { tempTarget = it }, suffix = "°C")

        HairLine()

        ResultTile(
            label = "Приведенный tg δ при $tempTarget °C",
            value = String.format(Locale.US, "%.2f", convertedTan),
            unit = "%",
            modifier = Modifier.fillMaxWidth()
        )
    }

    SectionCard {
        SectionHeader(
            title = "Конвертер вакуума",
            subtitle = "Перевод из мм рт. ст.",
            icon = Icons.Outlined.CompareArrows
        )
        NumberField("Давление", mmHgInput, { mmHgInput = it }, suffix = "мм рт. ст.")

        HairLine()

        pressureMap.forEach { (unit, value) ->
            SpecRow(
                label = unit,
                value = String.format(Locale.US, "%.4f", value)
            )
        }
    }
}

@Composable
private fun DewPointCalculatorCard() {
    var modeIndex by remember { mutableIntStateOf(0) }
    var moisturePpm by remember { mutableStateOf("50") }
    var dewPointC by remember { mutableStateOf("-40") }
    var pressurePa by remember { mutableStateOf("101325") }

    val pressure = pressurePa.toDoubleOrNull() ?: 101325.0
    val calculatedDewPoint = remember(moisturePpm, pressure) {
        EngineeringCalculators.calculateDewPointC(
            moisturePpm = moisturePpm.toDoubleOrNull() ?: 0.0,
            pressurePa = pressure
        )
    }
    val calculatedMoisture = remember(dewPointC, pressure) {
        EngineeringCalculators.moistureFromDewPointPpm(
            dewPointC = dewPointC.toDoubleOrNull() ?: 0.0,
            pressurePa = pressure
        )
    }

    SectionCard {
        SectionHeader(
            title = "Точка росы",
            subtitle = "Связь влагосодержания газа и точки росы",
            icon = Icons.Outlined.Opacity
        )
        SegmentedSelector(
            options = listOf("Влага → точка росы", "Точка росы → влага"),
            selectedIndex = modeIndex,
            onSelect = { modeIndex = it }
        )
        NumberField("Давление в системе", pressurePa, { pressurePa = it }, suffix = "Па")

        if (modeIndex == 0) {
            NumberField("Влагосодержание газа", moisturePpm, { moisturePpm = it }, suffix = "ppm")
            HairLine()
            ResultTile(
                label = "Точка росы",
                value = if (calculatedDewPoint.isNaN()) "—"
                else String.format(Locale.US, "%.1f", calculatedDewPoint),
                unit = "°C",
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            NumberField("Точка росы", dewPointC, { dewPointC = it }, suffix = "°C")
            HairLine()
            ResultTile(
                label = "Влагосодержание газа",
                value = String.format(Locale.US, "%.1f", calculatedMoisture),
                unit = "ppm",
                modifier = Modifier.fillMaxWidth()
            )
        }

        SpecRow(
            label = "Ориентир для вакуумной сушки",
            value = "точка росы ≤ −40 °C"
        )
    }
}

@Composable
private fun FlashPointCalculatorCard() {
    var initialFlash by remember { mutableStateOf("150") }
    var lightFraction by remember { mutableStateOf("3.0") }
    var voltageClassIndex by remember { mutableIntStateOf(1) }

    val estimated = remember(initialFlash, lightFraction) {
        EngineeringCalculators.estimateFlashPointC(
            initialFlashPointC = initialFlash.toDoubleOrNull() ?: 0.0,
            lightFractionPercent = lightFraction.toDoubleOrNull() ?: 0.0
        )
    }
    val limit = if (voltageClassIndex == 0) {
        EngineeringCalculators.flashPointLimitC(35)
    } else {
        EngineeringCalculators.flashPointLimitC(110)
    }
    val isPassing = !estimated.isNaN() && estimated >= limit

    SectionCard {
        SectionHeader(
            title = "Температура вспышки",
            subtitle = "Контроль накопления лёгких фракций",
            icon = Icons.Outlined.LocalFireDepartment
        )
        SegmentedSelector(
            options = listOf("до 35 кВ", "110 кВ и выше"),
            selectedIndex = voltageClassIndex,
            onSelect = { voltageClassIndex = it }
        )
        NumberField("Вспышка свежего масла (паспорт)", initialFlash, { initialFlash = it }, suffix = "°C")
        NumberField("Доля лёгких фракций", lightFraction, { lightFraction = it }, suffix = "%")

        HairLine()

        ResultTile(
            label = "Расчётная вспышка",
            value = if (estimated.isNaN()) "—" else String.format(Locale.US, "%.0f", estimated),
            unit = "°C",
            modifier = Modifier.fillMaxWidth()
        )
        SpecRow(label = "Норматив для класса", value = "не ниже ${formatVolume(limit)} °C")
        SpecRow(
            label = "Оценка",
            value = if (isPassing) "соответствует" else "ниже норматива"
        )
    }
}

private fun formatVolume(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString()
    else String.format(Locale.US, "%.1f", value)
