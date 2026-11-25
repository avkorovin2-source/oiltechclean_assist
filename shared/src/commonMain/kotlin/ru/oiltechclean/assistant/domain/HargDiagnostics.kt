package ru.oiltechclean.assistant.domain

object HargDiagnostics {

    /**
     * Граничные концентрации растворенных газов в трансформаторном масле, ppm.
     * Значения Таблицы 2 РД 153-34.0-46.302-00 (в тексте РД приводятся как
     * знаменатели относительных концентраций: H2 0.01 %об, CH4 0.01 %об,
     * C2H2 0.001 %об, C2H4 0.01 %об, C2H6 0.005 %об).
     */
    private const val BOUNDARY_H2_PPM = 100.0
    private const val BOUNDARY_CH4_PPM = 100.0
    private const val BOUNDARY_C2H2_PPM = 10.0
    private const val BOUNDARY_C2H4_PPM = 100.0
    private const val BOUNDARY_C2H6_PPM = 50.0

    /**
     * Порог отношения CO2/CO по п. 5.3 РД 153-34.0-46.302-00:
     * отношение >= 13 — подозрение на термическую деструкцию целлюлозы
     * (поражение твёрдой бумажной изоляции); отношение < 13 — твёрдая изоляция не поражена.
     */
    private const val CO2_CO_PAPER_LIMIT = 13.0

    /** Оборудование класса >= 110 кВ: любое присутствие ацетилена считается значимым. */
    private const val HIGH_VOLTAGE_KV = 110

    /** Ранги severity для выбора самого опасного вердикта при совпадении признаков. */
    private val SEVERITY_RANK = mapOf(
        "НОРМА / СТАБИЛЬНО" to 0,
        "ПОВЫШЕННОЕ ВНИМАНИЕ" to 1,
        "УМЕРЕННЫЙ РИСК" to 2,
        "ВЫСОКИЙ РИСК" to 3,
        "КРИТИЧЕСКИЙ УРОВЕНЬ" to 4
    )

    data class HargResult(
        val faultTitle: String,
        val severity: String,
        val explanation: String,
        val recommendedAction: String,
        /** Зона Треугольника Дюваля 1 (код) или "—", если расчёт невозможен. */
        val duvalZone: String,
        /** Расшифровка зоны Треугольника Дюваля 1. */
        val duvalInterpretation: String,
        /** Газы, превысившие граничные концентрации Таблицы 2 РД 153-34.0-46.302-00. */
        val boundaryExceedances: List<String>,
        /** Подозрение на поражение твёрдой (бумажной) изоляции: CO2/CO >= 13 (п. 5.3 РД). */
        val paperInsulationSuspected: Boolean
    )

