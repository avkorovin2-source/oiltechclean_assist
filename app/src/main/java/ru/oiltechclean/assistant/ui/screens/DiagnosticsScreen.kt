package ru.oiltechclean.assistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import ru.oiltechclean.assistant.data.OfflineCatalogRepository
import ru.oiltechclean.assistant.domain.HargDiagnostics
import ru.oiltechclean.assistant.model.GostStandardRow
import ru.oiltechclean.assistant.ui.components.DropdownField
import ru.oiltechclean.assistant.ui.components.HairLine
import ru.oiltechclean.assistant.ui.components.NumberField
import ru.oiltechclean.assistant.ui.components.SectionCard
import ru.oiltechclean.assistant.ui.components.SectionHeader
import ru.oiltechclean.assistant.ui.components.SegmentedSelector
import ru.oiltechclean.assistant.ui.components.SpecRow
import ru.oiltechclean.assistant.ui.components.StatusBadge
import ru.oiltechclean.assistant.ui.theme.BrandIndigo
import ru.oiltechclean.assistant.ui.theme.Danger
import ru.oiltechclean.assistant.ui.theme.DangerSoft
import ru.oiltechclean.assistant.ui.theme.Slate
import ru.oiltechclean.assistant.ui.theme.Success
import ru.oiltechclean.assistant.ui.theme.SuccessSoft
import ru.oiltechclean.assistant.ui.theme.Warning
import ru.oiltechclean.assistant.ui.theme.WarningSoft

