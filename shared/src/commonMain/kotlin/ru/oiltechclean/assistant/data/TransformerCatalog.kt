package ru.oiltechclean.assistant.data

import ru.oiltechclean.assistant.model.AcceptanceCheckGroup
import ru.oiltechclean.assistant.model.AcceptanceCheckItem
import ru.oiltechclean.assistant.model.TransformerType

/**
 * Справочник типовых силовых трансформаторов с массой масла.
 * Значения — типовые для серий ТМ/ТДН/ТДЦ/АОДЦТ, уточняются по паспорту конкретной единицы.
 */
object TransformerCatalog {

    val transformers = listOf(
        TransformerType("tm_100", "ТМ-100/10", 0.1, 10, 0.35),
        TransformerType("tm_250", "ТМ-250/10", 0.25, 10, 0.6),
        TransformerType("tm_400", "ТМ-400/10", 0.4, 10, 0.9),
        TransformerType("tm_630", "ТМ-630/10", 0.63, 10, 1.3),
        TransformerType("tm_1000", "ТМ-1000/10", 1.0, 10, 2.1),
        TransformerType("tm_1600", "ТМ-1600/10", 1.6, 10, 3.0),
        TransformerType("tm_2500", "ТМ-2500/35", 2.5, 35, 4.5),
        TransformerType("tmn_4000", "ТМН-4000/35", 4.0, 35, 6.5),
        TransformerType("tmn_6300", "ТМН-6300/35", 6.3, 35, 9.0),
        TransformerType("tdn_10000", "ТДН-10000/110", 10.0, 110, 14.0),
        TransformerType("tdn_16000", "ТДН-16000/110", 16.0, 110, 20.0),
        TransformerType("tdn_25000", "ТДН-25000/110", 25.0, 110, 28.0),
        TransformerType("tdn_40000", "ТДН-40000/110", 40.0, 110, 42.0),
        TransformerType("tdn_63000", "ТДН-63000/110", 63.0, 110, 60.0),
        TransformerType("tdc_125000", "ТДЦ-125000/220", 125.0, 220, 105.0),
        TransformerType("tdc_200000", "ТДЦ-200000/220", 200.0, 220, 155.0),
        TransformerType("tdc_250000", "ТДЦ-250000/330", 250.0, 330, 190.0),
        TransformerType("tdc_400000", "ТДЦ-400000/330", 400.0, 330, 270.0),
        TransformerType("aodct_333000", "АОДЦТН-333000/500", 333.0, 500, 240.0),
        TransformerType("aodct_667000", "АОДЦТН-667000/750", 667.0, 750, 420.0)
    )

    fun byId(id: String): TransformerType? = transformers.firstOrNull { it.id == id }

    /** Ближайший типовой трансформатор по массе масла — для подсказки объёма. */
    fun nearestByOilMass(oilMassTons: Double): TransformerType? =
        transformers.minByOrNull { kotlin.math.abs(it.oilMassTons - oilMassTons) }
}

/**
 * Чек-лист приёмки трансформаторного масла перед заливкой.
 * Составлен по РД 34.45-51.300-97 и типовой практике входного контроля.
 */
object OilAcceptanceChecklist {

