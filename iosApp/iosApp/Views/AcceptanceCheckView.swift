import SwiftUI
import SwiftData
import Shared

struct AcceptanceCheckView: View {
    @Environment(\.modelContext) private var context
    @StateObject private var vm = AcceptanceCheckViewModel()

    let onBack: () -> Void

    init(onBack: @escaping () -> Void = {}) {
        self.onBack = onBack
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 14) {
                    BrandCard {
                        BrandSectionHeader(title: "Чек-лист приёмки масла",
                                      subtitle: "Входной контроль перед заливкой")

                        BrandLabeledTextField("Объект / подстанция", text: $vm.objectName)

                        HStack(spacing: 10) {
                            BrandResultTile(label: "Критичных", value: "\(vm.missingCritical) из \(vm.criticalTotal)", unit: nil)
                            BrandResultTile(label: "Вердикт", value: vm.approved ? "Допущено" : "Не допущено", unit: nil)
                        }

                        BrandHairLine()

                        Button("Сохранить в историю") { vm.saveCheck() }
                            .buttonStyle(BrandPrimaryButtonStyle())
                            .disabled(!vm.canSave)
                            .frame(maxWidth: .infinity)
                    }

                    ForEach(OilAcceptanceChecklist.shared.groups, id: \.title) { group in
                        BrandCard {
                            BrandSectionHeader(title: group.title)
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
            .brandScreen()
            .task { vm.attach(context) }
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Назад") { onBack() }
                }
            }
        }
    }

    // MARK: - Компоненты чек-листа

    struct AcceptanceCheckRow: View {
        let item: AcceptanceCheckItem
        let isChecked: Bool
        let onToggle: () -> Void

        var body: some View {
            HStack {
                Button(action: onToggle) {
                    Image(systemName: isChecked ? "checkmark.square.fill" : "square")
                        .foregroundColor(isChecked ? BrandColor.indigo : BrandColor.slateLight)
                        .font(.system(size: 24))
                }
                .buttonStyle(.plain)

                VStack(alignment: .leading, spacing: 2) {
                    Text(item.title)
                        .font(.body)
                        .fontWeight(item.isCritical ? .semibold : .regular)
                        .foregroundColor(item.isCritical ? BrandColor.danger : BrandColor.ink)
                    Text(item.detail)
                        .font(.caption)
                        .foregroundColor(BrandColor.slate)
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
            BrandCard {
                BrandSectionHeader(title: "История проверок",
                              subtitle: "Сохранённые снимки по объектам")

                if vm.history.isEmpty {
                    Text("Проверок пока нет. После сохранения здесь появится журнал.")
                        .font(.caption)
                        .foregroundColor(BrandColor.slate)
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
            let verdictColor: Color = check.approved ? BrandColor.success : BrandColor.danger
            let verdictText = check.approved ? "Допущено" : "Не допущено"
            let checkedCount = check.checkedItemIds.split(separator: ",").filter { !$0.isEmpty }.count

            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text(check.objectName)
                        .font(.body)
                        .fontWeight(.semibold)
                    Text("\(formattedDate(check.checkedAt)) · отмечено \(checkedCount) из \(OilAcceptanceChecklist.shared.totalItems)")
                        .font(.caption)
                        .foregroundColor(BrandColor.slate)
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
}
