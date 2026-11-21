import Foundation

/// Плоское представление результата ХАРГ-диагностики.
///
/// Вложенные типы Kotlin (`HargDiagnostics.HargResult`) не используются напрямую
/// в SwiftUI-слое: так экран не зависит от правил экспорта вложенных классов.
struct HargDiagnosis: Identifiable {
    let id = UUID()
    let faultTitle: String
    let severity: String
    let explanation: String
    let recommendedAction: String
}

/// Плоское представление заключения по ГОСТ / РД.
struct GostEvaluation: Identifiable {
    let id = UUID()
    let isCompliant: Bool
    let hasData: Bool
    let summary: String
    let voltageClass: String
    let failedLines: [String]
}
