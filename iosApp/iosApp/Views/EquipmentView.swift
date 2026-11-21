import SwiftUI
import Shared

struct EquipmentView: View {
    @StateObject private var vm = EquipmentViewModel()

    var body: some View {
        NavigationStack {
            Group {
                if vm.showAcceptance {
                    AcceptanceCheckView(onBack: { vm.showAcceptance = false })
                } else if let unit = vm.selectedUnit {
                    EquipmentDetailsView(
                        unit: unit,
                        summary: vm.summary(for: unit),
                        troubleshooting: vm.troubleshooting(for: unit),
                        onBack: { vm.selectedUnit = nil }
                    )
                } else {
                    EquipmentListView(vm: vm)
                }
            }
            .navigationTitle("Установки")
            .navigationBarTitleDisplayMode(.large)
        }
    }

    // MARK: - Список установок

    struct EquipmentListView: View {
        @ObservedObject var vm: EquipmentViewModel

        var body: some View {
            ScrollView {
                VStack(spacing: 14) {
                    SectionCard {
                        SectionHeader(title: "Каталог установок ОИЛТЕХКЛИН")

                        Button("Чек-лист приёмки масла") {
                            vm.showAcceptance = true
                        }
                        .buttonStyle(.borderedProminent)
                        .frame(maxWidth: .infinity)

                        HStack(spacing: 10) {
                            ResultTile(label: "Пунктов", value: "\(OilAcceptanceChecklist.shared.totalItems)", unit: nil)
                            ResultTile(label: "Критичных", value: "\(OilAcceptanceChecklist.shared.criticalItems)", unit: nil)
                        }

                        HairLine()
                    }

                    ForEach(vm.equipmentList, id: \.id) { unit in
                        SectionCard {
                            Button { vm.selectedUnit = unit } label: {
                                VStack(alignment: .leading, spacing: 8) {
                                    HStack {
                                        Box {
                                            Image(systemName: "cpu.fill")
                                                .font(.system(size: 22))
                                                .foregroundColor(.white)
                                        }
                                        .frame(width: 46, height: 46)
                                        .background(.blue.opacity(0.8))
                                        .cornerRadius(14)

                                        VStack(alignment: .leading, spacing: 2) {
                                            Text(unit.name)
                                                .font(.title3)
                                                .fontWeight(.bold)
                                            Text(unit.series)
                                                .font(.caption)
                                                .foregroundColor(.secondary)
                                        }
                                        Spacer()
                                        Image(systemName: "chevron.right")
                                            .foregroundColor(.secondary)
                                    }

                                    HStack(spacing: 10) {
                                        StatusBadge(text: "\(unit.capacityM3H) м³/ч", color: .blue)
                                        StatusBadge(text: "\(unit.heaterPowerKw) кВт", color: .purple)
                                    }

                                    Text(vm.summary(for: unit))
                                        .font(.body)
                                        .foregroundColor(.secondary)
                                }
                            }
                            .buttonStyle(.plain)
                        }
                    }
                }
                .padding(.vertical, 8)
            }
        }
    }

    // MARK: - Детали установки

    struct EquipmentDetailsView: View {
        let unit: EquipmentUnit
        let summary: String
        let troubleshooting: [TroubleshootingRow]
        let onBack: () -> Void

        var body: some View {
            ScrollView {
                VStack(spacing: 14) {
                    HStack {
                        Button(action: onBack) {
                            Image(systemName: "arrow.left")
                                .foregroundColor(.blue)
                            Text("К списку установок")
                                .foregroundColor(.blue)
                                .fontWeight(.semibold)
                        }
                        Spacer()
                    }
                    .padding(.horizontal)

                    SectionCard {
                        Text(unit.name)
                            .font(.title2)
                            .fontWeight(.bold)
                        StatusBadge(text: unit.series, color: .purple)
                        Text(summary)
                            .font(.body)
                            .foregroundColor(.secondary)

                        HairLine()

                        SpecRow(label: "Производительность", value: "\(unit.capacityM3H) м³/ч")
                        SpecRow(label: "Мощность нагревателя", value: "\(unit.heaterPowerKw) кВт")
                        SpecRow(label: "Остаточная влага", value: "≤ \(unit.residualMoisturePpm) ppm")
                        SpecRow(label: "Остаточное газосодержание", value: "≤ \(unit.residualGasPercent) %")
                    }

                    SectionCard {
                        SectionHeader(title: "Чек-лист предпусковой проверки",
                                      subtitle: "Отметьте выполненные пункты")

                        ForEach(unit.startupChecklist, id: \.self) { step in
                            AcceptanceRow(title: step)
                        }
                    }

                    SectionCard {
                        SectionHeader(title: "Аварийный помощник",
                                      subtitle: "Типовые неисправности и решения")

                        ForEach(Array(troubleshooting.enumerated()), id: \.offset) { index, item in
                            if index > 0 { HairLine() }
                            VStack(alignment: .leading, spacing: 4) {
                                Text(item.problem)
                                    .font(.body)
                                    .fontWeight(.bold)
                                Text(item.solution)
                                    .font(.body)
                                    .foregroundColor(.secondary)
                            }
                        }
                    }
                }
                .padding(.vertical, 8)
            }
            .navigationTitle(unit.name)
            .navigationBarTitleDisplayMode(.inline)
        }
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

struct SpecRow: View {
    let label: String
    let value: String
    var body: some View {
        HStack {
            Text(label).font(.body).foregroundColor(.secondary)
            Spacer()
            Text(value).font(.body).fontWeight(.semibold)
        }
        .padding(.vertical, 2)
    }
}

struct StatusBadge: View {
    let text: String
    let color: Color
    var body: some View {
        Text(text)
            .font(.caption)
            .fontWeight(.bold)
            .foregroundColor(color)
            .padding(.horizontal, 10)
            .padding(.vertical, 5)
            .background(color.opacity(0.12))
            .cornerRadius(20)
    }
}

struct AcceptanceRow: View {
    let title: String
    var body: some View {
        HStack {
            Image(systemName: "circle")
                .foregroundColor(.secondary)
            Text(title)
                .font(.body)
        }
        .padding(.vertical, 2)
    }
}

struct HairLine: View {
    var body: some View { Divider().padding(.vertical, 6) }
}

struct Box<Content: View>: View {
    let content: Content
    init(@ViewBuilder content: () -> Content) { self.content = content() }
    var body: some View { content }
}