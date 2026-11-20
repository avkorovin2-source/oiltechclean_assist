package ru.oiltechclean.assistant.domain

object HargDiagnostics {

    data class HargResult(
        val faultTitle: String,
        val severity: String,
        val explanation: String,
        val recommendedAction: String
    )

    fun analyzeDissolvedGases(
        h2: Double,
        ch4: Double,
        c2h2: Double,
        c2h4: Double,
        c2h6: Double
    ): HargResult {
        val safeH2 = if (h2 <= 0) 0.1 else h2
        val safeCh4 = if (ch4 <= 0) 0.1 else ch4
        val safeC2h4 = if (c2h4 <= 0) 0.1 else c2h4
        val safeC2h6 = if (c2h6 <= 0) 0.1 else c2h6

        val ratioC2h2ToC2h4 = c2h2 / safeC2h4
        val ratioCh4ToH2 = ch4 / safeH2
        val ratioC2h4ToC2h6 = c2h4 / safeC2h6

        return when {
            c2h2 > 5.0 && ratioC2h2ToC2h4 >= 0.1 -> HargResult(
                faultTitle = "Электрическая дуга высокой энергии",
                severity = "КРИТИЧЕСКИЙ УРОВЕНЬ",
                explanation = "Обнаружена повышенная концентрация ацетилена (C2H2 = $c2h2 ppm). Характерно для дуговых разрядов и пробоев изоляции.",
                recommendedAction = "Требуется немедленный вывод трансформатора из работы, ревизия активной части и полная дегазация масла на УВСМ."
            )
            ratioCh4ToH2 < 0.1 && h2 > 100.0 -> HargResult(
                faultTitle = "Частичные разряды (ЧР)",
                severity = "ПОВЫШЕННОЕ ВНИМАНИЕ",
                explanation = "Преобладание водорода (H2 = $h2 ppm) над метаном указывает на ионизацию и частичные разряды в газовых включениях изоляции.",
                recommendedAction = "Провести глубокую вакуумную дегазацию масла на установке УВСМ для удаления растворенных газов и влаги."
            )
            ratioC2h4ToC2h6 > 3.0 && c2h4 > 50.0 -> HargResult(
                faultTitle = "Высокотемпературный перегрев (> 700 °C)",
                severity = "ВЫСОКИЙ РИСК",
                explanation = "Высокое соотношение этилена к этану свидетельствует о сильном локальном перегреве токоведущих соединений или магнитопровода.",
                recommendedAction = "Проверить контактные соединения, систему охлаждения и выполнить регенерацию масла блоком БРМ."
            )
            ratioC2h4ToC2h6 in 1.0..3.0 && ch4 > 80.0 -> HargResult(
                faultTitle = "Термический дефект средней температуры (300–700 °C)",
                severity = "УМЕРЕННЫЙ РИСК",
                explanation = "Повышенное выделение метана и этилена говорит о перегреве маслобарьерной изоляции.",
                recommendedAction = "Учащенный контроль ХАРГ и плановая очистка/дегазация масла."
            )
            else -> HargResult(
                faultTitle = "Явных признаков развивающегося дефекта не выявлено",
                severity = "НОРМА / СТАБИЛЬНО",
                explanation = "Соотношения ключевых газов находятся в допустимых пределах для нормального старения изоляции.",
                recommendedAction = "Продолжать плановый мониторинг согласно регламенту РД."
            )
        }
    }
}
