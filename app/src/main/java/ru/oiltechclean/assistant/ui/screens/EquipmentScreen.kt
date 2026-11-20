package ru.oiltechclean.assistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ru.oiltechclean.assistant.data.OilAcceptanceChecklist
import ru.oiltechclean.assistant.data.OfflineCatalogRepository
import ru.oiltechclean.assistant.model.EquipmentUnit
import ru.oiltechclean.assistant.ui.components.HairLine
import ru.oiltechclean.assistant.ui.components.SectionCard
import ru.oiltechclean.assistant.ui.components.SectionHeader
import ru.oiltechclean.assistant.ui.components.SpecRow
import ru.oiltechclean.assistant.ui.components.StatusBadge
import ru.oiltechclean.assistant.ui.theme.BrandGradient
import ru.oiltechclean.assistant.ui.theme.BrandIndigo
import ru.oiltechclean.assistant.ui.theme.Danger
import ru.oiltechclean.assistant.ui.theme.Slate

@Composable
fun EquipmentScreen() {
    var selectedUnit by remember { mutableStateOf<EquipmentUnit?>(null) }
    var showAcceptance by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        val unit = selectedUnit
        when {
            showAcceptance -> AcceptanceChecklistScreen(onBack = { showAcceptance = false })
            unit == null -> EquipmentList(
                onSelect = { selectedUnit = it },
                onOpenAcceptance = { showAcceptance = true }
            )
            else -> EquipmentDetails(unit = unit, onBack = { selectedUnit = null })
        }
    }
}

@Composable
private fun EquipmentList(
    onSelect: (EquipmentUnit) -> Unit,
    onOpenAcceptance: () -> Unit
) {
    Text(
        text = "Каталог установок ОИЛТЕХКЛИН",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )

    SectionCard(modifier = Modifier.clickable(onClick = onOpenAcceptance)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BrandGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.FactCheck,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Чек-лист приёмки масла",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Входной контроль перед заливкой",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate
                )
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Slate
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusBadge(text = "${OilAcceptanceChecklist.totalItems} пунктов", color = BrandIndigo)
            StatusBadge(text = "${OilAcceptanceChecklist.criticalItems} критичных", color = Danger)
        }
    }

    OfflineCatalogRepository.equipmentList.forEach { unit ->
        SectionCard(modifier = Modifier.clickable { onSelect(unit) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BrandGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Build,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = unit.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = unit.series,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = Slate
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(text = "${unit.capacityM3H} м³/ч", color = BrandIndigo)
                StatusBadge(text = "${unit.heaterPowerKw} кВт", color = Color(0xFF7F5AF0))
            }

            Text(
                text = unit.description,
                style = MaterialTheme.typography.bodySmall,
                color = Slate
            )
        }
    }
}

@Composable
private fun EquipmentDetails(unit: EquipmentUnit, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onBack)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = null,
            tint = BrandIndigo,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "К списку установок",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = BrandIndigo,
            modifier = Modifier.padding(start = 6.dp)
        )
    }

    SectionCard {
        Text(
            text = unit.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        StatusBadge(text = unit.series, color = Color(0xFF7F5AF0))
        Text(
            text = unit.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Slate
        )
        HairLine()
        SpecRow("Производительность", "${unit.capacityM3H} м³/ч")
        SpecRow("Мощность нагревателя", "${unit.heaterPowerKw} кВт")
        SpecRow("Остаточная влага", "≤ ${unit.residualMoisturePpm} ppm")
        SpecRow("Остаточное газосодержание", "≤ ${unit.residualGasPercent} %")
    }

    SectionCard {
        SectionHeader(
            title = "Чек-лист предпусковой проверки",
            subtitle = "Отметьте выполненные пункты",
            icon = Icons.Outlined.Checklist
        )
        unit.startupChecklist.forEach { step ->
            var checked by remember(step) { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { checked = !checked }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = BrandIndigo,
                        uncheckedColor = Slate
                    )
                )
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (checked) Slate else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (checked) TextDecoration.LineThrough else null,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }

    SectionCard {
        SectionHeader(
            title = "Аварийный помощник",
            subtitle = "Типовые неисправности и решения",
            icon = Icons.Outlined.ReportProblem
        )
        unit.troubleshooting.forEachIndexed { index, (symptom, fix) ->
            if (index > 0) HairLine()
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Danger)
                )
                Column(
                    modifier = Modifier.padding(start = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = symptom,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = fix,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate
                    )
                }
            }
        }
    }
}