@Composable
fun DiagnosticsScreen() {
    var selectedSection by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SegmentedSelector(
            options = listOf("ХАРГ диагностика", "Нормы ГОСТ / РД"),
            selectedIndex = selectedSection,
            onSelect = { selectedSection = it }
        )

        if (selectedSection == 0) {
            HargAnalyzerBlock()
        } else {
            GostReferenceBlock()
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
    var co by remember { mutableStateOf("") }
    var co2 by remember { mutableStateOf("") }

    val voltageClassOptions = listOf("Класс не указан", "До 35 кВ", "110–150 кВ", "220–500 кВ", "750 кВ")
    var voltageClassSelection by remember { mutableStateOf(voltageClassOptions.first()) }
    val voltageClassKv = when (voltageClassSelection) {
        "До 35 кВ" -> 35
        "110–150 кВ" -> 150
        "220–500 кВ" -> 500
        "750 кВ" -> 750
        else -> 0
    }

    val diagnosis = remember(h2, ch4, c2h2, c2h4, c2h6, co, co2, voltageClassKv) {
        HargDiagnostics.analyzeDissolvedGases(
            h2 = h2.toDoubleOrNull() ?: 0.0,
            ch4 = ch4.toDoubleOrNull() ?: 0.0,
            c2h2 = c2h2.toDoubleOrNull() ?: 0.0,
            c2h4 = c2h4.toDoubleOrNull() ?: 0.0,
            c2h6 = c2h6.toDoubleOrNull() ?: 0.0,
            co = co.toDoubleOrNull() ?: 0.0,
            co2 = co2.toDoubleOrNull() ?: 0.0,
            voltageClassKv = voltageClassKv
        )
    }

    SectionCard {
        SectionHeader(
            title = "Экспресс-диагностика по газам",
            subtitle = "Концентрации растворенных газов, ppm",
            icon = Icons.Outlined.Science
        )
        NumberField("Водород (H₂)", h2, { h2 = it }, suffix = "ppm")
        NumberField("Метан (CH₄)", ch4, { ch4 = it }, suffix = "ppm")
        NumberField("Ацетилен (C₂H₂)", c2h2, { c2h2 = it }, suffix = "ppm")
        NumberField("Этилен (C₂H₄)", c2h4, { c2h4 = it }, suffix = "ppm")
        NumberField("Этан (C₂H₆)", c2h6, { c2h6 = it }, suffix = "ppm")
        NumberField("Окись углерода (CO)", co, { co = it }, suffix = "ppm")
        NumberField("Диоксид углерода (CO₂)", co2, { co2 = it }, suffix = "ppm")
        DropdownField(
            label = "Класс напряжения оборудования",
            value = voltageClassSelection,
            options = voltageClassOptions,
            onSelect = { voltageClassSelection = it }
        )
    }

    DiagnosisResultCard(diagnosis)
}

@Composable
private fun DiagnosisResultCard(diagnosis: HargDiagnostics.HargResult) {
    val severity = diagnosis.severity.uppercase()
    val (accent, soft, icon) = when {
        severity.contains("КРИТИЧ") || severity.contains("ВЫСОК") ->
            Triple(Danger, DangerSoft, Icons.Outlined.ErrorOutline)
        severity.contains("ВНИМАНИЕ") || severity.contains("УМЕРЕН") ->
            Triple(Warning, WarningSoft, Icons.Outlined.WarningAmber)
        else -> Triple(Success, SuccessSoft, Icons.Outlined.CheckCircle)
    }

    SectionCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(soft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StatusBadge(text = diagnosis.severity, color = accent)
                Text(
                    text = diagnosis.faultTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = diagnosis.explanation,
            style = MaterialTheme.typography.bodyMedium,
            color = Slate
        )

        if (diagnosis.duvalZone != "—") {
            SpecRow(
                label = "Треугольник Дюваля 1",
                value = "${diagnosis.duvalZone} — ${diagnosis.duvalInterpretation}"
            )
        }
        if (diagnosis.boundaryExceedances.isNotEmpty()) {
            SpecRow(
                label = "Превышения границ (Табл. 2 РД)",
                value = diagnosis.boundaryExceedances.joinToString(", ")
            )
        }
        if (diagnosis.paperInsulationSuspected) {
            SpecRow(
                label = "Твёрдая (бумажная) изоляция",
                value = "Подозрение на поражение целлюлозы (CO₂/CO ≥ 13)"
            )
        }

        HairLine()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(soft)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "РЕКОМЕНДАЦИЯ",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = accent
            )
            Text(
                text = diagnosis.recommendedAction,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun GostReferenceBlock() {
    val operationRows = OfflineCatalogRepository.gostStandardsInOperation
    val freshRows = OfflineCatalogRepository.gostStandardsForFreshOil

    SectionCard {
        SectionHeader(
            title = "Масло в эксплуатации",
            subtitle = "Предельные значения по РД 34.45-51.300-97",
            icon = Icons.Outlined.MenuBook
        )
        Text(
            text = "Нормы для трансформаторного масла, находящегося в работе на оборудовании.",
            style = MaterialTheme.typography.bodySmall,
            color = Slate
        )
    }
    operationRows.forEach { row -> normRow(row) }

    SectionCard {
        SectionHeader(
            title = "Свежее масло при приёмке",
            subtitle = "Предельные значения для входного контроля (ГОСТ 982-80)",
            icon = Icons.AutoMirrored.Outlined.FactCheck
        )
        Text(
            text = "Нормы для свежего масла перед заливкой в оборудование. Не смешивать с нормами эксплуатации.",
            style = MaterialTheme.typography.bodySmall,
            color = Slate
        )
    }
    freshRows.forEach { row -> normRow(row) }

    SectionCard {
        Text(
            text = "Важно: это разные категории масла. При одних и тех же показателях протокол " +
                "(масло в эксплуатации) и чек-лист приёмки (свежее масло) могут выносить разный вердикт — " +
                "сверяйтесь со строкой соответствующей категории.",
            style = MaterialTheme.typography.bodySmall,
            color = Slate
        )
    }
}

@Composable
private fun normRow(row: GostStandardRow) {
    SectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = row.voltageClass,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            StatusBadge(text = row.category, color = BrandIndigo)
        }
        HairLine()
        SpecRow("Пробивное напряжение, не менее", "${row.breakdownVoltageKvMin} кВ")
        SpecRow("Влагосодержание, не более", "${row.moisturePpmMax} ppm")
        SpecRow("Газосодержание, не более", "${row.gasContentPercentMax} %")
        SpecRow("Кислотное число, не более", "${row.acidNumberMax} мг КОН/г")
        SpecRow("tg δ при 90 °C, не более", "${row.tanDelta90CMax} %")
    }
}
