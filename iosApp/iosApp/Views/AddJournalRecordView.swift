import SwiftUI
import SwiftData
import Shared

struct AddJournalRecordView: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var vm: AddJournalRecordViewModel

    init(vm: JournalViewModel) {
        _vm = StateObject(wrappedValue: AddJournalRecordViewModel(context: ModelContext(try! ModelContainer(for: JournalRecord.self, AcceptanceCheck.self))))
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 14) {
                    SectionCard {
                        SectionHeader(title: "овый замер", subtitle: "олевой журнал")

                        LabeledTextField("бъект / подстанция", text: .substationName)
                        LabeledTextField("Трансформатор", text: .transformerName)
                        LabeledTextField("становка ТХ", text: .unitModel)
                        LabeledTextField("ата и время замера", text: .recordDate)
                        LabeledTextField("ласс напряжения", text: .voltageClass)

                        HairLine()

                        Button("Сохранить замер") { vm.save(); dismiss() }
                            .buttonStyle(.borderedProminent)
                            .disabled((vm.substationName.isEmpty || vm.transformerName.isEmpty))
                            .frame(maxWidth: .infinity)
                    }

                    SectionCard {
                        SectionHeader(title: "езультаты измерений", subtitle: "араметры до и после обработки")

                        HStack(spacing: 10) {
                            NumberField("лажность ", value: .moistureBefore, suffix: "ppm")
                            NumberField("лажность С", value: .moistureAfter, suffix: "ppm")
                        }
                        HStack(spacing: 10) {
                            NumberField("робив. ", value: .breakdownBefore, suffix: "к")
                            NumberField("робив. С", value: .breakdownAfter, suffix: "к")
                        }
                        HStack(spacing: 10) {
                            NumberField("ислота ", value: .acidNumberBefore, suffix: "мг /г")
                            NumberField("ислота С", value: .acidNumberAfter, suffix: "мг /г")
                        }
                        HStack(spacing: 10) {
                            NumberField("tgδ ", value: .tanDeltaBefore, suffix: "%")
                            NumberField("tgδ С", value: .tanDeltaAfter, suffix: "%")
                        }
                        HStack(spacing: 10) {
                            NumberField("Температура масла", value: .oilTemperatureC, suffix: "°C")
                            NumberField("аз С", value: .gasContentAfter, suffix: "%")
                        }
                        HStack(spacing: 10) {
                            NumberField("спышка С", value: .flashPointAfter, suffix: "°C")
                        }

                        HairLine()

                        Button("ценить по СТ / ") { vm.evaluateGost() }
                            .buttonStyle(.bordered)
                            .frame(maxWidth: .infinity)

                        if let report = vm.gostReport, vm.showingGostResult {
                            GostResultCard(report: report)
                        }
                    }

                    SectionCard {
                        SectionHeader(title: "римечания", subtitle: "Свободное описание замера")
                        LabeledTextField("римечания", text: .remarks)
                    }
                }
                .padding(.vertical, 8)
            }
            .navigationTitle("обавить замер")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("тмена") { dismiss() }
                }
            }
        }
    }

    // MARK: - омпоненты формы
    struct LabeledTextField: View {
        let label: String
        @Binding var text: String
        var body: some View {
            VStack(alignment: .leading, spacing: 4) {
                Text(label).font(.caption).foregroundColor(.secondary)
                TextField("", text: )
                    .textFieldStyle(.roundedBorder)
            }
        }
    }

    struct NumberField: View {
        let label: String
        @Binding var value: String
        let suffix: String?
        var body: some View {
            VStack(alignment: .leading, spacing: 4) {
                Text(label).font(.caption).foregroundColor(.secondary)
                HStack {
                    TextField("", text: )
                        .keyboardType(.decimalPad)
                        .textFieldStyle(.roundedBorder)
                    if let suffix { Text(suffix).font(.caption).foregroundColor(.secondary) }
                }
            }
        }
    }

    struct GostResultCard: View {
        let report: GostComplianceEvaluator.ComplianceReport
        var body: some View {
            SectionCard {
                SectionHeader(title: "аключение по СТ / ")
                Text(report.isCompliant ? "Соответствует нормам" : "е соответствует нормам")
                    .font(.headline)
                    .fontWeight(.bold)
                    .foregroundColor(report.isCompliant ? .green : .red)
                Text("\(report.passed.count) из \(report.evaluated.count) показателей пройдено")
                    .font(.caption)
                    .foregroundColor(.secondary)
                ForEach(report.failed, id: \.parameter) { check in
                    HStack {
                        Text(check.parameter)
                            .font(.caption)
                        Spacer()
                        Text("\(check.actual ?? 0, specifier: "%.2f") \(check.unit) (лимит \(check.limit) \(check.unit))")
                            .font(.caption)
                            .foregroundColor(.red)
                    }
                }
            }
        }
    }

    // MARK: - азовые UI-компоненты
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

    struct HairLine: View {
        var body: some View { Divider().padding(.vertical, 6) }
    }
}
