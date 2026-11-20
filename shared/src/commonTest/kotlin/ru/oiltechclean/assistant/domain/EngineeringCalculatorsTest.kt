package ru.oiltechclean.assistant.domain

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Расчёты перенесены в общий KMP-модуль, поэтому тесты гоняются на JVM и на iOS. */
class EngineeringCalculatorsTest {

    @Test
    fun uvsmCyclesFollowExponentialPassEfficiency() {
        val result = EngineeringCalculators.calculateUvsmProcessing(
            oilVolumeM3 = 25.0,
            initialMoisturePpm = 35.0,
            targetMoisturePpm = 8.0,
            unitCapacityM3H = 4.0
        )
        // ln(35/8) / ln(1/0.35) = 1.406 -> 2 цикла
        assertEquals(2, result.recommendedCycles)
        assertEquals(50.0, result.totalVolumeProcessedM3, 0.001)
        assertEquals(12.5, result.totalHours, 0.001)
    }

    @Test
    fun uvsmReturnsSingleCycleWhenTargetNotLowerThanInitial() {
        val result = EngineeringCalculators.calculateUvsmProcessing(
            oilVolumeM3 = 10.0,
            initialMoisturePpm = 5.0,
            targetMoisturePpm = 10.0,
            unitCapacityM3H = 2.0
        )
        assertEquals(1, result.recommendedCycles)
        assertEquals(5.0, result.totalHours, 0.001)
    }

    @Test
    fun silicaGelMassScalesWithAcidDelta() {
        val kg = EngineeringCalculators.calculateSorbentMassKg(
            oilMassTons = 20.0,
            initialAcidNumber = 0.12,
            targetAcidNumber = 0.03,
            isZeoliteDryingOnly = false
        )
        // delta 0.09 -> 2.7 % -> 540 кг
        assertEquals(540.0, kg, 0.001)
    }

    @Test
    fun zeoliteMassIgnoresAcidNumbers() {
        val kg = EngineeringCalculators.calculateSorbentMassKg(
            oilMassTons = 20.0,
            initialAcidNumber = 0.5,
            targetAcidNumber = 0.01,
            isZeoliteDryingOnly = true
        )
        assertEquals(280.0, kg, 0.001)
    }

    @Test
    fun vacuumTimeGrowsWithVolumeAndDepth() {
        val minutes = EngineeringCalculators.calculateVacuumTimeMinutes(
            tankFreeVolumeM3 = 35.0,
            pumpSpeedM3H = 300.0,
            targetPressurePa = 100.0
        )
        assertTrue(abs(minutes - 116.28) < 0.5, "Ожидалось ≈116.28 мин, получено $minutes")
    }

    @Test
    fun tanDeltaConversionIsIdentityAtSameTemperature() {
        assertEquals(1.2, EngineeringCalculators.convertTanDelta(1.2, 90.0, 90.0), 1e-9)
        // exp(0.034 * 45) = 4.6182
        assertEquals(4.6182, EngineeringCalculators.convertTanDelta(1.0, 45.0, 90.0), 0.001)
    }

    @Test
    fun dewPointAndMoistureAreInverse() {
        val dewPoint = EngineeringCalculators.calculateDewPointC(150.0)
        assertTrue(abs(dewPoint - (-35.0)) < 10.0, "Точка росы вне ожидаемого диапазона: $dewPoint")
        val ppm = EngineeringCalculators.moistureFromDewPointPpm(dewPoint)
        assertEquals(150.0, ppm, 1.0)
    }

    @Test
    fun flashPointDropsWithLightFractions() {
        assertEquals(145.0, EngineeringCalculators.estimateFlashPointC(150.0, 3.0), 0.001)
        assertTrue(EngineeringCalculators.estimateFlashPointC(150.0, 0.0) == 150.0)
        assertEquals(130.0, EngineeringCalculators.flashPointLimitC(35), 0.001)
        assertEquals(135.0, EngineeringCalculators.flashPointLimitC(220), 0.001)
    }

    @Test
    fun pressureConversionFromMmHg() {
        val pa = EngineeringCalculators.convertPressureFromMmHg(760.0).getValue("Па (Pa)")
        assertEquals(101324.72, pa, 0.05)
    }
}
