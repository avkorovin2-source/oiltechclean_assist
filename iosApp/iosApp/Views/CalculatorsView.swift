import SwiftUI
import Shared

struct CalculatorsView: View {
    @StateObject private var vm = CalculatorsViewModel()

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 14) {
                    Picker("Расчет", selection: $vm.selectedTab) {
                        Text("Циклы").tag(0)
                        Text("Сорбент").tag(1)
                        Text("Вакуум").tag(2)
                        Text("tg δ").tag(3)
                        Text("Точка росы").tag(4)
                        Text("Вспышка").tag(5)
                    }
                    .pickerStyle(.segmented)
                    .padding(.horizontal)

                    switch vm.selectedTab {
                    case 0: UvsmCard(vm: vm)
                    case 1: SorbentCard(vm: vm)
                    case 2: VacuumCard(vm: vm)
                    case 3: TanDeltaCard(vm: vm)
                    case 4: DewPointCard(vm: vm)
                    default: FlashPointCard(vm: vm)
                    }
                }
                .padding(.vertical, 8)
            }
            .navigationTitle("Расчеты")
            .navigationBarTitleDisplayMode(.large)
        }
    }

    // MARK: - Циклы и время обработки (УВСМ)

    struct UvsmCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "Циклы и время обработки",
                              subtitle: "УВСМ: вакуумная сушка и дегазация")

                Picker("Тип трансформатора", selection: $vm.uvsmSelectedTransformerLabel) {
                    ForEach(vm.transformerOptions, id: \.self) { Text($0) }
                }
                .pickerStyle(.menu)
                .onChange(of: vm.uvsmSelectedTransformerLabel) { _, newValue in
                    vm.selectTransformer(by: newValue)
                }

                NumberField("Объем масла в баке", value: $vm.uvsmVolume, suffix: "м³")
                NumberField("Начальное влагосодержание", value: $vm.uvsmMStart, suffix: "ppm")
                NumberField("Целевое влагосодержание", value: $vm.uvsmMTarget, suffix: "ppm")
                NumberField("Производительность установки", value: $vm.uvsmCapacity, suffix: "м³/ч")

                HairLine()

                Button("Рассчитать") { vm.calculateUvsm() }
                    .buttonStyle(.borderedProminent)
                    .frame(maxWidth: .infinity)

                HStack(spacing: 10) {
                    ResultTile(label: "Циклов", value: vm.uvsmCycles, unit: nil)
                    ResultTile(label: "Время", value: vm.uvsmHours, unit: nil)
                }
                SpecRow(label: "Суммарный объем прокачки", value: vm.uvsmTotalVolume)
            }
        }
    }

    // MARK: - Сорбент

    struct SorbentCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "Масса сорбента",
                              subtitle: "Расчет адсорбента для регенерации масла")

                Toggle("Только термовакуумная сушка (цеолит)", isOn: $vm.sorbentZeoliteOnly)
                NumberField("Масса масла", value: $vm.sorbentOilMass, suffix: "тонн")
                NumberField("Кислотное число ДО", value: $vm.sorbentInitialAcid, suffix: "мг КОН/г")
                NumberField("Кислотное число ПОСЛЕ", value: $vm.sorbentTargetAcid, suffix: "мг КОН/г")

                HairLine()

                Button("Рассчитать") { vm.calculateSorbent() }
                    .buttonStyle(.borderedProminent)
                    .frame(maxWidth: .infinity)

                ResultTile(label: "Сорбента", value: vm.sorbentMass, unit: nil)
            }
        }
    }

    // MARK: - Вакуум

    struct VacuumCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "Время вакуумирования",
                              subtitle: "Расчет времени накачки вакуума")

                NumberField("Объем бака", value: $vm.vacuumTankVolume, suffix: "м³")
                NumberField("Производительность насоса", value: $vm.vacuumPumpSpeed, suffix: "м³/ч")
                NumberField("Целевое давление", value: $vm.vacuumTargetPressure, suffix: "Па")

                HairLine()

                Button("Рассчитать") { vm.calculateVacuum() }
                    .buttonStyle(.borderedProminent)
                    .frame(maxWidth: .infinity)

                HStack(spacing: 10) {
                    ResultTile(label: "Минут", value: vm.vacuumMinutes, unit: nil)
                    ResultTile(label: "Часов", value: vm.vacuumHours, unit: nil)
                }
            }
        }
    }

    // MARK: - tg δ

    struct TanDeltaCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "Температурная коррекция tg δ",
                              subtitle: "Приведение к 90 °C")

                NumberField("Измеренный tg δ", value: $vm.tanMeasured, suffix: "%")
                NumberField("Температура измерения", value: $vm.tempMeasured, suffix: "°C")
                NumberField("Привести к температуре", value: $vm.tempTarget, suffix: "°C")

                HairLine()

                ResultTile(label: "Приведенный tg δ", value: vm.tanConverted, unit: nil)
            }
        }
    }

    // MARK: - Точка росы

    struct DewPointCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "Точка росы",
                              subtitle: "Связь влагосодержания газа и точки росы")

                Picker("Режим", selection: $vm.dewMode) {
                    Text("Влага → точка росы").tag(0)
                    Text("Точка росы → влага").tag(1)
                }
                .pickerStyle(.segmented)
                .padding(.horizontal)

                if vm.dewMode == 0 {
                    NumberField("Влагосодержание газа", value: $vm.dewMoisturePpm, suffix: "ppm")
                    NumberField("Давление в системе", value: $vm.dewPressurePa, suffix: "Па")
                } else {
                    NumberField("Точка росы", value: $vm.dewPointC, suffix: "°C")
                    NumberField("Давление в системе", value: $vm.dewPressurePa, suffix: "Па")
                }

                HairLine()

                ResultTile(label: vm.dewMode == 0 ? "Точка росы" : "Влагосодержание газа",
                           value: vm.dewResult, unit: nil)

                SpecRow(label: "Ориентир для вакуумной сушки", value: "точка росы ≤ −40 °C")
            }
        }
    }

    // MARK: - Вспышка

    struct FlashPointCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "Температура вспышки",
                              subtitle: "Контроль накопления легких фракций")

                Picker("Класс напряжения", selection: $vm.flashVoltageClassIndex) {
                    Text("до 35 кВ").tag(0)
                    Text("110 кВ и выше").tag(1)
                }
                .pickerStyle(.segmented)
                .padding(.horizontal)

                NumberField("Вспышка свежего масла (паспорт)", value: $vm.flashInitial, suffix: "°C")
                NumberField("Доля легких фракций", value: $vm.flashLightFraction, suffix: "%")

                HairLine()

                ResultTile(label: "Расчетная вспышка", value: vm.flashEstimated, unit: nil)
                SpecRow(label: "Норматив для класса", value: vm.flashLimit)
                SpecRow(label: "Оценка",
                        value: vm.flashPassing ? "соответствует" : "ниже норматива")
                    .foregroundColor(vm.flashPassing ? .green : .red)
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
        var body: some View {
            VStack(alignment: .leading, spacing: 4) {
                Text(label).font(.caption).foregroundColor(.secondary)
                HStack {
                    TextField("", text: $value).keyboardType(.decimalPad).textFieldStyle(.roundedBorder)
                    if let suffix { Text(suffix).font(.caption).foregroundColor(.secondary) }
                }
            }
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

    struct HairLine: View {
        var body: some View { Divider().padding(.vertical, 6) }
    }
}

