import SwiftUI
import SwiftData
import Shared

struct AddJournalRecordView: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var vm: AddJournalRecordViewModel

    init(vm: JournalViewModel) {
        // Переиспользуем контекст из главного ViewModel через новый экземпляр с тем же контейнером
        // Для простоты создаём свой контейнер — в реальном проекте стоит вынести ModelContainer в стейт приложения
        _vm = StateObject(wrappedValue: AddJournalRecordViewModel(context: ModelContext(try! ModelContainer(for: JournalRecord.self, AcceptanceCheck.self))))
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 14) {
                    SectionCard {
                        SectionHeader(title: "Новый замер", subtitle: "Полевой журнал")

                        LabeledTextField("Объект / подстанция", text: $vm.substationName)
                        LabeledTextField("Трансформатор", text: $vm.transformerName)
                        LabeledTextField("Установка ОИЛТЕХКЛИН", text: $vm.unitModel)
                        LabeledTextField("Дата и время замера", text: $vm.recordDate)
                        LabeledTextField("Класс напряжения", text: $vm.voltageClass)

                        HairLine()

                        Button("Сохранить замер") { vm.save(); dismiss() }
                            .buttonStyle(.borderedProminent)
                            .disabled(vm.substationName.isEmpty || vm.transformerName.isEmpty)
                            .frame(maxWidth: .infinity)
                    }

                    SectionCard {
                        SectionHeader(title: "Результаты измерений", subtitle: "Параметры до и после обработки")

                        HStack(spacing: 10) {
                            NumberField("Влажность ДО", value: $vm.moistureBefore, suffix: "ppm")
                            NumberField("Влажность ПОСЛЕ", value: $vm.moistureAfter, suffix: "ppm")
                        }
                        HStack(spacing: 10) {
                            NumberField("Пробив. ДО", value: $vm.breakdownBefore, suffix: "кВ")
                            NumberField("Пробив. ПОСЛЕ", value: $vm.breakdownAfter, suffix: "кВ")
                        }
                        HStack(spacing: 10) {
                            NumberField("Кислота ДО", value: $vm.acidNumberBefore, suffix: "мг КОН/г")
                            NumberField("Кислота ПОСЛЕ", value: $vm.acidNumberAfter, suffix: "мг КОН/г")
                        }
                        HStack(spacing: 10) {
                            NumberField("tgδ ДО", value: $vm.tanDeltaBefore, suffix: "%")
                            NumberField("tgδ ПОСЛЕ", value: $vm.tanDeltaAfter, suffix: "%")
                        }
                        HStack(spacing: 10) {
                            NumberField("Темп. масла", value: $vm.oilTemperatureC, suffix: "°C")
                            NumberField("Газ ПОСЛЕ", value: $vm.gasContentAfter, suffix: "%")
                        }
                        HStack(spacing: 10) {
                            NumberField("Вспышка ПОСЛЕ", value: $vm.flashPointAfter, suffix: "°C")
                        }

                        HairLine()

                        Button("Оценить по ГОСТ / РД") { vm.evaluateGost() }
                            .buttonStyle(.bordered)
                            .frame(maxWidth: .infinity)

                        if let report = vm.gostReport, vm.showingGostResult {
                            GostResultCard(report: report)
                        }
                    }

                    SectionCard {
                        SectionHeader(title: "Примечания", subtitle: "Свободное описание замера")
                        LabeledTextField("Примечания", text: $vm.remarks)
                    }
                }
                .padding(.vertical, 8)
            }
        } // NavigationStack
    } // body
} // struct AddJournalRecordView

    // MARK: - Компоненты формы

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

    struct NumberField: View {
        let label: String
        @Binding var value: String
        let suffix: String?
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

    struct GostResultCard: View {
        let report: GostComplianceEvaluator.ComplianceReport

        var body: some View {
            SectionCard {
                SectionHeader(title: "Заключение по ГОСТ / РД")

                Text(report.isCompliant ? "Соответствует нормам" : "Не соответствует нормам")
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

    struct HairLine: View {
        var body: some View { Divider().padding(.vertical, 6) }
    }
            .navigationTitle("Добавить замер")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Отмена") { dismiss() }
                }
            }
        }
    }