package ru.oiltechclean.assistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.oiltechclean.assistant.data.AcceptanceCheckEntity
import ru.oiltechclean.assistant.data.OilAcceptanceChecklist
import ru.oiltechclean.assistant.data.checkedIdSet
import ru.oiltechclean.assistant.model.AcceptanceCheckItem
import ru.oiltechclean.assistant.ui.components.LabeledTextField
import ru.oiltechclean.assistant.ui.components.SectionCard
import ru.oiltechclean.assistant.ui.components.SectionHeader
import ru.oiltechclean.assistant.ui.components.StatusBadge
import ru.oiltechclean.assistant.ui.theme.BrandGradient
import ru.oiltechclean.assistant.ui.theme.BrandIndigo
import ru.oiltechclean.assistant.ui.theme.Danger
import ru.oiltechclean.assistant.ui.theme.DangerSoft
import ru.oiltechclean.assistant.ui.theme.Mist
import ru.oiltechclean.assistant.ui.theme.Slate
import ru.oiltechclean.assistant.ui.theme.Success
import ru.oiltechclean.assistant.ui.theme.SuccessSoft
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AcceptanceChecklistScreen(onBack: () -> Unit) {
    val viewModel: AcceptanceChecklistViewModel = viewModel()
    val checkedIds by viewModel.checkedIds.collectAsState()
    val objectName by viewModel.objectName.collectAsState()
    val history by viewModel.history.collectAsState()

    val approved = OilAcceptanceChecklist.isApproved(checkedIds)
    val criticalTotal = OilAcceptanceChecklist.criticalIds.size
    val missingCritical = OilAcceptanceChecklist.criticalIds.count { it !in checkedIds }
    val canSave = objectName.isNotBlank() && checkedIds.isNotEmpty()

    AcceptanceBackLink(text = "К каталогу установок", onClick = onBack)

    SectionCard {
        SectionHeader(
            title = "Приёмка масла",
            subtitle = "Входной контроль перед заливкой",
            icon = Icons.AutoMirrored.Outlined.FactCheck
        )
        LabeledTextField(
            label = "Объект (ПС, трансформатор)",
            value = objectName,
            onValueChange = viewModel::setObjectName
        )
        VerdictBlock(
            approved = approved,
            missingCritical = missingCritical,
            criticalTotal = criticalTotal
        )
        ProgressBlock(
            done = checkedIds.size,
            total = OilAcceptanceChecklist.totalItems,
            onReset = viewModel::resetChecklist
        )
        Text(
            text = "Отмечайте пункты по мере проверки. Критичные пункты обязательны — без них заливка масла не допускается.",
            style = MaterialTheme.typography.bodySmall,
            color = Slate
        )
    }

    ChecklistGroups(
        checkedIds = checkedIds,
        onToggle = viewModel::toggleItem
    )

    SaveCard(
        canSave = canSave,
        onSave = viewModel::saveCheck
    )

    HistoryCard(
        history = history,
        onDelete = viewModel::deleteCheck
    )
}

@Composable
private fun AcceptanceBackLink(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
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
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = BrandIndigo,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Composable
private fun VerdictBlock(approved: Boolean, missingCritical: Int, criticalTotal: Int) {
    val tint = if (approved) Success else Danger
    val containerColor = if (approved) SuccessSoft else DangerSoft
    val title = if (approved) "Допущено к заливке" else "Не допущено"
    val subtitle = if (approved) {
        "Все критичные пункты отмечены"
    } else {
        "Не отмечено критичных пунктов: $missingCritical из $criticalTotal"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (approved) Icons.Outlined.CheckCircle else Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
        Column(
            modifier = Modifier.padding(start = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = tint
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Slate
            )
        }
    }
}

@Composable
private fun ProgressBlock(done: Int, total: Int, onReset: () -> Unit) {
    val progress = if (total == 0) 0f else done.toFloat() / total

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusBadge(text = "Выполнено $done из $total", color = BrandIndigo)
        Text(
            text = "Сбросить",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = Slate,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onReset)
                .padding(horizontal = 6.dp, vertical = 4.dp)
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape)
            .background(Mist)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(8.dp)
                .clip(CircleShape)
                .background(BrandGradient)
        )
    }
}

@Composable
private fun ChecklistGroups(checkedIds: Set<String>, onToggle: (String) -> Unit) {
    OilAcceptanceChecklist.groups.forEach { group ->
        SectionCard {
            SectionHeader(
                title = group.title,
                icon = Icons.Outlined.Checklist
            )
            group.items.forEach { item ->
                ChecklistRow(
                    item = item,
                    checked = item.id in checkedIds,
                    onToggle = { onToggle(item.id) }
                )
            }
        }
    }
}

@Composable
private fun ChecklistRow(item: AcceptanceCheckItem, checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onToggle)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Нажатие обрабатывает вся строка: если бы чекбокс тоже менял значение, клик сработал бы дважды.
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = BrandIndigo,
                uncheckedColor = Slate
            )
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (checked) Slate else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (checked) TextDecoration.LineThrough else null,
                    modifier = Modifier.weight(1f)
                )
                if (item.isCritical) {
                    StatusBadge(text = "критично", color = Danger)
                }
            }
            Text(
                text = item.detail,
                style = MaterialTheme.typography.bodySmall,
                color = Slate
            )
        }
    }
}

@Composable
private fun SaveCard(canSave: Boolean, onSave: () -> Unit) {
    SectionCard {
        SectionHeader(
            title = "Сохранение",
            subtitle = if (canSave) {
                "Снимок отметок и вердикт попадут в историю"
            } else {
                "Укажите объект и отметьте хотя бы один пункт"
            },
            icon = Icons.Outlined.Description
        )
        Button(
            onClick = onSave,
            enabled = canSave,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandIndigo,
                disabledContainerColor = Mist,
                disabledContentColor = Slate
            )
        ) {
            Text(
                text = "Сохранить в историю",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun HistoryCard(history: List<AcceptanceCheckEntity>, onDelete: (AcceptanceCheckEntity) -> Unit) {
    SectionCard {
        SectionHeader(
            title = "История проверок",
            subtitle = "Сохранённые снимки по объектам",
            icon = Icons.Outlined.History
        )
        if (history.isEmpty()) {
            Text(
                text = "Проверок пока нет. После сохранения здесь появится журнал.",
                style = MaterialTheme.typography.bodySmall,
                color = Slate
            )
        } else {
            history.forEach { check ->
                HistoryRow(check = check, onDelete = { onDelete(check) })
            }
        }
    }
}

@Composable
private fun HistoryRow(check: AcceptanceCheckEntity, onDelete: () -> Unit) {
    val verdictColor = if (check.approved) Success else Danger
    val verdictText = if (check.approved) "Допущено" else "Не допущено"
    val checkedCount = check.checkedIdSet().size

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Mist)
            .padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = check.objectName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${formatCheckDate(check.checkedAt)} · отмечено $checkedCount из ${OilAcceptanceChecklist.totalItems}",
                style = MaterialTheme.typography.bodySmall,
                color = Slate
            )
            StatusBadge(text = verdictText, color = verdictColor)
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = "Удалить проверку",
                tint = Slate
            )
        }
    }
}

private fun formatCheckDate(millis: Long): String =
    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.forLanguageTag("ru-RU")).format(Date(millis))
