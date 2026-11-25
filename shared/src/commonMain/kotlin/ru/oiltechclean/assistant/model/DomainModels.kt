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
/** Свежее масло при приёмке перед заливкой — пределы ГОСТ 982-80 / типовой практики входного контроля. */
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

/**
 * Типовой силовой трансформатор из справочника.
 * [oilMassTons] — масса масла, [oilVolumeM3] — объём масла (при плотности ≈ 0.89 т/м³).
 */
data class TransformerType(
    val id: String,
    val label: String,
    val powerMva: Double,
    val voltageClassKv: Int,
    val oilMassTons: Double
) {
    val oilVolumeM3: Double get() = oilMassTons / 0.89
}

/** Пункт чек-листа приёмки масла перед заливкой в оборудование. */
data class AcceptanceCheckItem(
    val id: String,
    val title: String,
    val detail: String,
    val isCritical: Boolean
)

/** Группа пунктов чек-листа приёмки. */
data class AcceptanceCheckGroup(
    val title: String,
    val items: List<AcceptanceCheckItem>
)

/**
 * Пара «неисправность — решение» в Swift-дружественном виде.
 *
 * Kotlin `Pair` экспортируется в Swift как `KotlinPair` без именованных полей,
 * поэтому для iOS-клиента используется отдельный data class.
 */
data class TroubleshootingRow(
    val problem: String,
    val solution: String
)
