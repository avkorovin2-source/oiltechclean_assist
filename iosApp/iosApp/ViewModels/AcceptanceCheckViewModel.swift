import SwiftUI
import SwiftData
import Shared

@MainActor
final class AcceptanceCheckViewModel: ObservableObject {
    @Published var objectName = ""
    @Published var checkedIds: Set<String> = []
    @Published var history: [AcceptanceCheck] = []

    private var context: ModelContext?

    /// Kotlin-`object` экспортируется в Swift как класс с синглтоном `shared`.
    private var checklist: OilAcceptanceChecklist { OilAcceptanceChecklist.shared }

    var approved: Bool {
        checklist.isApprovedList(checkedIds: Array(checkedIds))
    }

    var criticalTotal: Int { Int(checklist.criticalItems) }

    var missingCritical: Int {
        Int(checklist.missingCriticalCount(checkedIds: Array(checkedIds)))
    }

    var canSave: Bool {
        !objectName.trimmingCharacters(in: .whitespaces).isEmpty && !checkedIds.isEmpty
    }

    func attach(_ context: ModelContext) {
        guard self.context == nil else { return }
        self.context = context
        loadHistory()
    }

    func loadHistory() {
        guard let context = context else {
            history = []
            return
        }
        do {
            let descriptor = FetchDescriptor<AcceptanceCheck>(
                sortBy: [SortDescriptor(\AcceptanceCheck.checkedAt, order: .reverse)]
            )
            history = try context.fetch(descriptor)
        } catch {
            history = []
        }
    }

    func toggleItem(_ itemId: String) {
        if checkedIds.contains(itemId) {
            checkedIds.remove(itemId)
        } else {
            checkedIds.insert(itemId)
        }
    }

    func resetChecklist() {
        checkedIds.removeAll()
        objectName = ""
    }

    func saveCheck() {
        let name = objectName.trimmingCharacters(in: .whitespaces)
        guard let context = context, !name.isEmpty, !checkedIds.isEmpty else { return }

        let entity = AcceptanceCheck(
            objectName: name,
            checkedAt: Date().timeIntervalSince1970 * 1000,
            checkedItemIds: checkedIds.sorted().joined(separator: ","),
            approved: approved
        )
        context.insert(entity)
        do {
            try context.save()
            loadHistory()
            resetChecklist()
        } catch {
            print("Ошибка сохранения проверки: \(error)")
        }
    }

    func deleteCheck(_ check: AcceptanceCheck) {
        guard let context = context else { return }
        context.delete(check)
        do {
            try context.save()
            loadHistory()
        } catch {
            print("Ошибка удаления проверки: \(error)")
        }
    }
}
