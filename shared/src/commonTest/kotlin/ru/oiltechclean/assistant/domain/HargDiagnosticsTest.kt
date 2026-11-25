package ru.oiltechclean.assistant.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class HargDiagnosticsTest {

    @Test
    fun highEnergyArcIsCritical() {
        // Ацетилен выше граничной концентрации Таблицы 2 РД (10 ppm) — дуга.
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 150.0, ch4 = 40.0, c2h2 = 12.0, c2h4 = 20.0, c2h6 = 10.0
        )
        assertEquals("Электрическая дуга высокой энергии", result.faultTitle)
        assertEquals("КРИТИЧЕСКИЙ УРОВЕНЬ", result.severity)
        assertTrue(result.boundaryExceedances.any { it.startsWith("C₂H₂") })
    }

    @Test
    fun acetyleneBelowBoundaryAtHighVoltageIsAttention() {
        // C2H2 = 4 ppm < границы, но при >= 110 кВ не может быть «нормы».
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 20.0, ch4 = 40.0, c2h2 = 4.0, c2h4 = 20.0, c2h6 = 10.0, voltageClassKv = 110
        )
        assertEquals("ПОВЫШЕННОЕ ВНИМАНИЕ", result.severity)
        assertTrue(result.faultTitle.contains("разряды"))
        assertTrue(result.explanation.contains("4.0"))
    }

    @Test
    fun acetyleneDetectedAtLowVoltageIsNotNormal() {
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 20.0, ch4 = 40.0, c2h2 = 4.0, c2h4 = 20.0, c2h6 = 10.0, voltageClassKv = 35
        )
        assertNotEquals("НОРМА / СТАБИЛЬНО", result.severity)
        assertTrue(result.faultTitle.contains("Ацетилен"))
    }

    @Test
    fun acetyleneAtBoundaryIsNotArc() {
        // Ровно на границе (10 ppm) — не дуга, но повышенное внимание при высоком напряжении.
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 20.0, ch4 = 40.0, c2h2 = 10.0, c2h4 = 20.0, c2h6 = 10.0, voltageClassKv = 110
        )
        assertNotEquals("Электрическая дуга высокой энергии", result.faultTitle)
        assertEquals("ПОВЫШЕННОЕ ВНИМАНИЕ", result.severity)
    }

    @Test
    fun hydrogenDominanceMeansPartialDischarges() {
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 150.0, ch4 = 5.0, c2h2 = 0.0, c2h4 = 10.0, c2h6 = 10.0
        )
        assertEquals("Частичные разряды (ЧР)", result.faultTitle)
    }

    @Test
    fun highEthyleneToEthaneRatioMeansOverheating() {
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 20.0, ch4 = 40.0, c2h2 = 0.0, c2h4 = 60.0, c2h6 = 10.0
        )
        assertEquals("Высокотемпературный перегрев (> 700 °C)", result.faultTitle)
    }

    @Test
    fun moderateRatioMeansThermalDefect() {
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 20.0, ch4 = 100.0, c2h2 = 0.0, c2h4 = 20.0, c2h6 = 10.0
        )
        assertEquals("Термический дефект средней температуры (300–700 °C)", result.faultTitle)
    }

    @Test
    fun paperInsulationSuspectedByCo2CoRatio() {
        // CO2/CO = 20 >= 13 — подозрение на деструкцию целлюлозы (п. 5.3 РД).
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 20.0, ch4 = 40.0, c2h2 = 0.0, c2h4 = 20.0, c2h6 = 10.0,
            co = 100.0, co2 = 2000.0
        )
        assertTrue(result.paperInsulationSuspected)
        assertTrue(result.explanation.contains("целлюлоз"))
        assertEquals("ПОВЫШЕННОЕ ВНИМАНИЕ", result.severity)
    }

    @Test
    fun paperInsulationNotSuspectedBelowRatioLimit() {
        // CO2/CO = 4 < 13 — твёрдая изоляция не поражена.
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 20.0, ch4 = 40.0, c2h2 = 0.0, c2h4 = 20.0, c2h6 = 10.0,
            co = 100.0, co2 = 400.0
        )
        assertFalse(result.paperInsulationSuspected)
    }

    @Test
    fun duvalZoneThermalMedium() {
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 10.0, ch4 = 40.0, c2h2 = 0.0, c2h4 = 30.0, c2h6 = 10.0
        )
        assertEquals("T2", result.duvalZone)
        assertTrue(result.explanation.contains("Треугольнику Дюваля 1"))
    }

    @Test
    fun duvalZoneArcing() {
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 5.0, ch4 = 5.0, c2h2 = 60.0, c2h4 = 20.0, c2h6 = 5.0
        )
        assertEquals("D2", result.duvalZone)
        assertTrue(result.explanation.contains("Дуговые разряды высокой энергии"))
    }

    @Test
    fun boundaryExceedancesAreReported() {
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 200.0, ch4 = 10.0, c2h2 = 0.0, c2h4 = 5.0, c2h6 = 5.0
        )
        assertTrue(result.boundaryExceedances.any { it.startsWith("H₂") })
    }

    @Test
    fun zeroInputFallsBackToNormal() {
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 0.0, ch4 = 0.0, c2h2 = 0.0, c2h4 = 0.0, c2h6 = 0.0
        )
        assertEquals("НОРМА / СТАБИЛЬНО", result.severity)
        assertEquals("—", result.duvalZone)
    }

    @Test
    fun growthRatesAreComputedPerDay() {
        val rates = HargDiagnostics.computeGrowthRates(
            current = mapOf("H2" to 130.0, "CH4" to 90.0),
            previous = mapOf("H2" to 100.0, "CH4" to 60.0),
            days = 10.0
        )
        assertEquals(2, rates.size)
        assertEquals(3.0, rates.first { it.gas == "H2" }.ppmPerDay)
        assertEquals(3.0, rates.first { it.gas == "CH4" }.ppmPerDay)
    }
}
