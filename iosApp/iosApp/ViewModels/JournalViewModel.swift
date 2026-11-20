import SwiftUI
import SwiftData
import Shared

@MainActor
class JournalViewModel: ObservableObject {
    @Published var records: [JournalRecord] = []

    private let context: ModelContext

    init(context: ModelContext) {
        self.context = context
        loadRecords()
    }

    func loadRecords() {
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
        context.insert(record)
        try? context.save()
        loadRecords()
    }

    func deleteRecord(_ record: JournalRecord) {
        context.delete(record)
        try? context.save()
        loadRecords()
    }
}