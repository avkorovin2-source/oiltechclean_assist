import SwiftUI
import SwiftData
import Shared

struct AcceptanceCheckView: View {
    @Environment(\.modelContext) private var context
    @StateObject private var vm: AcceptanceCheckViewModel
    @Environment(\.dismiss) private var dismiss

    init() {
        _vm = StateObject(wrappedValue: AcceptanceCheckViewModel(context: ModelContext(try! ModelContainer(for: AcceptanceCheck.self))))
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 14) {
                    SectionCard {
                        SectionHeader(title: "Чек-лист приёмки масла",
                                      subtitle: "Входной контроль перед заливкой")

                        LabeledTextField("Объект / подстанция", text: $vm.objectName)

                        HStack(spacing: 10) {
                            ResultTile(label: "Критичных", value: "\(vm.missingCritical) из \(vm.criticalTotal)", unit: nil)
                            ResultTile(label: "Вердикт", value: vm.approved ? "Допущено" : "Не допущено", unit: nil)
                        }

                        HairLine()

                        Button("Сохранить в историю") { vm.saveCheck() }
                            .buttonStyle(.borderedProminent)
                            .disabled(!vm.canSave)
                            .frame(maxWidth: .infinity)
                    }

                    ForEach(OilAcceptanceChecklist.groups, id: \.title) { group in
                        SectionCard {
                            SectionHeader(title: group.title)
                            ForEach(group.items, id: \.id) { item in
                                AcceptanceCheckRow(
                                    item: item,
                                    isChecked: vm.checkedIds.contains(item.id),
                                    onToggle: { vm.toggleItem(item.id) }
                                )
                            }
                        }
                    }

                    HistoryCard(vm: vm)
                }
                .padding(.vertical, 8)
            }
            .navigationTitle("Приёмка масла")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Назад") { dismiss() }
                }
            }
        }
    }

    // MARK: - Компоненты чек-листа

    struct LabeledTextField: View {
        let label: String
        @Binding var text: String
        var body: some View {
            VStack(alignment: .leading, spacing: 4) {
                Text(label).font(.caption).foregroundColor(.secondary)
                TextField("", text: $text)
                    .textFieldStyle(.roundedBorder)
            }
        }
    }

    struct AcceptanceCheckRow: View {
        let item: AcceptanceCheckItem
        let isChecked: Bool
        let onToggle: () -> Void

        var body: some View {
            HStack {
                Button(action: onToggle) {
                    Image(systemName: isChecked ? "checkmark.square.fill" : "square")
                        .foregroundColor(isChecked ? .blue : .secondary)
                        .font(.system(size: 24))
                }
                .buttonStyle(.plain)

                VStack(alignment: .leading, spacing: 2) {
                    Text(item.title)
                        .font(.body)
                        .fontWeight(item.isCritical ? .semibold : .regular)
                        .foregroundColor(item.isCritical ? .red : .primary)
                    Text(item.detail)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                Spacer()
            }
            .contentShape(Rectangle())
            .onTapGesture { onToggle() }
        }
    }

    struct HistoryCard: View {
        @ObservedObject var vm: AcceptanceCheckViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "История проверок",
                              subtitle: "Сохранённые снимки по объектам")

                if vm.history.isEmpty {
                    Text("Проверок пока нет. После сохранения здесь появится журнал.")
                        .font(.caption)
                        .foregroundColor(.secondary)
                } else {
                    ForEach(vm.history) { check in
                        HistoryRow(check: check, onDelete: { vm.deleteCheck($0) })
                    }
                }
            }
        }
    }

    struct HistoryRow: View {
        let check: AcceptanceCheck
        let onDelete: (AcceptanceCheck) -> Void

        var body: some View {
            let verdictColor: Color = check.approved ? .green : .red
            let verdictText = check.approved ? "Допущено" : "Не допущено"
            let checkedCount = check.checkedItemIds.split(separator: ",").filter { !$0.isEmpty }.count

            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text(check.objectName)
                        .font(.body)
                        .fontWeight(.semibold)
                    Text("\(formattedDate(check.checkedAt)) · отмечено \(checkedCount) из \(OilAcceptanceChecklist.totalItems)")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    Text(verdictText)
                        .font(.caption2)
                        .fontWeight(.bold)
                        .foregroundColor(verdictColor)
                }
                Spacer()
                Button(action: { onDelete(check) }) {
                    Image(systemName: "trash")
                        .foregroundColor(.secondary)
                }
                .buttonStyle(.plain)
            }
            .padding(.vertical, 4)
        }

        private func formattedDate(_ millis: Double) -> String {
            let date = Date(timeIntervalSince1970: millis / 1000)
            let formatter = DateFormatter()
            formatter.locale = Locale(identifier: "ru_RU")
            formatter.dateFormat = "dd.MM.yyyy HH:mm"
            return formatter.string(from: date)
        }
    }

    // MARK: - Базовые UI-компоненты

    struct SectionCard<Content: View>: View {
        let content: Content
        init(@ViewBuilder content: () -> Content) { self.content = content() }
        var body: some View {
            VStack(alignment: .leading, spacing: 0) { content }
                .padding(16)
                .background(Color(.systemBackground))
                .cornerRadius(18)
                .shadow(color: Color.black.opacity(0.04), radius: 4, x: 0, y: 2)
                .padding(.horizontal, 16)
        }
    }

    struct SectionHeader: View {
        let title: String
        let subtitle: String?
        init(title: String, subtitle: String? = nil) { self.title = title; self.subtitle = subtitle }
        var body: some View {
            VStack(alignment: .leading, spacing: 4) {
                Text(title).font(.headline).fontWeight(.bold)
                if let subtitle { Text(subtitle).font(.caption).foregroundColor(.secondary) }
            }
            .padding(.bottom, 10)
        }
    }

    struct ResultTile: View {
        let label: String
        let value: String
        let unit: String?
        var body: some View {
            VStack(alignment: .leading, spacing: 2) {
                Text(label.uppercased()).font(.caption2).foregroundColor(.secondary)
                HStack(alignment: .bottom, spacing: 2) {
                    Text(value).font(.title3).fontWeight(.bold)
                    if let unit { Text(unit).font(.caption).foregroundColor(.secondary) }
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(12)
            .background(Color.blue.opacity(0.1))
            .cornerRadius(12)
        }
    }

    struct HairLine: View {
        var body: some View { Divider().padding(.vertical, 6) }
    }
}