import SwiftUI
import Shared

struct DiagnosticsView: View {
    @StateObject private var vm = DiagnosticsViewModel()
    @State private var selectedSection = 0

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 14) {
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
            .navigationTitle("Диагностика")
            .navigationBarTitleDisplayMode(.large)
        }
    }

    // MARK: - ХАРГ анализатор

    struct HargAnalyzerBlock: View {
        @ObservedObject var vm: DiagnosticsViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "Экспресс-диагностика по газам",
                              subtitle: "Концентрации растворенных газов, ppm")

                NumberField("Водород (H₂)", value: $vm.h2, suffix: "ppm")
                NumberField("Метан (CH₄)", value: $vm.ch4, suffix: "ppm")
                NumberField("Ацетилен (C₂H₂)", value: $vm.c2h2, suffix: "ppm")
                NumberField("Этилен (C₂H₄)", value: $vm.c2h4, suffix: "ppm")
                NumberField("Этан (C₂H₆)", value: $vm.c2h6, suffix: "ppm")

                HairLine()

                Button("Рассчитать") { vm.calculateHarg() }
                    .buttonStyle(.borderedProminent)
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

            SectionCard {
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

                HairLine()

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
                return (.red, .red.opacity(0.15), "exclamationmark.triangle.fill")
            } else if severity.contains("ВНИМАНИЕ") || severity.contains("УМЕРЕН") {
                return (.orange, .orange.opacity(0.15), "warninglight.fill")
            } else {
                return (.green, .green.opacity(0.15), "checkmark.circle.fill")
            }
        }
    }

    // MARK: - ГОСТ справочник

    struct GostReferenceBlock: View {
        @ObservedObject var vm: DiagnosticsViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "Предельные значения показателей",
                              subtitle: "РД 34.45-51.300-97")

                Text("Допустимые значения качества масла по классам напряжения.")
                    .font(.caption)
                    .foregroundColor(.secondary)

                ForEach(vm.gostStandards, id: \.voltageClass) { row in
                    GostRow(row: row)
                }
            }
        }
    }

    struct GostRow: View {
        let row: GostStandardRow

        var body: some View {
            SectionCard {
                HStack(alignment: .center, spacing: 12) {
                    Text(row.voltageClass)
                        .font(.headline)
                        .fontWeight(.bold)
                    Spacer()
                    Text("класс")
                        .font(.caption2)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(.blue)
                        .cornerRadius(8)
                }

                HairLine()

                SpecRow(label: "Пробивное напряжение, не менее", value: "\(row.breakdownVoltageKvMin) кВ")
                SpecRow(label: "Влагосодержание, не более", value: "\(row.moisturePpmMax) ppm")
                SpecRow(label: "Газосодержание, не более", value: "\(row.gasContentPercentMax) %")
                SpecRow(label: "Кислотное число, не более", value: "\(row.acidNumberMax) мг КОН/г")
                SpecRow(label: "tg δ при 90 °C, не более", value: "\(row.tanDelta90CMax) %")
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

    struct NumberField: View {
        let label: String
        @Binding var value: String
        let suffix: String?

        init(_ label: String, value: Binding<String>, suffix: String? = nil) {
            self.label = label
            self._value = value
            self.suffix = suffix
        }

        var body: some View {
            VStack(alignment: .leading, spacing: 4) {
                Text(label).font(.caption).foregroundColor(.secondary)
                HStack {
                    TextField("", text: $value)
                        .keyboardType(.decimalPad)
                        .textFieldStyle(.roundedBorder)
                    if let suffix { Text(suffix).font(.caption).foregroundColor(.secondary) }
                }
            }
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

    struct HairLine: View {
        var body: some View { Divider().padding(.vertical, 6) }
    }
}