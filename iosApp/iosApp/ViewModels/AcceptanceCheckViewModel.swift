import SwiftUI
import SwiftData
import Shared

@MainActor
class AcceptanceCheckViewModel: ObservableObject {
    @Published var objectName = ""
    @Published var checkedIds: Set<String> = []
    @Published var history: [AcceptanceCheck] = []

    private let context: ModelContext
    private let checklist = OilAcceptanceChecklist

    var approved: Bool {
        checklist.isApproved(checkedIds: checkedIds)
    }
    var criticalTotal: Int { checklist.criticalItems }
    var missingCritical: Int { checklist.criticalIds.count { $0 !in checkedIds } }
    var canSave: Bool { !objectName.trimmingCharacters(in: .whitespaces).isEmpty && !checkedIds.isEmpty }

    init(context: ModelContext) {
        self.context = context
        loadHistory()
    }

    func loadHistory() {
        do {
            let descriptor = FetchDescriptor<AcceptanceCheck>(sortBy: [SortDescriptor(\AcceptanceCheck.checkedAt, order: .reverse)])
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
        guard !name.isEmpty, !checkedIds.isEmpty else { return }

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
        context.delete(check)
        do {
            try context.save()
            loadHistory()
        } catch {
            print("Ошибка удаления проверки: \(error)")
        }
    }
}
