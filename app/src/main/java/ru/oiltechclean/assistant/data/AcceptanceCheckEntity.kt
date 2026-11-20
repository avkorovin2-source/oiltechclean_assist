package ru.oiltechclean.assistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Результат проверки чек-листа приёмки масла.
 * [checkedItemIds] — идентификаторы отмеченных пунктов через запятую.
 * [approved] — вердикт на момент сохранения: все критичные пункты отмечены.
 */
@Entity(tableName = "acceptance_checks")
data class AcceptanceCheckEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val objectName: String,
    /** Момент сохранения проверки, миллисекунды Unix-времени. */
    val checkedAt: Long,
    val checkedItemIds: String,
    val approved: Boolean
)

/** Отмеченные пункты в виде множества идентификаторов. */
fun AcceptanceCheckEntity.checkedIdSet(): Set<String> =
    checkedItemIds.split(",").filter { it.isNotBlank() }.toSet()
