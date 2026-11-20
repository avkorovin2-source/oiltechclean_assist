package ru.oiltechclean.assistant.ui.screens

import android.app.Application
import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.oiltechclean.assistant.data.AppDatabase
import ru.oiltechclean.assistant.data.JournalRecordEntity
import ru.oiltechclean.assistant.data.OfflineCatalogRepository
import ru.oiltechclean.assistant.domain.GostComplianceEvaluator
import ru.oiltechclean.assistant.ui.components.DropdownField
import ru.oiltechclean.assistant.ui.components.HairLine
import ru.oiltechclean.assistant.ui.components.LabeledTextField
import ru.oiltechclean.assistant.ui.components.NumberField
import ru.oiltechclean.assistant.ui.components.SectionCard
import ru.oiltechclean.assistant.ui.components.SectionHeader
import ru.oiltechclean.assistant.ui.components.StatusBadge
import ru.oiltechclean.assistant.ui.theme.BrandGradient
import ru.oiltechclean.assistant.ui.theme.BrandIndigo
import ru.oiltechclean.assistant.ui.theme.BrandSky
import ru.oiltechclean.assistant.ui.theme.Danger
import ru.oiltechclean.assistant.ui.theme.DangerSoft
import ru.oiltechclean.assistant.ui.theme.Mist
import ru.oiltechclean.assistant.ui.theme.Slate
import ru.oiltechclean.assistant.ui.theme.Success
import ru.oiltechclean.assistant.ui.theme.SuccessSoft
import ru.oiltechclean.assistant.utils.PdfReportGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JournalViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).journalDao()

    private val searchQuery = MutableStateFlow("")
    private val voltageFilter = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val entries: StateFlow<List<JournalRecordEntity>> =
        combine(searchQuery, voltageFilter) { query, voltage -> query to voltage }
            .flatMapLatest { (query, voltage) -> dao.searchRecords(query.trim(), voltage) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val usedVoltageClasses: StateFlow<List<String>> = dao.getUsedVoltageClasses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setVoltageFilter(voltageClass: String) {
        voltageFilter.value = voltageClass
    }

    fun insertRecord(record: JournalRecordEntity) {
        viewModelScope.launch { dao.insertRecord(record) }
    }

    fun deleteRecord(record: JournalRecordEntity) {
        viewModelScope.launch { dao.deleteRecord(record) }
    }
}

private val voltageClassOptions = OfflineCatalogRepository.gostStandards.map { it.voltageClass }

