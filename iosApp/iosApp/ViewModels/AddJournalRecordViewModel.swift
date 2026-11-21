import SwiftUI
import SwiftData
import Shared

@MainActor
final class AddJournalRecordViewModel: ObservableObject {
    @Published var substationName = ""
    @Published var transformerName = ""
    @Published var unitModel = ""
    @Published var recordDate = ""
    @Published var voltageClass = ""

    @Published var moistureBefore = ""
    @Published var moistureAfter = ""
    @Published var breakdownBefore = ""
    @Published var breakdownAfter = ""
    @Published var acidNumberBefore = ""
    @Published var acidNumberAfter = ""
    @Published var tanDeltaBefore = ""
    @Published var tanDeltaAfter = ""
    @Published var oilTemperatureC = ""
    @Published var gasContentAfter = ""
    @Published var flashPointAfter = ""

    @Published var remarks = ""
    @Published var conclusion = ""

    @Published var gostResult: GostEvaluation?
    @Published var showingGostResult = false

    private var context: ModelContext?

    init() {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "ru_RU")
        formatter.dateFormat = "dd.MM.yyyy HH:mm"
        recordDate = formatter.string(from: Date())
    }

    func attach(_ context: ModelContext) {
        guard self.context == nil else { return }
        self.context = context
    }

    func save() {
        guard let context = context else { return }

        let record = JournalRecord(
            id: Int(Date().timeIntervalSince1970),
            substationName: substationName,
            transformerName: transformerName,
            unitModel: unitModel,
            recordDate: recordDate,
            moistureBefore: number(moistureBefore) ?? 0.0,
            moistureAfter: number(moistureAfter) ?? 0.0,
            breakdownBefore: number(breakdownBefore) ?? 0.0,
            breakdownAfter: number(breakdownAfter) ?? 0.0,
            acidNumberBefore: number(acidNumberBefore),
            acidNumberAfter: number(acidNumberAfter),
            tanDeltaBefore: number(tanDeltaBefore),
            tanDeltaAfter: number(tanDeltaAfter),
            remarks: remarks.isEmpty ? nil : remarks,
            voltageClass: voltageClass.isEmpty ? nil : voltageClass,
            oilTemperatureC: number(oilTemperatureC),
            gasContentAfter: number(gasContentAfter),
            flashPointAfter: number(flashPointAfter),
            conclusion: conclusion.isEmpty ? nil : conclusion
        )
        context.insert(record)
        try? context.save()
    }

    func evaluateGost() {
        guard let standard = OfflineCatalogRepository.shared.gostStandards
            .first(where: { $0.voltageClass == voltageClass }) else {
            gostResult = nil
            showingGostResult = false
            return
        }

        let report = GostComplianceEvaluator.shared.evaluateValues(
            standard: standard,
            breakdownVoltageKv: number(breakdownBefore) ?? .nan,
            moisturePpm: number(moistureBefore) ?? .nan,
            gasContentPercent: number(gasContentAfter) ?? .nan,
            acidNumber: number(acidNumberBefore) ?? .nan,
            tanDelta90C: number(tanDeltaBefore) ?? .nan
        )

        gostResult = GostEvaluation(
            isCompliant: report.isCompliant,
            hasData: report.hasData,
            summary: report.summaryText,
            voltageClass: report.voltageClass,
            failedLines: report.failedLines
        )
        showingGostResult = true
        conclusion = GostComplianceEvaluator.shared.buildConclusion(report: report)
    }

    /// Разбор числа с поддержкой запятой как десятичного разделителя.
    private func number(_ text: String) -> Double? {
        let normalized = text
            .replacingOccurrences(of: ",", with: ".")
            .trimmingCharacters(in: .whitespaces)
        guard !normalized.isEmpty else { return nil }
        return Double(normalized)
    }
}
