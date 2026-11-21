import SwiftUI
import Shared

@MainActor
final class CalculatorsViewModel: ObservableObject {

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
    @Published var pressureResults: [(unit: String, value: String)] = []

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

    private let calculators = EngineeringCalculators.shared
    private let catalog = TransformerCatalog.shared


    // MARK: - Расчеты

    func calculateUvsm() {
        let result = calculators.calculateUvsmProcessing(
            oilVolumeM3: number(uvsmVolume) ?? 0.0,
            initialMoisturePpm: number(uvsmMStart) ?? 0.0,
            targetMoisturePpm: number(uvsmMTarget) ?? 0.0,
            unitCapacityM3H: number(uvsmCapacity) ?? 4.0,
            passEfficiency: 0.65
        )

        uvsmCycles = "\(result.recommendedCycles)"
        uvsmHours = String(format: "%.1f ч", result.totalHours)
        uvsmTotalVolume = String(format: "%.2f м³", result.totalVolumeProcessedM3)
    }

    func calculateSorbent() {
        let mass = calculators.calculateSorbentMassKg(
            oilMassTons: number(sorbentOilMass) ?? 0.0,
            initialAcidNumber: number(sorbentInitialAcid) ?? 0.0,
            targetAcidNumber: number(sorbentTargetAcid) ?? 0.0,
            isZeoliteDryingOnly: sorbentZeoliteOnly
        )
        sorbentMass = String(format: "%.2f кг", mass)
    }

    func calculateVacuum() {
        let minutes = calculators.calculateVacuumTimeMinutes(
            tankFreeVolumeM3: number(vacuumTankVolume) ?? 0.0,
            pumpSpeedM3H: number(vacuumPumpSpeed) ?? 30.0,
            targetPressurePa: number(vacuumTargetPressure) ?? 133.0
        )

        vacuumMinutes = String(format: "%.0f мин", minutes)
        vacuumHours = String(format: "%.1f ч", minutes / 60.0)
    }

    func calculateTanDelta() {
        let converted = calculators.convertTanDelta(
            measuredTanDelta: number(tanMeasured) ?? 0.0,
            measuredTempC: number(tempMeasured) ?? 20.0,
            targetTempC: number(tempTarget) ?? 90.0
        )
        tanConverted = String(format: "%.2f %%", converted)
    }

    /// Пересчёт давления выполняется на стороне Swift:
    /// Kotlin возвращает `Map<String, Double>`, который неудобно читать через интероп.
    func convertPressure() {
        let mmHg = number(pressureMmHg) ?? 0.0
        let pa = mmHg * 133.322
        pressureResults = [
            ("Па (Pa)", String(format: "%.4f", pa)),
            ("мбар (mbar)", String(format: "%.4f", pa / 100.0)),
            ("кПа (kPa)", String(format: "%.4f", pa / 1000.0)),
            ("атм (atm)", String(format: "%.6f", mmHg / 760.0))
        ]
    }

    func calculateDewPoint() {
        if dewMode == 0 {
            let dewPoint = calculators.calculateDewPointC(
                moisturePpm: number(dewMoisturePpm) ?? 0.0,
                pressurePa: number(dewPressurePa) ?? 101325.0
            )
            dewResult = dewPoint.isNaN ? "—" : String(format: "%.1f °C", dewPoint)
        } else {
            let ppm = calculators.moistureFromDewPointPpm(
                dewPointC: number(dewPointC) ?? -40.0,
                pressurePa: number(dewPressurePa) ?? 101325.0
            )
            dewResult = String(format: "%.1f ppm", ppm)
        }
    }

    func calculateFlashPoint() {
        let estimated = calculators.estimateFlashPointC(
            initialFlashPointC: number(flashInitial) ?? 0.0,
            lightFractionPercent: number(flashLightFraction) ?? 0.0
        )
        let voltageClass: Int32 = flashVoltageClassIndex == 0 ? 35 : 110
        let limit = calculators.flashPointLimitC(voltageClassKv: voltageClass)

        flashEstimated = estimated.isNaN ? "—" : String(format: "%.0f °C", estimated)
        flashLimit = String(format: "не ниже %.0f °C", limit)
        flashPassing = !estimated.isNaN && estimated >= limit
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

    // MARK: - Вспомогательное

    private func number(_ text: String) -> Double? {
        let normalized = text
            .replacingOccurrences(of: ",", with: ".")
            .trimmingCharacters(in: .whitespaces)
        guard !normalized.isEmpty else { return nil }
        return Double(normalized)
    }

    private func formatVolume(_ value: Double) -> String {
        if value == Double(Int(value)) {
            return String(Int(value))
        }
        return String(format: "%.1f", value)
    }
}