// MARK: - Заглушки остальных экранов (реализуются на следующих этапах)

struct DiagnosticsView: View {
    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                Image(systemName: "microscope")
                    .font(.system(size: 60))
                    .foregroundColor(.blue.opacity(0.5))
                Text("Диагностика").font(.title2).fontWeight(.bold)
                Text("ХАРГ-диагностика и нормы ГОСТ / РД")
                    .font(.subheadline).foregroundColor(.secondary)
                Text("Экран реализуется на этапе 2")
                    .font(.footnote).foregroundColor(.tertiary)
            }
            .padding()
            .navigationTitle("Диагностика")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

struct EquipmentView: View {
    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                Image(systemName: "cpu")
                    .font(.system(size: 60))
                    .foregroundColor(.blue.opacity(0.5))
                Text("Установки").font(.title2).fontWeight(.bold)
                Text("Каталог установок ОИЛТЕХКЛИН и чек-лист приемки")
                    .font(.subheadline).foregroundColor(.secondary)
                Text("Экран реализуется на этапе 3")
                    .font(.footnote).foregroundColor(.tertiary)
            }
            .padding()
            .navigationTitle("Установки")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

struct JournalView: View {
    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                Image(systemName: "doc.text")
                    .font(.system(size: 60))
                    .foregroundColor(.blue.opacity(0.5))
                Text("Журнал").font(.title2).fontWeight(.bold)
                Text("Полевой журнал замеров и протоколов")
                    .font(.subheadline).foregroundColor(.secondary)
                Text("Экран реализуется на этапе 4 (SwiftData)")
                    .font(.footnote).foregroundColor(.tertiary)
            }
            .padding()
            .navigationTitle("Журнал")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}