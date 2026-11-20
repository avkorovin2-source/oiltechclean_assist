import SwiftUI
import Shared

@MainActor
class CalculatorsViewModel: ObservableObject {

    // MARK: - Входные данные (циклы УВСМ)
    @Published var uvsmVolume = "25.0"
    @Published var uvsmMStart = "35.0"
    @Published var uvsmMTarget = "8.0"
    @Published var uvsmCapacity = "4.0"
    @Published var uvsmSelectedTransformerLabel = "Выбрать из справочника"

    // MARK: - Результаты (циклы УВСМ)
    @Published var uvsmCycles = "—"
    @Published var uvsmHours = "—"
    @Published var uvsmTotalVolume = "—"

    // MARK: - Входные данные (сорбент)
    @Published var sorbentOilMass = "1.0"
    @Published var sorbentInitialAcid = "0.02"
    @Published var sorbentTargetAcid = "0.01"
    @Published var sorbentZeoliteOnly = false

    // MARK: - Результаты (сорбент)
    @Published var sorbentMass = "—"

    // MARK: - Входные данные (вакуум)
    @Published var vacuumTankVolume = "1.0"
    @Published var vacuumPumpSpeed = "30.0"
    @Published var vacuumTargetPressure = "133.0"

    // MARK: - Результаты (вакуум)
    @Published var vacuumMinutes = "—"
    @Published var vacuumHours = "—"

    // MARK: - Входные данные (tg δ)
    @Published var tanMeasured = "1.2"
    @Published var tempMeasured = "45.0"
    @Published var tempTarget = "90.0"

    // MARK: - Результаты (tg δ)
    @Published var tanConverted = "—"

    // MARK: - Давление
    @Published var pressureMmHg = "1.0"
    @Published var pressureResults: [String: String] = [:]

    // MARK: - Точка росы
    @Published var dewMode = 0
    @Published var dewMoisturePpm = "50"
    @Published var dewPointC = "-40"
    @Published var dewPressurePa = "101325"
    @Published var dewResult = "—"

    // MARK: - Вспышка
    @Published var flashInitial = "150"
    @Published var flashLightFraction = "3.0"
    @Published var flashVoltageClassIndex = 1
    @Published var flashEstimated = "—"
    @Published var flashLimit = "—"
    @Published var flashPassing = false

    // MARK: - Выбранный таб калькулятора (0..5)
    @Published var selectedTab = 0

    private let calculators = EngineeringCalculators()
    private let catalog = TransformerCatalog()

    // MARK: - Расчеты

    func calculateUvsm() {
        let volume = Double(uvsmVolume.replacingOccurrences(of: ",", with: ".")) ?? 0.0
        let mStart = Double(uvsmMStart.replacingOccurrences(of: ",", with: ".")) ?? 0.0
        let mTarget = Double(uvsmMTarget.replacingOccurrences(of: ",", with: ".")) ?? 0.0
        let capacity = Double(uvsmCapacity.replacingOccurrences(of: ",", with: ".")) ?? 4.0

        let result = calculators.calculateUvsmProcessing(
            oilVolumeM3: volume,
            initialMoisturePpm: mStart,
            targetMoisturePpm: mTarget,
            unitCapacityM3H: capacity
        )

        uvsmCycles = "\(result.recommendedCycles)"
        uvsmHours = String(format: "%.1f ч", result.totalHours)
        uvsmTotalVolume = String(format: "%.2f м³", result.totalVolumeProcessedM3)
    }

    func calculateSorbent() {
        let oilMass = Double(sorbentOilMass.replacingOccurrences(of: ",", with: ".")) ?? 0.0
        let initialAcid = Double(sorbentInitialAcid.replacingOccurrences(of: ",", with: ".")) ?? 0.0
        let targetAcid = Double(sorbentTargetAcid.replacingOccurrences(of: ",", with: ".")) ?? 0.0

        let mass = calculators.calculateSorbentMassKg(
            oilMassTons: oilMass,
            initialAcidNumber: initialAcid,
            targetAcidNumber: targetAcid,
            isZeoliteDryingOnly: sorbentZeoliteOnly
        )

        sorbentMass = String(format: "%.2f кг", mass)
    }

