import SwiftUI
import Shared

@MainActor
class DiagnosticsViewModel: ObservableObject {

    // MARK: - ХАРГ входные данные
    @Published var h2 = "45"
    @Published var ch4 = "30"
    @Published var c2h2 = "0.5"
    @Published var c2h4 = "20"
    @Published var c2h6 = "15"

    @Published var diagnosis: HargDiagnostics.HargResult?

    private let hargDiagnostics = HargDiagnostics()

    func calculateHarg() {
        diagnosis = hargDiagnostics.analyzeDissolvedGases(
            h2: Double(h2.replacingOccurrences(of: ",", with: ".")) ?? 0.0,
            ch4: Double(ch4.replacingOccurrences(of: ",", with: ".")) ?? 0.0,
            c2h2: Double(c2h2.replacingOccurrences(of: ",", with: ".")) ?? 0.0,
            c2h4: Double(c2h4.replacingOccurrences(of: ",", with: ".")) ?? 0.0,
            c2h6: Double(c2h6.replacingOccurrences(of: ",", with: ".")) ?? 0.0
        )
    }

    // MARK: - ГОСТ справочник

    var gostStandards: [GostStandardRow] {
        OfflineCatalogRepository.gostStandards
    }
}