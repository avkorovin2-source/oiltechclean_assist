package ru.oiltechclean.assistant.domain

import ru.oiltechclean.assistant.data.OfflineCatalogRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GostComplianceTest {

    private val standard = OfflineCatalogRepository.gostStandards.first { it.voltageClass == "220 кВ" }

    @Test
    fun allParametersWithinLimitsGiveCompliantReport() {
        val report = GostComplianceEvaluator.evaluate(
            standard = standard,
            breakdownVoltageKv = 65.0,
            moisturePpm = 8.0,
            gasContentPercent = 0.3,
            acidNumber = 0.03,
            tanDelta90C = 5.0
        )
        assertTrue(report.isCompliant)
        assertEquals(5, report.passed.size)
        assertTrue(report.failed.isEmpty())
    }

    @Test
    fun moistureAboveLimitFails() {
        val report = GostComplianceEvaluator.evaluate(
            standard = standard,
            breakdownVoltageKv = null,
            moisturePpm = 12.0,
            gasContentPercent = null,
            acidNumber = null,
            tanDelta90C = null
        )
        assertFalse(report.isCompliant)
        assertEquals(1, report.failed.size)
        assertEquals("Влагосодержание", report.failed.first().parameter)
        assertTrue(GostComplianceEvaluator.buildConclusion(report).contains("влагосодержание"))
    }

    @Test
    fun emptyReportHasNoData() {
        val report = GostComplianceEvaluator.evaluate(
            standard = standard,
            breakdownVoltageKv = null,
            moisturePpm = null,
            gasContentPercent = null,
            acidNumber = null,
            tanDelta90C = null
        )
        assertFalse(report.hasData)
        assertFalse(report.isCompliant)
        assertEquals("Недостаточно данных для оценки соответствия нормам.", GostComplianceEvaluator.buildConclusion(report))
    }
}
