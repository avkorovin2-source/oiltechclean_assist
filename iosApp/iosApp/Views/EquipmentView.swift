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
            .brandScreen()
            .toolbar(.hidden, for: .navigationBar)
        }
    }

    // MARK: - Список установок

    struct EquipmentListView: View {
        @ObservedObject var vm: EquipmentViewModel

        var body: some View {
            ScrollView {
                VStack(spacing: 14) {
                    BrandHeroHeader(
                        title: "Установки",
                        subtitle: "Паспорта и чек-листы",
                        icon: "wrench.and.screwdriver.fill"
                    )

                    BrandCard {
                        BrandSectionHeader(title: "Каталог установок ОИЛТЕХКЛИН", icon: "shippingbox.fill")

                        Button("Чек-лист приёмки масла") {
                            vm.showAcceptance = true
                        }
                        .buttonStyle(BrandPrimaryButtonStyle())
                        .frame(maxWidth: .infinity)

                        HStack(spacing: 10) {
                            BrandResultTile(label: "Пунктов", value: "\(OilAcceptanceChecklist.shared.totalItems)", unit: nil)
                            BrandResultTile(label: "Критичных", value: "\(OilAcceptanceChecklist.shared.criticalItems)", unit: nil)
                        }
                    }

                    ForEach(vm.equipmentList, id: \.id) { unit in
                        BrandCard {
                            Button { vm.selectedUnit = unit } label: {
                                VStack(alignment: .leading, spacing: 8) {
                                    HStack {
                                        RoundedRectangle(cornerRadius: 14, style: .continuous)
                                            .fill(BrandColor.brandGradient)
                                            .frame(width: 46, height: 46)
                                            .overlay(
                                                Image(systemName: "cpu.fill")
                                                    .font(.system(size: 21))
                                                    .foregroundColor(.white)
                                            )

                                        VStack(alignment: .leading, spacing: 2) {
                                            Text(unit.name)
                                                .font(.title3)
                                                .fontWeight(.bold)
                                                .foregroundColor(BrandColor.ink)
                                            Text(unit.series)
                                                .font(.caption)
                                                .foregroundColor(BrandColor.slate)
                                        }
                                        Spacer()
                                        Image(systemName: "chevron.right")
                                            .foregroundColor(BrandColor.slateLight)
                                    }

                                    HStack(spacing: 10) {
                                        BrandStatusBadge(text: "\(unit.capacityM3H) м³/ч", color: BrandColor.indigo)
                                        BrandStatusBadge(text: "\(unit.heaterPowerKw) кВт", color: BrandColor.purple)
                                    }

                                    Text(vm.summary(for: unit))
                                        .font(.subheadline)
                                        .foregroundColor(BrandColor.slate)
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
                            HStack(spacing: 6) {
                                Image(systemName: "chevron.left")
                                Text("К списку установок")
                                    .fontWeight(.semibold)
                            }
                            .font(.subheadline)
                            .foregroundColor(BrandColor.deep)
                        }
                        Spacer()
                    }
                    .padding(.horizontal, 16)

                    BrandCard {
                        Text(unit.name)
                            .font(.title2)
                            .fontWeight(.heavy)
                            .foregroundColor(BrandColor.ink)
                        BrandStatusBadge(text: unit.series, color: BrandColor.purple)
                        Text(summary)
                            .font(.subheadline)
                            .foregroundColor(BrandColor.slate)

                        BrandHairLine()

                        BrandSpecRow(label: "Производительность", value: "\(unit.capacityM3H) м³/ч")
                        BrandSpecRow(label: "Мощность нагревателя", value: "\(unit.heaterPowerKw) кВт")
                        BrandSpecRow(label: "Остаточная влага", value: "≤ \(unit.residualMoisturePpm) ppm")
                        BrandSpecRow(label: "Остаточное газосодержание", value: "≤ \(unit.residualGasPercent) %")
                    }

                    BrandCard {
                        BrandSectionHeader(title: "Чек-лист предпусковой проверки",
                                      subtitle: "Отметьте выполненные пункты")

                        ForEach(unit.startupChecklist, id: \.self) { step in
                            AcceptanceRow(title: step)
                        }
                    }

                    BrandCard {
                        BrandSectionHeader(title: "Аварийный помощник",
                                      subtitle: "Типовые неисправности и решения")

                        ForEach(Array(troubleshooting.enumerated()), id: \.offset) { index, item in
                            if index > 0 { BrandHairLine() }
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
            .brandScreen()
        }
    }
}


// MARK: - Вспомогательные строки

struct AcceptanceRow: View {
    let title: String
    var body: some View {
        HStack(spacing: 10) {
            Image(systemName: "circle")
                .font(.system(size: 10, weight: .bold))
                .foregroundColor(BrandColor.sky)
                .frame(width: 22, height: 22)
                .background(Circle().fill(BrandColor.mistDeep))
            Text(title)
                .font(.subheadline)
                .foregroundColor(BrandColor.inkSoft)
            Spacer(minLength: 0)
        }
    }
}

/// Обёртка для контента карточек списка.
struct Box<Content: View>: View {
    let content: Content
    init(@ViewBuilder content: () -> Content) { self.content = content() }
    var body: some View { content }
}