@Composable
fun JournalScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val journalViewModel: JournalViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return JournalViewModel(application) as T
            }
        }
    )

    val entries by journalViewModel.entries.collectAsState()
    val usedClasses by journalViewModel.usedVoltageClasses.collectAsState()

    var substation by remember { mutableStateOf("ПС 220 кВ Северная") }
    var transformer by remember { mutableStateOf("АТ-1 (ТДЦ-125000/220)") }
    var unitModel by remember { mutableStateOf("УВСМ-10") }
    var voltageClass by remember { mutableStateOf(voltageClassOptions[2]) }
    var oilTemperature by remember { mutableStateOf("20") }
    var moistureBefore by remember { mutableStateOf("28.0") }
    var moistureAfter by remember { mutableStateOf("6.5") }
    var breakdownBefore by remember { mutableStateOf("42.0") }
    var breakdownAfter by remember { mutableStateOf("68.0") }
    var acidBefore by remember { mutableStateOf("") }
    var acidAfter by remember { mutableStateOf("") }
    var tanDeltaBefore by remember { mutableStateOf("") }
    var tanDeltaAfter by remember { mutableStateOf("") }
    var gasAfter by remember { mutableStateOf("") }
    var flashPointAfter by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    var showExtended by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var filterClass by remember { mutableStateOf("") }

    val standard = OfflineCatalogRepository.gostStandards.firstOrNull { it.voltageClass == voltageClass }
    val liveReport = remember(
        voltageClass, breakdownAfter, moistureAfter, gasAfter, acidAfter, tanDeltaAfter
    ) {
        standard?.let {
            GostComplianceEvaluator.evaluate(
                standard = it,
                breakdownVoltageKv = breakdownAfter.toDoubleOrNull(),
                moisturePpm = moistureAfter.toDoubleOrNull(),
                gasContentPercent = gasAfter.toDoubleOrNull(),
                acidNumber = acidAfter.toDoubleOrNull(),
                tanDelta90C = tanDeltaAfter.toDoubleOrNull()
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SectionCard {
            SectionHeader(
                title = "Новый замер",
                subtitle = "Параметры до и после обработки",
                icon = Icons.Outlined.Add
            )
            LabeledTextField(
                label = "Объект / Подстанция",
                value = substation,
                onValueChange = { substation = it }
            )
            LabeledTextField(
                label = "Трансформатор",
                value = transformer,
                onValueChange = { transformer = it }
            )
            LabeledTextField(
                label = "Установка ОИЛТЕХКЛИН",
                value = unitModel,
                onValueChange = { unitModel = it }
            )
            DropdownField(
                label = "Класс напряжения",
                value = voltageClass,
                options = voltageClassOptions,
                onSelect = { voltageClass = it }
            )
            NumberField(
                label = "Температура масла при отборе",
                value = oilTemperature,
                onValueChange = { oilTemperature = it },
                suffix = "°C"
            )

            HairLine()

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumberField(
                    label = "Влага ДО",
                    value = moistureBefore,
                    onValueChange = { moistureBefore = it },
                    suffix = "ppm",
                    modifier = Modifier.weight(1f)
                )
                NumberField(
                    label = "Влага ПОСЛЕ",
                    value = moistureAfter,
                    onValueChange = { moistureAfter = it },
                    suffix = "ppm",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumberField(
                    label = "Пробив. ДО",
                    value = breakdownBefore,
                    onValueChange = { breakdownBefore = it },
                    suffix = "кВ",
                    modifier = Modifier.weight(1f)
                )
                NumberField(
                    label = "Пробив. ПОСЛЕ",
                    value = breakdownAfter,
                    onValueChange = { breakdownAfter = it },
                    suffix = "кВ",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showExtended = !showExtended }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showExtended) "Скрыть дополнительные параметры" else "Дополнительные параметры протокола",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandIndigo,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (showExtended) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = BrandIndigo,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (showExtended) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NumberField(
                        label = "Кисл. число ДО",
                        value = acidBefore,
                        onValueChange = { acidBefore = it },
                        suffix = "мг КОН/г",
                        modifier = Modifier.weight(1f)
                    )
                    NumberField(
                        label = "Кисл. число ПОСЛЕ",
                        value = acidAfter,
                        onValueChange = { acidAfter = it },
                        suffix = "мг КОН/г",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NumberField(
                        label = "tg δ ДО",
                        value = tanDeltaBefore,
                        onValueChange = { tanDeltaBefore = it },
                        suffix = "%",
                        modifier = Modifier.weight(1f)
                    )
                    NumberField(
                        label = "tg δ ПОСЛЕ",
                        value = tanDeltaAfter,
                        onValueChange = { tanDeltaAfter = it },
                        suffix = "%",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NumberField(
                        label = "Газосодержание ПОСЛЕ",
                        value = gasAfter,
                        onValueChange = { gasAfter = it },
                        suffix = "%",
                        modifier = Modifier.weight(1f)
                    )
                    NumberField(
                        label = "Вспышка ПОСЛЕ",
                        value = flashPointAfter,
                        onValueChange = { flashPointAfter = it },
                        suffix = "°C",
                        modifier = Modifier.weight(1f)
                    )
                }
                LabeledTextField(
                    label = "Примечания / заключение",
                    value = remarks,
                    onValueChange = { remarks = it }
                )
            }

            if (liveReport != null && liveReport.hasData) {
                HairLine()
                ComplianceSummary(report = liveReport)
            }

            Button(
                onClick = {
                    val now = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
                    val record = JournalRecordEntity(
                        substationName = substation.ifBlank { "Без названия" },
                        transformerName = transformer.ifBlank { "Трансформатор" },
                        unitModel = unitModel.ifBlank { "УВСМ" },
                        recordDate = now,
                        moistureBefore = moistureBefore.toDoubleOrNull() ?: 0.0,
                        moistureAfter = moistureAfter.toDoubleOrNull() ?: 0.0,
                        breakdownBefore = breakdownBefore.toDoubleOrNull() ?: 0.0,
                        breakdownAfter = breakdownAfter.toDoubleOrNull() ?: 0.0,
                        acidNumberBefore = acidBefore.toDoubleOrNull(),
                        acidNumberAfter = acidAfter.toDoubleOrNull(),
                        tanDeltaBefore = tanDeltaBefore.toDoubleOrNull(),
                        tanDeltaAfter = tanDeltaAfter.toDoubleOrNull(),
                        remarks = remarks.takeIf { it.isNotBlank() },
                        voltageClass = voltageClass,
                        oilTemperatureC = oilTemperature.toDoubleOrNull(),
                        gasContentAfter = gasAfter.toDoubleOrNull(),
                        flashPointAfter = flashPointAfter.toDoubleOrNull(),
                        conclusion = liveReport?.let { GostComplianceEvaluator.buildConclusion(it) }
                    )
                    journalViewModel.insertRecord(record)
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandIndigo,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Сохранить замер в журнал",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Записи журнала",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            StatusBadge(text = "${entries.size}", color = BrandIndigo)
        }

        if (entries.isNotEmpty() || searchQuery.isNotBlank() || filterClass.isNotBlank()) {
            SearchAndFilter(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    journalViewModel.setSearchQuery(it)
                },
                selectedClass = filterClass,
                classOptions = usedClasses,
                onClassChange = {
                    filterClass = it
                    journalViewModel.setVoltageFilter(it)
                }
            )
        }

        if (entries.isEmpty()) {
            EmptyJournalState(isFiltered = searchQuery.isNotBlank() || filterClass.isNotBlank())
        } else {
            entries.forEach { entry ->
                JournalEntryCard(
                    entry = entry,
                    onShare = {
                        val pdfFile = PdfReportGenerator.generateReport(context, entry)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "ru.oiltechclean.assistant.fileprovider",
                            pdfFile
                        )
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Отправить PDF-протокол"))
                    },
                    onDelete = { journalViewModel.deleteRecord(entry) }
                )
            }
        }
    }
}

@Composable
private fun SearchAndFilter(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedClass: String,
    classOptions: List<String>,
    onClassChange: (String) -> Unit
) {
    SectionCard {
        LabeledTextField(
            label = "Поиск по объекту, трансформатору, установке",
            value = query,
            onValueChange = onQueryChange
        )
        if (classOptions.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    text = "Все",
                    selected = selectedClass.isEmpty(),
                    onClick = { onClassChange("") }
                )
                classOptions.take(3).forEach { option ->
                    FilterChip(
                        text = option,
                        selected = selectedClass == option,
                        onClick = { onClassChange(option) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) BrandGradient else androidx.compose.ui.graphics.SolidColor(Mist))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else Slate,
            maxLines = 1
        )
    }
}

@Composable
private fun ComplianceSummary(report: GostComplianceEvaluator.ComplianceReport) {
    val isOk = report.isCompliant
    val accent = if (isOk) Success else Danger
    val soft = if (isOk) SuccessSoft else DangerSoft

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(soft)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isOk) Icons.Outlined.CheckCircle else Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = if (isOk) "Соответствует нормам РД" else "Не соответствует нормам РД",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = accent,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        report.checks.filter { it.verdict != GostComplianceEvaluator.Verdict.UNKNOWN }.forEach { check ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = check.parameter,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (check.isFail) "не соотв." else "ок",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (check.isFail) Danger else Success
                )
            }
        }
    }
}