    /**
     * Экспресс-диагностика по растворённым газам (ХАРГ).
     *
     * @param h2, ch4, c2h2, c2h4, c2h6 концентрации газов, ppm.
     * @param co, co2 концентрации оксидов углерода, ppm — нужны для оценки бумажной изоляции (CO2/CO).
     * @param voltageClassKv класс напряжения оборудования, кВ (0 — не указан).
     *
     * По сравнению с упрощённой версией:
     * - учтены оксиды углерода (дефект бумажной изоляции через CO2/CO, п. 5.3 РД);
     * - применены граничные концентрации Таблицы 2 РД 153-34.0-46.302-00;
     * - ацетилен никогда не даёт вердикт «НОРМА»: при >= 110 кВ любое присутствие C2H2 —
     *   повышенное внимание, выше граничной концентрации (10 ppm) — дуга/критический уровень;
     * - добавлена зона Треугольника Дюваля 1 (IEEE C57.104-2019 §6.2.3);
     * - добавлен перечень превышений граничных концентраций.
     */
    fun analyzeDissolvedGases(
        h2: Double,
        ch4: Double,
        c2h2: Double,
        c2h4: Double,
        c2h6: Double,
        co: Double = 0.0,
        co2: Double = 0.0,
        voltageClassKv: Int = 0
    ): HargResult {
        val safeH2 = if (h2 <= 0) 0.1 else h2
        val safeCh4 = if (ch4 <= 0) 0.1 else ch4
        val safeC2h4 = if (c2h4 <= 0) 0.1 else c2h4
        val safeC2h6 = if (c2h6 <= 0) 0.1 else c2h6

        val ratioCh4ToH2 = ch4 / safeH2
        val ratioC2h4ToC2h6 = c2h4 / safeC2h6
        val ratioC2h2ToC2h4 = c2h2 / safeC2h4

        val exceedances = boundaryExceedances(h2, ch4, c2h2, c2h4, c2h6)

        val paperInsulationSuspected = co > 0.0 && co2 > 0.0 && co2 / co >= CO2_CO_PAPER_LIMIT

        // Треугольник Дюваля 1 — проценты от суммы CH4 + C2H4 + C2H2 (IEEE C57.104-2019 §6.2.3)
        val (duvalZoneCode, duvalInterpretation) = when {
            (ch4 + c2h4 + c2h2) > 0.0 -> {
                val sum = ch4 + c2h4 + c2h2
                duvalTriangle1(
                    ch4 * 100.0 / sum,
                    c2h4 * 100.0 / sum,
                    c2h2 * 100.0 / sum
                )
            }
            else -> "—" to "Невозможно рассчитать (CH4 + C2H4 + C2H2 = 0)"
        }

        val diagnosed = diagnose(
            h2, ch4, c2h2, c2h4, c2h6,
            ratioCh4ToH2, ratioC2h4ToC2h6, ratioC2h2ToC2h4,
            exceedances, voltageClassKv
        )
        val title = diagnosed.first
        val (severity, action, description) = diagnosed.second

        var finalSeverity = severity
        var finalTitle = title
        val explanationParts = mutableListOf<String>()
        explanationParts += description

        if (exceedances.isNotEmpty()) {
            explanationParts += "Превышены граничные концентрации Таблицы 2 РД 153-34.0-46.302-00: ${exceedances.joinToString(", ")}."
        }

        if (co > 0.0 && co2 > 0.0) {
            val ratio = co2 / co
            val paperVerdict = if (paperInsulationSuspected) {
                "подозрение на термическую деструкцию целлюлозы (поражение твёрдой изоляции)"
            } else {
                "твёрдая (бумажная) изоляция не поражена"
            }
            explanationParts += "Отношение CO₂/CO = ${formatNumber(ratio)} — $paperVerdict (порог по п. 5.3 РД: $CO2_CO_PAPER_LIMIT)."
            if (paperInsulationSuspected && SEVERITY_RANK[finalSeverity]!! < SEVERITY_RANK["ПОВЫШЕННОЕ ВНИМАНИЕ"]!!) {
                finalSeverity = "ПОВЫШЕННОЕ ВНИМАНИЕ"
                finalTitle = "Подозрение на поражение твёрдой изоляции (целлюлозы)"
            }
        } else {
            explanationParts += "CO/CO₂ не указаны — оценка твёрдой изоляции по отношению CO₂/CO невозможна."
        }

        explanationParts += "По Треугольнику Дюваля 1: $duvalInterpretation (зона $duvalZoneCode)."

        return HargResult(
            faultTitle = finalTitle,
            severity = finalSeverity,
            explanation = explanationParts.joinToString(" "),
            recommendedAction = action,
            duvalZone = duvalZoneCode,
            duvalInterpretation = duvalInterpretation,
            boundaryExceedances = exceedances,
            paperInsulationSuspected = paperInsulationSuspected
        )
    }

    /** Вызов без CO/CO2 и без привязки к классу напряжения — сохранён для обратной совместимости. */
    fun analyzeDissolvedGases(
        h2: Double,
        ch4: Double,
        c2h2: Double,
        c2h4: Double,
        c2h6: Double
    ): HargResult = analyzeDissolvedGases(
        h2 = h2, ch4 = ch4, c2h2 = c2h2, c2h4 = c2h4, c2h6 = c2h6,
        co = 0.0, co2 = 0.0, voltageClassKv = 0
    )

