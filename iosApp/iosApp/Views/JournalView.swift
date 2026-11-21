import SwiftUI
import SwiftData

struct JournalView: View {
    @Environment(\.modelContext) private var context
    @StateObject private var vm = JournalViewModel()
    @State private var showingAddRecord = false
    @State private var exportRecord: JournalRecord?

    var body: some View {
        NavigationStack {
            Group {
                if vm.records.isEmpty {
                    EmptyJournalView()
                } else {
                    JournalListView(vm: vm, exportRecord: $exportRecord)
                }
            }
            .navigationTitle("Журнал")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Добавить") { showingAddRecord = true }
                }
            }
            .task { vm.attach(context) }
            .sheet(isPresented: $showingAddRecord) {
                AddJournalRecordView(onSaved: { vm.loadRecords() })
            }
            .sheet(item: $exportRecord) { record in
                if let url = try? PDFReportGenerator.generateReport(record: record) {
                    PDFShareView(fileURL: url) {
                        exportRecord = nil
                    }
                }
            }
        }
    }

    // MARK: - Пустое состояние

    struct EmptyJournalView: View {
        var body: some View {
            ContentUnavailableView(
                "Записей нет",
                systemImage: "doc.text",
                description: Text("Добавьте первый замер через кнопку «Добавить»")
            )
        }
    }

    // MARK: - Список

    struct JournalListView: View {
        @ObservedObject var vm: JournalViewModel
        @Binding var exportRecord: JournalRecord?

        var body: some View {
            List {
                ForEach(vm.records) { record in
                    JournalRow(record: record)
                        .swipeActions {
                            Button("Удалить", role: .destructive) {
                                vm.deleteRecord(record)
                            }
                            Button("Экспорт PDF") {
                                exportRecord = record
                            }
                            .tint(.blue)
                        }
                }
            }
        }
    }

    struct JournalRow: View {
        let record: JournalRecord

        var body: some View {
            VStack(alignment: .leading, spacing: 6) {
                HStack {
                    Text(record.substationName)
                        .font(.headline)
                    Spacer()
                    Text(record.recordDate)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                Text("\(record.transformerName) · \(record.unitModel)")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                HStack(spacing: 16) {
                    SpecChip(
                        label: "Влаж. Д/С",
                        value: "\(record.moistureBefore) / \(record.moistureAfter) ppm"
                    )
                    SpecChip(
                        label: "Пробив. Д/С",
                        value: "\(record.breakdownBefore) / \(record.breakdownAfter) кВ"
                    )
                }
                if let conclusion = record.conclusion, !conclusion.isEmpty {
                    Text(conclusion)
                        .font(.caption)
                        .foregroundColor(.secondary)
                        .padding(.top, 4)
                }
            }
            .padding(.vertical, 4)
        }
    }

    struct SpecChip: View {
        let label: String
        let value: String
        var body: some View {
            VStack(alignment: .leading, spacing: 2) {
                Text(label).font(.caption2).foregroundColor(.secondary)
                Text(value).font(.caption).fontWeight(.semibold)
            }
            .frame(maxWidth: .infinity)
        }
    }
}
