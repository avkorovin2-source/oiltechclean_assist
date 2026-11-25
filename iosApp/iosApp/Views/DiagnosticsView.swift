import SwiftUI
import Shared

struct DiagnosticsView: View {
    @StateObject private var vm = DiagnosticsViewModel()
    @State private var selectedSection = 0

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 14) {
                    BrandHeroHeader(
                        title: "Диагностика",
                        subtitle: "ХАРГ и нормы ГОСТ",
                        icon: "waveform.path.ecg"
                    )

                    Picker("Раздел", selection: $selectedSection) {
                        Text("ХАРГ диагностика").tag(0)
                        Text("Нормы ГОСТ / РД").tag(1)
                    }
                    .pickerStyle(.segmented)
                    .padding(.horizontal)

                    if selectedSection == 0 {
                        HargAnalyzerBlock(vm: vm)
                    } else {
                        GostReferenceBlock(vm: vm)
                    }
                }
                .padding(.vertical, 8)
            }
            .brandScreen()
            .toolbar(.hidden, for: .navigationBar)
        }
    }

    // MARK: - ХАРГ анализатор

    struct HargAnalyzerBlock: View {
        @ObservedObject var vm: DiagnosticsViewModel

        var body: some View {
            BrandCard {
                BrandSectionHeader(title: "Экспресс-диагностика по газам",
                              subtitle: "Концентрации растворенных газов, ppm")

                BrandNumberField("Водород (H₂)", value: $vm.h2, suffix: "ppm")
                BrandNumberField("Метан (CH₄)", value: $vm.ch4, suffix: "ppm")
                BrandNumberField("Ацетилен (C₂H₂)", value: $vm.c2h2, suffix: "ppm")
                BrandNumberField("Этилен (C₂H₄)", value: $vm.c2h4, suffix: "ppm")
                BrandNumberField("Этан (C₂H₆)", value: $vm.c2h6, suffix: "ppm")
                BrandNumberField("Окись углерода (CO)", value: $vm.co, suffix: "ppm")
                BrandNumberField("Диоксид углерода (CO₂)", value: $vm.co2, suffix: "ppm")

                Picker("Класс напряжения", selection: $vm.voltageClassSelection) {
                    Text("Класс не указан").tag("Класс не указан")
                    Text("До 35 кВ").tag("До 35 кВ")
                    Text("110–150 кВ").tag("110–150 кВ")
                    Text("220–500 кВ").tag("220–500 кВ")
                    Text("750 кВ").tag("750 кВ")
                }
                .pickerStyle(.segmented)
                .padding(.horizontal)

                BrandHairLine()

                Button("Рассчитать") { vm.calculateHarg() }
                    .buttonStyle(BrandPrimaryButtonStyle())
                    .frame(maxWidth: .infinity)

                if let diagnosis = vm.diagnosis {
                    DiagnosisResultCard(diagnosis: diagnosis)
                }
            }
        }
    }

    struct DiagnosisResultCard: View {
        let diagnosis: HargDiagnosis

        var body: some View {
            let severity = diagnosis.severity.uppercased()
            let (accent, soft, icon) = severityConfig(severity)

            BrandCard {
                HStack(alignment: .center, spacing: 12) {
                    Image(systemName: icon)
                        .font(.system(size: 24))
                        .foregroundColor(accent)
                        .frame(width: 44, height: 44)
                        .background(soft)
                        .cornerRadius(14)

                    VStack(alignment: .leading, spacing: 4) {
                        Text(diagnosis.severity)
                            .font(.caption)
                            .fontWeight(.bold)
                            .foregroundColor(accent)
                        Text(diagnosis.faultTitle)
                            .font(.title3)
                            .fontWeight(.bold)
                    }
                }

                Text(diagnosis.explanation)
                    .font(.body)
                    .foregroundColor(.secondary)

                BrandHairLine()

                VStack(alignment: .leading, spacing: 4) {
                    Text("РЕКОМЕНДАЦИЯ")
                        .font(.caption2)
                        .fontWeight(.bold)
                        .foregroundColor(accent)
                    Text(diagnosis.recommendedAction)
                        .font(.body)
                }
                .padding(14)
                .background(soft)
                .cornerRadius(14)
            }
        }

        private func severityConfig(_ severity: String) -> (Color, Color, String) {
            if severity.contains("КРИТИЧ") || severity.contains("ВЫСОК") {
                return (BrandColor.danger, BrandColor.dangerSoft, "exclamationmark.triangle.fill")
            } else if severity.contains("ВНИМАНИЕ") || severity.contains("УМЕРЕН") {
                return (BrandColor.warning, BrandColor.warningSoft, "exclamationmark.triangle.fill")
            } else {
                return (BrandColor.success, BrandColor.successSoft, "checkmark.circle.fill")
            }
        }
    }

    // MARK: - ГОСТ справочник

    struct GostReferenceBlock: View {
        @ObservedObject var vm: DiagnosticsViewModel

        var body: some View {
            BrandCard {
                BrandSectionHeader(title: "Масло в эксплуатации",
                              subtitle: "Предельные значения по РД 34.45-51.300-97")

                Text("Нормы для трансформаторного масла, находящегося в работе на оборудовании.")
                    .font(.caption)
                    .foregroundColor(.secondary)

                ForEach(vm.gostStandardsInOperation, id: \.voltageClass) { row in
                    GostRow(row: row)
                }
            }

            BrandCard {
                BrandSectionHeader(title: "Свежее масло при приёмке",
                              subtitle: "Предельные значения для входного контроля (ГОСТ 982-80)")

                Text("Нормы для свежего масла перед заливкой в оборудование. Не смешивать с нормами эксплуатации.")
                    .font(.caption)
                    .foregroundColor(.secondary)

                ForEach(vm.gostStandardsForFreshOil, id: \.voltageClass) { row in
                    GostRow(row: row)
                }
            }

            Text("Важно: это разные категории масла. При одних и тех же показателях протокол (масло в эксплуатации) и чек-лист приёмки (свежее масло) могут выносить разный вердикт — сверяйтесь со строкой соответствующей категории.")
                .font(.caption)
                .foregroundColor(.secondary)
                .padding(.horizontal)
        }
    }

    struct GostRow: View {
        let row: GostStandardRow

        var body: some View {
            BrandCard {
                HStack(alignment: .center, spacing: 12) {
                    Text(row.voltageClass)
                        .font(.headline)
                        .fontWeight(.bold)
                    Spacer()
                    Text(row.category)
                        .font(.caption2)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(BrandColor.indigo)
                        .cornerRadius(8)
                }

                BrandHairLine()

                BrandSpecRow(label: "Пробивное напряжение, не менее", value: "\(row.breakdownVoltageKvMin) кВ")
                BrandSpecRow(label: "Влагосодержание, не более", value: "\(row.moisturePpmMax) ppm")
                BrandSpecRow(label: "Газосодержание, не более", value: "\(row.gasContentPercentMax) %")
                BrandSpecRow(label: "Кислотное число, не более", value: "\(row.acidNumberMax) мг КОН/г")
                BrandSpecRow(label: "tg δ при 90 °C, не более", value: "\(row.tanDelta90CMax) %")
            }
        }
    }
}