    private fun diagnose(
        h2: Double,
        ch4: Double,
        c2h2: Double,
        c2h4: Double,
        c2h6: Double,
        ratioCh4ToH2: Double,
        ratioC2h4ToC2h6: Double,
        ratioC2h2ToC2h4: Double,
        exceedances: List<String>,
        voltageClassKv: Int
    ): Pair<String, Triple<String, String, String>> { // title, (severity, action, description)
        val c2h2Note = if (c2h2 > 0.0) " (C₂H₂ = $c2h2 ppm)" else ""
        return when {
            // 1. Дуга высокой энергии — ацетилен выше граничной концентрации.
            c2h2 > BOUNDARY_C2H2_PPM -> "Электрическая дуга высокой энергии" to Triple(
                "КРИТИЧЕСКИЙ УРОВЕНЬ",
                "Требуется немедленный вывод трансформатора из работы, ревизия активной части и полная дегазация масла на УВСМ.",
                "Обнаружена повышенная концентрация ацетилена (C₂H₂ = $c2h2 ppm, выше границы ${BOUNDARY_C2H2_PPM} ppm). " +
                    "Характерно для дуговых разрядов и пробоев изоляции. Отношение C₂H₂/C₂H₄ = ${formatNumber(ratioC2h2ToC2h4)}."
            )
            // 2. Высоковольтное оборудование: любое присутствие ацетилена значимо.
            voltageClassKv >= HIGH_VOLTAGE_KV && c2h2 > 0.0 -> "Подозрение на электрические разряды" to Triple(
                "ПОВЫШЕННОЕ ВНИМАНИЕ",
                "Углубить контроль ХАРГ, проверить изоляцию активной части и заземление; при подтверждении роста ацетилена — вывод в ремонт.",
                "Для оборудования класса напряжения ${voltageClassKv} кВ любое присутствие ацетилена$c2h2Note считается значимым, " +
                    "даже ниже граничной концентрации ${BOUNDARY_C2H2_PPM} ppm."
            )
            // 3. Частичные разряды — преобладание водорода.
            ratioCh4ToH2 < 0.1 && h2 > 100.0 -> "Частичные разряды (ЧР)" to Triple(
                "ПОВЫШЕННОЕ ВНИМАНИЕ",
                "Провести глубокую вакуумную дегазацию масла на установке УВСМ для удаления растворенных газов и влаги.",
                "Преобладание водорода (H₂ = $h2 ppm) над метаном указывает на ионизацию и частичные разряды " +
                    "в газовых включениях изоляции.$c2h2Note"
            )
            // 4. Термический дефект высоких температур.
            ratioC2h4ToC2h6 > 3.0 && c2h4 > 50.0 -> "Высокотемпературный перегрев (> 700 °C)" to Triple(
                "ВЫСОКИЙ РИСК",
                "Проверить контактные соединения, систему охлаждения и выполнить регенерацию масла блоком БРМ.",
                "Высокое соотношение этилена к этану свидетельствует о сильном локальном перегреве " +
                    "токоведущих соединений или магнитопровода.$c2h2Note"
            )
            // 5. Термический дефект средних температур.
            ratioC2h4ToC2h6 in 1.0..3.0 && ch4 > 80.0 -> "Термический дефект средней температуры (300–700 °C)" to Triple(
                "УМЕРЕННЫЙ РИСК",
                "Учащенный контроль ХАРГ и плановая очистка/дегазация масла.",
                "Повышенное выделение метана и этилена говорит о перегреве маслобарьерной изоляции.$c2h2Note"
            )
            // 6. Ацетилен обнаружен, но ниже граничной концентрации — всегда значимо.
            c2h2 > 0.0 -> "Ацетилен обнаружен — ниже граничной концентрации" to Triple(
                "ПОВЫШЕННОЕ ВНИМАНИЕ",
                "Повторить анализ через краткий интервал для оценки динамики; при росте ацетилена — вывод в ремонт.",
                "Обнаружен ацетилен$c2h2Note. Присутствие ацетилена всегда значимо, даже ниже граничной " +
                    "концентрации ${BOUNDARY_C2H2_PPM} ppm."
            )
            // 7. Превышение иных граничных концентраций без конкретной картины дефекта.
            exceedances.isNotEmpty() -> "Превышение граничных концентраций газов" to Triple(
                "ПОВЫШЕННОЕ ВНИМАНИЕ",
                "Сравнить с предыдущими замерами; при подтверждении тренда — очистка/дегазация масла на УВСМ.",
                "Превышены граничные концентрации Таблицы 2 РД 153-34.0-46.302-00: ${exceedances.joinToString(", ")}."
            )
            else -> "Явных признаков развивающегося дефекта не выявлено" to Triple(
                "НОРМА / СТАБИЛЬНО",
                "Продолжать плановый мониторинг согласно регламенту РД.",
                "Соотношения ключевых газов находятся в допустимых пределах для нормального старения изоляции."
            )
        }
    }

