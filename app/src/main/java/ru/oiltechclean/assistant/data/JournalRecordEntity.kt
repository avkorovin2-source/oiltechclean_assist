package ru.oiltechclean.assistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_records")
data class JournalRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val substationName: String,
    val transformerName: String,
    val unitModel: String,
    val recordDate: String,
    val moistureBefore: Double,
    val moistureAfter: Double,
    val breakdownBefore: Double,
    val breakdownAfter: Double,
    val acidNumberBefore: Double? = null,
    val acidNumberAfter: Double? = null,
    val tanDeltaBefore: Double? = null,
    val tanDeltaAfter: Double? = null,
    val remarks: String? = null,
    /** Класс напряжения оборудования, например «220 кВ». */
    val voltageClass: String? = null,
    /** Температура масла при отборе пробы, °C. */
    val oilTemperatureC: Double? = null,
    /** Газосодержание после обработки, %. */
    val gasContentAfter: Double? = null,
    /** Температура вспышки после обработки, °C. */
    val flashPointAfter: Double? = null,
    /** Заключение по результатам замера. */
    val conclusion: String? = null
)
