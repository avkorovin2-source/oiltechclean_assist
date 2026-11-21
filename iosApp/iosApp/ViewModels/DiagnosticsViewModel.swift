import SwiftUI
import Shared

@MainActor
final class DiagnosticsViewModel: ObservableObject {

    // MARK: - ХАРГ входные данные
    @Published var h2 = "45"
    @Published var ch4 = "30"
    @Published var c2h2 = "0.5"
    @Published var c2h4 = "20"
    @Published var c2h6 = "15"

    @Published var diagnosis: HargDiagnosis?

    func calculateHarg() {
        let result = HargDiagnostics.shared.analyzeDissolvedGases(
            h2: number(h2) ?? 0.0,
            ch4: number(ch4) ?? 0.0,
            c2h2: number(c2h2) ?? 0.0,
            c2h4: number(c2h4) ?? 0.0,
            c2h6: number(c2h6) ?? 0.0
        )
        diagnosis = HargDiagnosis(
            faultTitle: result.faultTitle,
            severity: result.severity,
            explanation: result.explanation,
            recommendedAction: result.recommendedAction
        )
    }

    // MARK: - ГОСТ справочник

    var gostStandards: [GostStandardRow] {
        OfflineCatalogRepository.shared.gostStandards
    }

    private func number(_ text: String) -> Double? {
        let normalized = text
            .replacingOccurrences(of: ",", with: ".")
            .trimmingCharacters(in: .whitespaces)
        guard !normalized.isEmpty else { return nil }
        return Double(normalized)
    }
}
