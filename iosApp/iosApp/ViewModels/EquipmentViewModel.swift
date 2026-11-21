import SwiftUI
import Shared

@MainActor
final class EquipmentViewModel: ObservableObject {
    @Published var selectedUnit: EquipmentUnit?
    @Published var showAcceptance = false

    var equipmentList: [EquipmentUnit] {
        OfflineCatalogRepository.shared.equipmentList
    }

    /// Kotlin `List<Pair<String, String>>` экспортируется как `KotlinPair`,
    /// поэтому неисправности берутся через плоский хелпер `TroubleshootingRow`.
    func troubleshooting(for unit: EquipmentUnit) -> [TroubleshootingRow] {
        OfflineCatalogRepository.shared.troubleshootingRows(unit: unit)
    }

    /// `description` из Kotlin конфликтует с `NSObject.description`, читаем через метод.
    func summary(for unit: EquipmentUnit) -> String {
        OfflineCatalogRepository.shared.unitSummary(unit: unit)
    }
}