    func calculateVacuum() {
        let tank = Double(vacuumTankVolume.replacingOccurrences(of: ",", with: ".")) ?? 0.0
        let speed = Double(vacuumPumpSpeed.replacingOccurrences(of: ",", with: ".")) ?? 0.0
        let pressure = Double(vacuumTargetPressure.replacingOccurrences(of: ",", with: ".")) ?? 133.0

        let minutes = calculators.calculateVacuumTimeMinutes(
            tankFreeVolumeM3: tank,
            pumpSpeedM3H: speed,
            targetPressurePa: pressure
        )

        vacuumMinutes = String(format: "%.0f мин", minutes)
        vacuumHours = String(format: "%.1f ч", minutes / 60.0)
    }

    func calculateTanDelta() {
        let measured = Double(tanMeasured.replacingOccurrences(of: ",", with: ".")) ?? 0.0
        let tempMeas = Double(tempMeasured.replacingOccurrences(of: ",", with: ".")) ?? 20.0
        let tempTarg = Double(tempTarget.replacingOccurrences(of: ",", with: ".")) ?? 90.0

        let converted = calculators.convertTanDelta(
            measuredTanDelta: measured,
            measuredTempC: tempMeas,
            targetTempC: tempTarg
        )

        tanConverted = String(format: "%.2f %", converted)
    }

    func convertPressure() {
        let mmHg = Double(pressureMmHg.replacingOccurrences(of: ",", with: ".")) ?? 0.0
        let map = calculators.convertPressureFromMmHg(mmHg: mmHg)
        pressureResults = map.mapValues { String(format: "%.4f", $0) }
    }

    func calculateDewPoint() {
        if dewMode == 0 {
            let ppm = Double(dewMoisturePpm.replacingOccurrences(of: ",", with: ".")) ?? 0.0
            let pa = Double(dewPressurePa.replacingOccurrences(of: ",", with: ".")) ?? 101325.0
            let dp = calculators.calculateDewPointC(moisturePpm: ppm, pressurePa: pa)
            if dp.isNaN {
                dewResult = "—"
            } else {
                dewResult = String(format: "%.1f °C", dp)
            }
        } else {
            let dp = Double(dewPointC.replacingOccurrences(of: ",", with: ".")) ?? -40.0
            let pa = Double(dewPressurePa.replacingOccurrences(of: ",", with: ".")) ?? 101325.0
            let ppm = calculators.moistureFromDewPointPpm(dewPointC: dp, pressurePa: pa)
            dewResult = String(format: "%.1f ppm", ppm)
        }
    }

    func calculateFlashPoint() {
        let initial = Double(flashInitial.replacingOccurrences(of: ",", with: ".")) ?? 0.0
        let fraction = Double(flashLightFraction.replacingOccurrences(of: ",", with: ".")) ?? 0.0
        let voltageClass = flashVoltageClassIndex == 0 ? 35 : 110

        let estimated = calculators.estimateFlashPointC(
            initialFlashPointC: initial,
            lightFractionPercent: fraction
        )
        let limit = calculators.flashPointLimitC(voltageClassKv: Int32(voltageClass))

        if estimated.isNaN {
            flashEstimated = "—"
        } else {
            flashEstimated = String(format: "%.0f °C", estimated)
        }
        flashLimit = String(format: "не ниже %.0f °C", limit)
        flashPassing = !estimated.isNaN() && estimated >= limit
    }

    // MARK: - Справочник трансформаторов

    var transformerOptions: [String] {
        catalog.transformers.map { "\($0.label) — \(formatVolume($0.oilVolumeM3)) м³" }
    }

    func selectTransformer(by label: String) {
        guard let type = catalog.transformers.first(where: { label.hasPrefix($0.label) }) else { return }
        uvsmVolume = String(format: "%.1f", type.oilVolumeM3)
        uvsmSelectedTransformerLabel = label
    }

    private func formatVolume(_ value: Double) -> String {
        if value == Double(Int(value)) {
            return String(Int(value))
        }
        return String(format: "%.1f", value)
    }
}