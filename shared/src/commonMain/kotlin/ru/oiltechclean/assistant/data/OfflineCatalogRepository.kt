package ru.oiltechclean.assistant.data

import ru.oiltechclean.assistant.model.EquipmentUnit
import ru.oiltechclean.assistant.model.GostStandardRow
import ru.oiltechclean.assistant.model.TroubleshootingRow

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
                "Падение производительности на выходе" to "Проверить перепад давления на фильтрах тонкой очистки. При перепаде > 0.25 МПа заменить картриджи.",
                "Повышенный шум вакуумного агрегата" to "Проверить наличие подсоса воздуха на всасывающей магистрали и уровень смазки в редукторе."
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

    /**
     * Swift-дружественное представление [EquipmentUnit.troubleshooting]:
     * список плоских пар вместо `List<Pair<String, String>>`.
     */
    /**
     * Swift-дружественное представление [EquipmentUnit.troubleshooting]:
     * список плоских пар вместо `List<Pair<String, String>>`.
     */
    fun troubleshootingRows(unit: EquipmentUnit): List<TroubleshootingRow> =
        unit.troubleshooting.map { TroubleshootingRow(problem = it.first, solution = it.second) }

    /**
     * Описание установки для iOS. Имя `description` конфликтует с `NSObject.description`,
     * поэтому из Swift оно читается через этот метод.
     */
    fun unitSummary(unit: EquipmentUnit): String = unit.description
}
