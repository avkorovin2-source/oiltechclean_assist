import SwiftUI
import SwiftData
import Shared

struct AddJournalRecordView: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.modelContext) private var context
    @StateObject private var vm = AddJournalRecordViewModel()

    let onSaved: () -> Void

    init(onSaved: @escaping () -> Void = {}) {
        self.onSaved = onSaved
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 14) {
                    BrandCard {
                        BrandSectionHeader(title: "Новый замер", subtitle: "Полевой журнал")

                        BrandLabeledTextField("Объект / подстанция", text: $vm.substationName)
                        BrandLabeledTextField("Трансформатор", text: $vm.transformerName)
                        BrandLabeledTextField("Установка ОИЛТЕХКЛИН", text: $vm.unitModel)
                        BrandLabeledTextField("Дата и время замера", text: $vm.recordDate)
                        BrandLabeledTextField("Класс напряжения", text: $vm.voltageClass)

                        BrandHairLine()

                        Button("Сохранить замер") {
                            vm.save()
                            onSaved()
                            dismiss()
                        }
                        .buttonStyle(BrandPrimaryButtonStyle())
                        .disabled(vm.substationName.isEmpty || vm.transformerName.isEmpty)
                        .frame(maxWidth: .infinity)
                    }

                    BrandCard {
                        BrandSectionHeader(title: "Результаты измерений",
                                      subtitle: "Параметры до и после обработки")

                        HStack(spacing: 10) {
                            BrandNumberField("Влажность ДО", value: $vm.moistureBefore, suffix: "ppm")
                            BrandNumberField("Влажность ПОСЛЕ", value: $vm.moistureAfter, suffix: "ppm")
                        }
                        HStack(spacing: 10) {
                            BrandNumberField("Пробив. ДО", value: $vm.breakdownBefore, suffix: "кВ")
                            BrandNumberField("Пробив. ПОСЛЕ", value: $vm.breakdownAfter, suffix: "кВ")
                        }
                        HStack(spacing: 10) {
                            BrandNumberField("Кислота ДО", value: $vm.acidNumberBefore, suffix: "мг КОН/г")
                            BrandNumberField("Кислота ПОСЛЕ", value: $vm.acidNumberAfter, suffix: "мг КОН/г")
                        }
                        HStack(spacing: 10) {
                            BrandNumberField("tgδ ДО", value: $vm.tanDeltaBefore, suffix: "%")
                            BrandNumberField("tgδ ПОСЛЕ", value: $vm.tanDeltaAfter, suffix: "%")
                        }
                        HStack(spacing: 10) {
                            BrandNumberField("Температура масла", value: $vm.oilTemperatureC, suffix: "°C")
                            BrandNumberField("Газосодержание ПОСЛЕ", value: $vm.gasContentAfter, suffix: "%")
                        }
                        HStack(spacing: 10) {
                            BrandNumberField("Вспышка ПОСЛЕ", value: $vm.flashPointAfter, suffix: "°C")
                        }

                        BrandHairLine()

                        Button("Оценить по ГОСТ / РД") { vm.evaluateGost() }
                            .buttonStyle(BrandSecondaryButtonStyle())
                            .frame(maxWidth: .infinity)

                        if let result = vm.gostResult, vm.showingGostResult {
                            GostResultCard(result: result)
                        }
                    }

                    BrandCard {
                        BrandSectionHeader(title: "Примечания", subtitle: "Свободное описание замера")
                        BrandLabeledTextField("Примечания", text: $vm.remarks)
                    }
                }
                .padding(.vertical, 8)
            }
            .navigationTitle("Добавить замер")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Отмена") { dismiss() }
                }
            }
            .brandScreen()
            .task { vm.attach(context) }
        }
    }

    // MARK: - Компоненты формы

    struct GostResultCard: View {
        let result: GostEvaluation

        var body: some View {
            BrandCard {
                BrandSectionHeader(title: "Заключение по ГОСТ / РД")

                Text(headline)
                    .font(.headline)
                    .fontWeight(.bold)
                    .foregroundColor(accent)

                if result.hasData {
                    Text(result.summary)
                        .font(.caption)
                        .foregroundColor(.secondary)

                    ForEach(result.failedLines, id: \.self) { line in
                        Text(line)
                            .font(.caption)
                            .foregroundColor(BrandColor.danger)
                    }
                } else {
                    Text("Недостаточно данных для оценки соответствия нормам.")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
        }

        private var headline: String {
            guard result.hasData else { return "Нет данных" }
            return result.isCompliant ? "Соответствует нормам" : "Не соответствует нормам"
        }

        private var accent: Color {
            guard result.hasData else { return BrandColor.slate }
            return result.isCompliant ? BrandColor.success : BrandColor.danger
        }
    }
}
