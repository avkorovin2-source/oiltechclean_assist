import SwiftUI
import Shared

@MainActor
class EquipmentViewModel: ObservableObject {
    @Published var selectedUnit: EquipmentUnit?
    @Published var showAcceptance = false

    var equipmentList: [EquipmentUnit] {
        OfflineCatalogRepository.equipmentList
    }
}