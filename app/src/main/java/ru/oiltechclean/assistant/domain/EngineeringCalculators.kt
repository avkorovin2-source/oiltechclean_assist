package ru.oiltechclean.assistant.domain

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max

object EngineeringCalculators {

    data class UvsmCalculationResult(
        val recommendedCycles: Int,
        val totalHours: Double,
        val totalVolumeProcessedM3: Double
    )

    fun calculateUvsmProcessing(
        oilVolumeM3: Double,
        initialMoisturePpm: Double,
        targetMoisturePpm: Double,
        unitCapacityM3H: Double,
        passEfficiency: Double = 0.65
    ): UvsmCalculationResult {
        if (oilVolumeM3 <= 0 || unitCapacityM3H <= 0 || initialMoisturePpm <= targetMoisturePpm) {
            return UvsmCalculationResult(1, oilVolumeM3 / max(unitCapacityM3H, 0.1), oilVolumeM3)
        }
        val safeTarget = max(targetMoisturePpm, 1.0)
        val rawCycles = ln(initialMoisturePpm / safeTarget) / ln(1.0 / (1.0 - passEfficiency))
        val cycles = max(1, kotlin.math.ceil(rawCycles).toInt())
        val totalVolume = oilVolumeM3 * cycles
        val hours = totalVolume / unitCapacityM3H
        return UvsmCalculationResult(
            recommendedCycles = cycles,
            totalHours = hours,
            totalVolumeProcessedM3 = totalVolume
        )
    }

    fun calculateSorbentMassKg(
        oilMassTons: Double,
        initialAcidNumber: Double,
        targetAcidNumber: Double,
        isZeoliteDryingOnly: Boolean
    ): Double {
        if (oilMassTons <= 0) return 0.0
        return if (isZeoliteDryingOnly) {
            oilMassTons * 1000.0 * 0.014
        } else {
            val deltaAcid = max(0.01, initialAcidNumber - targetAcidNumber)
            val percent = (deltaAcid / 0.05) * 1.5
            oilMassTons * 1000.0 * (percent / 100.0).coerceIn(0.01, 0.08)
        }
    }

    fun calculateVacuumTimeMinutes(
        tankFreeVolumeM3: Double,
        pumpSpeedM3H: Double,
        targetPressurePa: Double
    ): Double {
        if (tankFreeVolumeM3 <= 0 || pumpSpeedM3H <= 0) return 0.0
        val pStart = 101325.0
        val pEnd = targetPressurePa.coerceIn(10.0, pStart)
        val hours = 2.4 * (tankFreeVolumeM3 / pumpSpeedM3H) * ln(pStart / pEnd)
        return hours * 60.0
    }

    fun convertTanDelta(
        measuredTanDelta: Double,
        measuredTempC: Double,
        targetTempC: Double
    ): Double {
        val factor = exp(0.034 * (targetTempC - measuredTempC))
        return measuredTanDelta * factor
    }

    fun convertPressureFromMmHg(mmHg: Double): Map<String, Double> {
        val pa = mmHg * 133.322
        return mapOf(
            "Па (Pa)" to pa,
            "мбар (mbar)" to pa / 100.0,
            "кПа (kPa)" to pa / 1000.0,
            "атм (atm)" to mmHg / 760.0
        )
    }

    /**
     * Точка росы влажного воздуха при известном влагосодержании.
     *
     * Используется приближение Магнуса — Тетенса для парциального давления насыщения.
     * [moisturePpm] — объёмная доля влаги в газе (ppm), [pressurePa] — общее давление.
     */
    fun calculateDewPointC(moisturePpm: Double, pressurePa: Double = 101325.0): Double {
        if (moisturePpm <= 0 || pressurePa <= 0) return Double.NaN
        val partialPressurePa = pressurePa * moisturePpm / 1_000_000.0
        val partialPressureHpa = partialPressurePa / 100.0
        if (partialPressureHpa <= 0.01) return -60.0
        val gamma = ln(partialPressureHpa / 6.112)
        return 243.12 * gamma / (17.62 - gamma)
    }

    /**
     * Влагосодержание газа (ppm) при заданной точке росы и давлении.
     * Обратная функция к [calculateDewPointC].
     */
    fun moistureFromDewPointPpm(dewPointC: Double, pressurePa: Double = 101325.0): Double {
        if (pressurePa <= 0) return 0.0
        val saturationHpa = 6.112 * exp(17.62 * dewPointC / (243.12 + dewPointC))
        return saturationHpa * 100.0 / pressurePa * 1_000_000.0
    }

    /**
     * Оценка температуры вспышки в закрытом тигле по фракционному составу.
     * Эмпирическая зависимость для минеральных трансформаторных масел:
     * снижение вспышки на 1 °C соответствует потере ≈ 0.6 % лёгких фракций.
     *
     * [initialFlashPointC] — паспортная вспышка свежего масла,
     * [lightFractionPercent] — доля лёгких фракций, накопившихся при эксплуатации.
     */
    fun estimateFlashPointC(initialFlashPointC: Double, lightFractionPercent: Double): Double {
        if (initialFlashPointC <= 0) return Double.NaN
        val safeFraction = lightFractionPercent.coerceIn(0.0, 20.0)
        return initialFlashPointC - safeFraction / 0.6
    }

    /**
     * Минимально допустимая температура вспышки для класса напряжения.
     * По РД 34.45-51.300-97: не ниже 135 °C для масла, заливаемого в оборудование.
     */
    fun flashPointLimitC(voltageClassKv: Int): Double = if (voltageClassKv >= 110) 135.0 else 130.0
}