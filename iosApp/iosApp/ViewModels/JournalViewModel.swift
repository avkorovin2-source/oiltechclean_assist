import SwiftUI
import SwiftData

@MainActor
final class JournalViewModel: ObservableObject {
    @Published var records: [JournalRecord] = []

    private var context: ModelContext?

    /// Контекст приходит из SwiftUI-окружения (единый ModelContainer приложения).
    func attach(_ context: ModelContext) {
        guard self.context == nil else { return }
        self.context = context
        loadRecords()
    }

    func loadRecords() {
        guard let context = context else {
            records = []
            return
        }
        do {
            let descriptor = FetchDescriptor<JournalRecord>(
                sortBy: [SortDescriptor(\JournalRecord.recordDate, order: .reverse)]
            )
            records = try context.fetch(descriptor)
        } catch {
            records = []
        }
    }

    func addRecord(_ record: JournalRecord) {
        guard let context = context else { return }
        context.insert(record)
        try? context.save()
        loadRecords()
    }

    func deleteRecord(_ record: JournalRecord) {
        guard let context = context else { return }
        context.delete(record)
        try? context.save()
        loadRecords()
    }
}
