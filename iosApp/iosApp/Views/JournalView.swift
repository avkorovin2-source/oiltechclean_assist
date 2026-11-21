import SwiftUI
import SwiftData

struct JournalView: View {
    @Environment(\.modelContext) private var context
    @StateObject private var vm = JournalViewModel()
    @State private var showingAddRecord = false
    @State private var exportRecord: JournalRecord?

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 14) {
                    BrandHeroHeader(
                        title: "Журнал",
                        subtitle: "Замеры и протоколы",
                        icon: "doc.text.fill",
                        actionTitle: "Добавить",
                        action: { showingAddRecord = true }
                    )

                    if vm.records.isEmpty {
                        EmptyJournalView()
                    } else {
                        JournalListContent(vm: vm, exportRecord: $exportRecord)
                    }
                }
                .padding(.vertical, 8)
            }
            .brandScreen()
            .toolbar(.hidden, for: .navigationBar)
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
            BrandCard {
                VStack(spacing: 10) {
                    Image(systemName: "doc.text")
                        .font(.system(size: 34))
                        .foregroundColor(BrandColor.indigo)
                        .frame(width: 72, height: 72)
                        .background(Circle().fill(BrandColor.mistDeep))
                    Text("Записей нет")
                        .font(.headline)
                        .foregroundColor(BrandColor.ink)
                    Text("Добавьте первый замер через кнопку «Добавить»")
                        .font(.caption)
                        .foregroundColor(BrandColor.slate)
                        .multilineTextAlignment(.center)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 8)
            }
        }
    }

    // MARK: - Список

    struct JournalListContent: View {
        @ObservedObject var vm: JournalViewModel
        @Binding var exportRecord: JournalRecord?

        var body: some View {
            ForEach(vm.records) { record in
                BrandCard {
                    JournalRow(record: record)

                    HStack(spacing: 10) {
                        Button("Экспорт PDF") { exportRecord = record }
                            .buttonStyle(BrandSecondaryButtonStyle())

                        Button(role: .destructive) {
                            vm.deleteRecord(record)
                        } label: {
                            Text("Удалить")
                                .font(.subheadline)
                                .fontWeight(.semibold)
                                .foregroundColor(BrandColor.danger)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 11)
                                .background(
                                    RoundedRectangle(cornerRadius: 16, style: .continuous)
                                        .fill(BrandColor.dangerSoft)
                                )
                        }
                        .buttonStyle(.plain)
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
                        .foregroundColor(BrandColor.ink)
                    Spacer()
                    Text(record.recordDate)
                        .font(.caption)
                        .foregroundColor(BrandColor.slate)
                }
                Text("\(record.transformerName) · \(record.unitModel)")
                    .font(.subheadline)
                    .foregroundColor(BrandColor.slate)
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
                        .foregroundColor(BrandColor.slate)
                        .padding(.top, 4)
                }
            }
        }
    }

    struct SpecChip: View {
        let label: String
        let value: String
        var body: some View {
            VStack(alignment: .leading, spacing: 2) {
                Text(label)
                    .font(.caption2)
                    .foregroundColor(BrandColor.slateLight)
                Text(value)
                    .font(.caption)
                    .fontWeight(.semibold)
                    .foregroundColor(BrandColor.ink)
            }
            .frame(maxWidth: .infinity)
        }
    }
}
