package ru.oiltechclean.assistant.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class HargDiagnosticsTest {

    @Test
    fun highEnergyArcIsCritical() {
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 150.0, ch4 = 40.0, c2h2 = 10.0, c2h4 = 20.0, c2h6 = 10.0
        )
        assertEquals("Электрическая дуга высокой энергии", result.faultTitle)
        assertEquals("КРИТИЧЕСКИЙ УРОВЕНЬ", result.severity)
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
    fun zeroInputFallsBackToNormal() {
        val result = HargDiagnostics.analyzeDissolvedGases(
            h2 = 0.0, ch4 = 0.0, c2h2 = 0.0, c2h4 = 0.0, c2h6 = 0.0
        )
        assertEquals("НОРМА / СТАБИЛЬНО", result.severity)
    }
}
