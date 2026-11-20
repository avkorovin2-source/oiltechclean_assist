import SwiftUI
import SwiftData
import Shared

@MainActor
class AddJournalRecordViewModel: ObservableObject {
    @Published var substationName = ""
    @Published var transformerName = ""
    @Published var unitModel = ""
    @Published var recordDate = ""
    @Published var moistureBefore = ""
    @Published var moistureAfter = ""
    @Published var breakdownBefore = ""
    @Published var breakdownAfter = ""
    @Published var acidNumberBefore = ""
    @Published var acidNumberAfter = ""
    @Published var tanDeltaBefore = ""
    @Published var tanDeltaAfter = ""
    @Published var remarks = ""
    @Published var voltageClass = ""
    @Published var oilTemperatureC = ""
    @Published var gasContentAfter = ""
    @Published var flashPointAfter = ""
    @Published var conclusion = ""

    @Published var gostReport: GostComplianceEvaluator.ComplianceReport?
    @Published var showingGostResult = false

    private let context: ModelContext
    private let evaluator = GostComplianceEvaluator()

    init(context: ModelContext) {
        self.context = context
        let formatter = DateFormatter()
        formatter.dateFormat = "dd.MM.yyyy HH:mm"
        recordDate = formatter.string(from: Date())
    }

    func save() {
        let record = JournalRecord(
            substationName: substationName,
            transformerName: transformerName,
            unitModel: unitModel,
            recordDate: recordDate,
            moistureBefore: Double(moistureBefore.replacingOccurrences(of: ",", with: ".")) ?? 0.0,
            moistureAfter: Double(moistureAfter.replacingOccurrences(of: ",", with: ".")) ?? 0.0,
            breakdownBefore: Double(breakdownBefore.replacingOccurrences(of: ",", with: ".")) ?? 0.0,
            breakdownAfter: Double(breakdownAfter.replacingOccurrences(of: ",", with: ".")) ?? 0.0,
            acidNumberBefore: Double(acidNumberBefore.replacingOccurrences(of: ",", with: ".")),
            acidNumberAfter: Double(acidNumberAfter.replacingOccurrences(of: ",", with: ".")),
            tanDeltaBefore: Double(tanDeltaBefore.replacingOccurrences(of: ",", with: ".")),
            tanDeltaAfter: Double(tanDeltaAfter.replacingOccurrences(of: ",", with: ".")),
            remarks: remarks.isEmpty ? nil : remarks,
            voltageClass: voltageClass.isEmpty ? nil : voltageClass,
            oilTemperatureC: Double(oilTemperatureC.replacingOccurrences(of: ",", with: ".")),
            gasContentAfter: Double(gasContentAfter.replacingOccurrences(of: ",", with: ".")),
            flashPointAfter: Double(flashPointAfter.replacingOccurrences(of: ",", with: ".")),
            conclusion: conclusion.isEmpty ? nil : conclusion
        )
        context.insert(record)
        try? context.save()
    }

    func evaluateGost() {
        guard let standard = OfflineCatalogRepository.gostStandards.first(where: { $0.voltageClass == voltageClass }) else {
            gostReport = nil
            showingGostResult = false
            return
        }
        gostReport = evaluator.evaluate(
            standard: standard,
            breakdownVoltageKv: Double(breakdownBefore.replacingOccurrences(of: ",", with: ".")),
            moisturePpm: Double(moistureBefore.replacingOccurrences(of: ",", with: ".")),
            gasContentPercent: Double(gasContentAfter.replacingOccurrences(of: ",", with: ".")),
            acidNumber: Double(acidNumberBefore.replacingOccurrences(of: ",", with: ".")),
            tanDelta90C: Double(tanDeltaBefore.replacingOccurrences(of: ",", with: "."))
        )
        showingGostResult = true
    }
}