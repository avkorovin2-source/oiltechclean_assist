package ru.oiltechclean.assistant.domain

import ru.oiltechclean.assistant.model.GostStandardRow

/**
 * Оценка соответствия параметров масла нормам РД 34.45-51.300-97.
 *
 * Для каждого показателя проверяется предельное значение по классу напряжения.
 * Показатели, у которых нет данных (null), в оценку не включаются.
 */
object GostComplianceEvaluator {

    enum class Verdict { PASS, FAIL, UNKNOWN }

    data class CheckResult(
        val parameter: String,
        val actual: Double?,
        val limit: Double,
        val unit: String,
        val isUpperBound: Boolean,
        val verdict: Verdict
    ) {
        val isFail: Boolean get() = verdict == Verdict.FAIL
    }

    data class ComplianceReport(
        val voltageClass: String,
        val category: String,
        val checks: List<CheckResult>
    ) {
        val failed: List<CheckResult> get() = checks.filter { it.verdict == Verdict.FAIL }
        val passed: List<CheckResult> get() = checks.filter { it.verdict == Verdict.PASS }
        val evaluated: List<CheckResult> get() = checks.filter { it.verdict != Verdict.UNKNOWN }

        val isCompliant: Boolean get() = failed.isEmpty() && evaluated.isNotEmpty()
        val hasData: Boolean get() = evaluated.isNotEmpty()

        /** Доля выполненных показателей, 0..1. */
        val passRatio: Float
            get() = if (evaluated.isEmpty()) 0f else passed.size.toFloat() / evaluated.size

        /** Готовая строка «пройдено N из M» для iOS-интерфейса. */
        val summaryText: String get() = "Пройдено ${passed.size} из ${evaluated.size}"

        /** Несоответствия в виде готовых строк — без обращения к вложенным типам из Swift. */
        val failedLines: List<String>
            get() = failed.map { check ->
                val actual = check.actual?.let { formatNumber(it) } ?: "—"
                "${check.parameter}: $actual ${check.unit} (норма ${formatNumber(check.limit)} ${check.unit})"
            }
    }

    fun evaluate(
        standard: GostStandardRow,
        breakdownVoltageKv: Double?,
        moisturePpm: Double?,
        gasContentPercent: Double?,
        acidNumber: Double?,
        tanDelta90C: Double?
    ): ComplianceReport {
        val checks = listOf(
            check(
                parameter = "Пробивное напряжение",
                actual = breakdownVoltageKv,
                limit = standard.breakdownVoltageKvMin,
                unit = "кВ",
                isUpperBound = false
            ),
            check(
                parameter = "Влагосодержание",
                actual = moisturePpm,
                limit = standard.moisturePpmMax,
                unit = "ppm",
                isUpperBound = true
            ),
            check(
                parameter = "Газосодержание",
                actual = gasContentPercent,
                limit = standard.gasContentPercentMax,
                unit = "%",
                isUpperBound = true
            ),
            check(
                parameter = "Кислотное число",
                actual = acidNumber,
                limit = standard.acidNumberMax,
                unit = "мг КОН/г",
                isUpperBound = true
            ),
            check(
                parameter = "tg δ при 90 °C",
                actual = tanDelta90C,
                limit = standard.tanDelta90CMax,
                unit = "%",
                isUpperBound = true
            )
        )
        return ComplianceReport(
            voltageClass = standard.voltageClass,
            category = standard.category,
            checks = checks
        )
    }

    /**
     * Swift-дружественный вариант [evaluate]: все параметры не-null,
     * отсутствие данных передаётся как [Double.NaN].
     * Нужен потому, что Kotlin `Double?` экспортируется в Swift как `KotlinDouble?`.
     */
    fun evaluateValues(
        standard: GostStandardRow,
        breakdownVoltageKv: Double,
        moisturePpm: Double,
        gasContentPercent: Double,
        acidNumber: Double,
        tanDelta90C: Double
    ): ComplianceReport = evaluate(
        standard = standard,
        breakdownVoltageKv = breakdownVoltageKv.takeUnless { it.isNaN() },
        moisturePpm = moisturePpm.takeUnless { it.isNaN() },
        gasContentPercent = gasContentPercent.takeUnless { it.isNaN() },
        acidNumber = acidNumber.takeUnless { it.isNaN() },
        tanDelta90C = tanDelta90C.takeUnless { it.isNaN() }
    )

    private fun formatNumber(value: Double): String {
        val rounded = kotlin.math.round(value * 100.0) / 100.0
        val asInt = rounded.toInt()
        return if (rounded == asInt.toDouble()) asInt.toString() else rounded.toString()
    }

    private fun check(
        parameter: String,
        actual: Double?,
        limit: Double,
        unit: String,
        isUpperBound: Boolean
    ): CheckResult {
        val verdict = when {
            actual == null -> Verdict.UNKNOWN
            isUpperBound -> if (actual <= limit) Verdict.PASS else Verdict.FAIL
            else -> if (actual >= limit) Verdict.PASS else Verdict.FAIL
        }
        return CheckResult(
            parameter = parameter,
            actual = actual,
            limit = limit,
            unit = unit,
            isUpperBound = isUpperBound,
            verdict = verdict
        )
    }

    /** Текст заключения для протокола. */
    fun buildConclusion(report: ComplianceReport): String {
        if (!report.hasData) {
            return "Недостаточно данных для оценки соответствия нормам."
        }
        if (report.isCompliant) {
            return "Показатели качества масла соответствуют нормам РД 34.45-51.300-97 " +
                "для оборудования класса напряжения ${report.voltageClass}."
        }
        val failedNames = report.failed.joinToString(", ") { it.parameter.lowercase() }
        return "Выявлено несоответствие нормам РД 34.45-51.300-97 " +
            "для оборудования класса напряжения ${report.voltageClass}: $failedNames. " +
            "Требуется повторная обработка масла."
    }
}