    val groups = listOf(
        AcceptanceCheckGroup(
            title = "Документация партии",
            items = listOf(
                AcceptanceCheckItem(
                    id = "doc_passport",
                    title = "Паспорт качества на партию масла",
                    detail = "Проверить номер партии, дату изготовления, завод-изготовитель и марку масла (ГК, ВГ, Т-1500У).",
                    isCritical = true
                ),
                AcceptanceCheckItem(
                    id = "doc_cert",
                    title = "Сертификат соответствия ГОСТ",
                    detail = "Масло должно соответствовать ГОСТ 982-80 (или ГОСТ 10121-76 для ГК). Сверить марку с проектом.",
                    isCritical = true
                ),
                AcceptanceCheckItem(
                    id = "doc_transport",
                    title = "Транспортные документы и пломбы",
                    detail = "Проверить целостность пломб на цистерне или бочках, отсутствие следов подтёков.",
                    isCritical = false
                )
            )
        ),

        AcceptanceCheckGroup(
            title = "Отбор пробы",
            items = listOf(
                AcceptanceCheckItem(
                    id = "sample_bottom",
                    title = "Проба из нижней точки ёмкости",
                    detail = "Отбирать после отстоя не менее 2 часов. Для цистерны — с трёх уровней (верх, середина, низ).",
                    isCritical = true
                ),
                AcceptanceCheckItem(
                    id = "sample_clean",
                    title = "Чистота пробоотборника и тары",
                    detail = "Посуда должна быть промыта нефрасом и высушена. Влага и пыль в пробе искажают результат.",
                    isCritical = true
                ),
                AcceptanceCheckItem(
                    id = "sample_label",
                    title = "Маркировка пробы",
                    detail = "Указать объект, номер партии, дату и время отбора, ФИО отобравшего.",
                    isCritical = false
                )
            )
        ),
        AcceptanceCheckGroup(
            title = "Лабораторный контроль",
            items = listOf(
                AcceptanceCheckItem(
                    id = "lab_breakdown",
                    title = "Пробивное напряжение",
                    detail = "Не менее 35 кВ для масла до заливки в оборудование 35 кВ и ниже, не менее 55 кВ — для 110 кВ и выше.",
                    isCritical = true
                ),
                AcceptanceCheckItem(
                    id = "lab_moisture",
                    title = "Влагосодержание",
                    detail = "Не более 10 ppm для оборудования 110 кВ и выше, не более 25 ppm — для 35 кВ и ниже.",
                    isCritical = true
                ),
                AcceptanceCheckItem(
                    id = "lab_acid",
                    title = "Кислотное число",
                    detail = "Не более 0.02 мг КОН/г для свежего масла. Рост показателя указывает на окисление.",
                    isCritical = true
                ),
                AcceptanceCheckItem(
                    id = "lab_tandelta",
                    title = "tg δ при 90 °C",
                    detail = "Не более 2.5 % для свежего масла. Повышенное значение — признак загрязнения или увлажнения.",
                    isCritical = false
                ),
                AcceptanceCheckItem(
                    id = "lab_flash",
                    title = "Температура вспышки",
                    detail = "Не ниже 135 °C для масла ГК. Снижение указывает на наличие лёгких фракций.",
                    isCritical = false
                ),
                AcceptanceCheckItem(
                    id = "lab_visual",
                    title = "Внешний вид и цвет",
                    detail = "Масло должно быть прозрачным, без механических примесей, осадка и воды. Цвет — не темнее эталона.",
                    isCritical = false
                )
            )
        ),
        AcceptanceCheckGroup(
            title = "Перед заливкой",
            items = listOf(
                AcceptanceCheckItem(
                    id = "pre_temp",
                    title = "Температура масла выше температуры оборудования",
                    detail = "Заливать масло с температурой не ниже температуры бака, чтобы избежать конденсации влаги на изоляции.",
                    isCritical = true
                ),
                AcceptanceCheckItem(
                    id = "pre_vacuum",
                    title = "Оборудование под вакуумом",
                    detail = "Для трансформаторов 110 кВ и выше заливка выполняется под вакуумом не выше 133 Па.",
                    isCritical = true
                ),
                AcceptanceCheckItem(
                    id = "pre_hoses",
                    title = "Чистота рукавов и арматуры",
                    detail = "Промыть магистрали горячим маслом, проверить отсутствие влаги и стружки в рукавах.",
                    isCritical = false
                )
            )
        )
    )

    val totalItems: Int get() = groups.sumOf { it.items.size }
    val criticalItems: Int get() = groups.sumOf { group -> group.items.count { it.isCritical } }

    val criticalIds: Set<String>
        get() = groups.flatMap { it.items }.filter { it.isCritical }.map { it.id }.toSet()

    /** Допущено к заливке только при отметке всех критичных пунктов. */
    fun isApproved(checkedIds: Set<String>): Boolean = criticalIds.all { it in checkedIds }
}