    private fun boundaryExceedances(
        h2: Double, ch4: Double, c2h2: Double, c2h4: Double, c2h6: Double
    ): List<String> {
        val result = mutableListOf<String>()
        if (h2 > BOUNDARY_H2_PPM) result += "H₂ > ${BOUNDARY_H2_PPM} ppm"
        if (ch4 > BOUNDARY_CH4_PPM) result += "CH₄ > ${BOUNDARY_CH4_PPM} ppm"
        if (c2h2 > BOUNDARY_C2H2_PPM) result += "C₂H₂ > ${BOUNDARY_C2H2_PPM} ppm"
        if (c2h4 > BOUNDARY_C2H4_PPM) result += "C₂H₄ > ${BOUNDARY_C2H4_PPM} ppm"
        if (c2h6 > BOUNDARY_C2H6_PPM) result += "C₂H₆ > ${BOUNDARY_C2H6_PPM} ppm"
        return result
    }

    /**
     * Треугольник Дюваля 1 — границы зон в % от суммы CH4 + C2H4 + C2H2
     * (IEEE C57.104-2019, §6.2.3). Проверка по порядку, первое совпадение определяет зону.
     */
    private fun duvalTriangle1(pCh4: Double, pC2H4: Double, pC2H2: Double): Pair<String, String> =
        when {
            pCh4 >= 98.0 -> "PD" to "Частичные разряды (корона)"
            pCh4 < 98.0 && pC2H4 < 20.0 && pC2H2 < 4.0 -> "T1" to "Термический дефект ниже 300 °C"
            pC2H4 >= 20.0 && pC2H4 < 50.0 && pC2H2 < 4.0 -> "T2" to "Термический дефект 300–700 °C"
            pC2H4 >= 50.0 && pC2H2 < 15.0 -> "T3" to "Термический дефект выше 700 °C"
            pC2H4 < 50.0 && pC2H2 >= 4.0 && pC2H2 < 13.0 -> "DT" to "Смешанный термо-электрический дефект"
            pC2H4 >= 40.0 && pC2H4 < 50.0 && pC2H2 >= 13.0 && pC2H2 < 29.0 -> "DT" to "Смешанный термо-электрический дефект"
            pC2H4 >= 50.0 && pC2H2 >= 15.0 && pC2H2 < 29.0 -> "DT" to "Смешанный термо-электрический дефект"
            pC2H4 < 23.0 && pC2H2 >= 13.0 -> "D1" to "Разряды низкой энергии (искровые)"
            pC2H4 >= 23.0 && pC2H2 >= 29.0 -> "D2" to "Дуговые разряды высокой энергии"
            pC2H4 >= 23.0 && pC2H4 < 40.0 && pC2H2 >= 13.0 && pC2H2 < 29.0 -> "D2" to "Дуговые разряды высокой энергии"
            else -> "—" to "Зона не определена (пограничный случай)"
        }

    private fun formatNumber(value: Double): String {
        val rounded = kotlin.math.round(value * 100.0) / 100.0
        val asInt = rounded.toInt()
        return if (rounded == asInt.toDouble()) asInt.toString() else rounded.toString()
    }

    /**
     * Скорость нарастания концентраций газов между двумя замерами, ppm/сутки.
     * Пороги тревожного нарастания уточняются по п. 3.2 РД 153-34.0-46.302-00 под
     * конкретное оборудование; метод возвращает расчитанные значения без встроенных порогов.
     */
    data class GasGrowthRate(
        val gas: String,
        val currentPpm: Double,
        val previousPpm: Double,
        val days: Double,
        val ppmPerDay: Double
    )

    fun computeGrowthRates(
        current: Map<String, Double>,
        previous: Map<String, Double>,
        days: Double
    ): List<GasGrowthRate> {
        require(days > 0.0) { "Интервал между замерами должен быть больше нуля" }
        return current.entries
            .filter { it.value > 0 }
            .map { (gas, cur) ->
                val prev = previous[gas] ?: 0.0
                GasGrowthRate(gas, cur, prev, days, (cur - prev) / days)
            }
    }
}