@Composable
private fun EmptyJournalState(isFiltered: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Mist)
            .padding(vertical = 32.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFiltered) Icons.Outlined.Search else Icons.Outlined.Description,
                contentDescription = null,
                tint = Slate,
                modifier = Modifier.size(26.dp)
            )
        }
        Text(
            text = if (isFiltered) "Ничего не найдено" else "Журнал пока пуст",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = if (isFiltered) "Измените запрос или сбросьте фильтр по классу напряжения."
            else "Заполните параметры выше и сохраните первый замер.",
            style = MaterialTheme.typography.bodySmall,
            color = Slate
        )
    }
}

@Composable
private fun JournalEntryCard(
    entry: JournalRecordEntity,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    SectionCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(BrandGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = entry.substationName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = entry.transformerName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusBadge(text = entry.recordDate, color = BrandSky)
            StatusBadge(text = entry.unitModel, color = BrandIndigo)
            entry.voltageClass?.let { StatusBadge(text = it, color = Color(0xFF7F5AF0)) }
        }

        HairLine()

        MetricRow(
            icon = Icons.Outlined.WaterDrop,
            title = "Влагосодержание",
            before = "${entry.moistureBefore} ppm",
            after = "${entry.moistureAfter} ppm",
            improved = entry.moistureAfter < entry.moistureBefore
        )
        MetricRow(
            icon = Icons.Outlined.Speed,
            title = "Пробивное напряжение",
            before = "${entry.breakdownBefore} кВ",
            after = "${entry.breakdownAfter} кВ",
            improved = entry.breakdownAfter > entry.breakdownBefore
        )
        entry.acidNumberAfter?.let { after ->
            MetricRow(
                icon = Icons.Outlined.Science,
                title = "Кислотное число",
                before = "${entry.acidNumberBefore ?: "—"} мг КОН/г",
                after = "$after мг КОН/г",
                improved = (entry.acidNumberBefore ?: Double.MAX_VALUE) > after
            )
        }
        entry.tanDeltaAfter?.let { after ->
            MetricRow(
                icon = Icons.Outlined.Thermostat,
                title = "tg δ при 90 °C",
                before = "${entry.tanDeltaBefore ?: "—"} %",
                after = "$after %",
                improved = (entry.tanDeltaBefore ?: Double.MAX_VALUE) > after
            )
        }

        entry.conclusion?.takeIf { it.isNotBlank() }?.let { conclusion ->
            HairLine()
            Text(
                text = conclusion,
                style = MaterialTheme.typography.bodySmall,
                color = Slate
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onShare,
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandIndigo,
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PictureAsPdf,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                Text(
                    text = "PDF",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
            OutlinedButton(
                onClick = onDelete,
                shape = RoundedCornerShape(13.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = null,
                    tint = Danger,
                    modifier = Modifier.size(17.dp)
                )
                Text(
                    text = "Удалить",
                    color = Danger,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun MetricRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    before: String,
    after: String,
    improved: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Slate,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Slate,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        Text(
            text = before,
            style = MaterialTheme.typography.bodySmall,
            color = Slate
        )
        Text(
            text = " → ",
            style = MaterialTheme.typography.bodySmall,
            color = Slate
        )
        Text(
            text = after,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (improved) Success else MaterialTheme.colorScheme.onSurface
        )
    }
}
