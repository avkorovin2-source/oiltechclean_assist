import SwiftUI
import Shared

struct CalculatorsView: View {
    @StateObject private var vm = CalculatorsViewModel()

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 14) {
                    BrandHeroHeader(
                        title: "Расчеты",
                        subtitle: "Инженерные калькуляторы",
                        icon: "function"
                    )

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
            .brandScreen()
            .toolbar(.hidden, for: .navigationBar)
        }
    }

    // MARK: - Циклы и время обработки (УВСМ)

    struct UvsmCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            BrandCard {
                BrandSectionHeader(title: "Циклы и время обработки",
                              subtitle: "УВСМ: вакуумная сушка и дегазация")

                Picker("Тип трансформатора", selection: $vm.uvsmSelectedTransformerLabel) {
                    ForEach(vm.transformerOptions, id: \.self) { Text($0) }
                }
                .pickerStyle(.menu)
                .onChange(of: vm.uvsmSelectedTransformerLabel) { _, newValue in
                    vm.selectTransformer(by: newValue)
                }

                BrandNumberField("Объем масла в баке", value: $vm.uvsmVolume, suffix: "м³")
                BrandNumberField("Начальное влагосодержание", value: $vm.uvsmMStart, suffix: "ppm")
                BrandNumberField("Целевое влагосодержание", value: $vm.uvsmMTarget, suffix: "ppm")
                BrandNumberField("Производительность установки", value: $vm.uvsmCapacity, suffix: "м³/ч")

                BrandHairLine()

                Button("Рассчитать") { vm.calculateUvsm() }
                    .buttonStyle(BrandPrimaryButtonStyle())
                    .frame(maxWidth: .infinity)

                HStack(spacing: 10) {
                    BrandResultTile(label: "Циклов", value: vm.uvsmCycles, unit: nil)
                    BrandResultTile(label: "Время", value: vm.uvsmHours, unit: nil)
                }
                BrandSpecRow(label: "Суммарный объем прокачки", value: vm.uvsmTotalVolume)
            }
        }
    }

    // MARK: - Сорбент

    struct SorbentCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            BrandCard {
                BrandSectionHeader(title: "Масса сорбента",
                              subtitle: "Расчет адсорбента для регенерации масла")

                Toggle("Только термовакуумная сушка (цеолит)", isOn: $vm.sorbentZeoliteOnly)
                BrandNumberField("Масса масла", value: $vm.sorbentOilMass, suffix: "тонн")
                BrandNumberField("Кислотное число ДО", value: $vm.sorbentInitialAcid, suffix: "мг КОН/г")
                BrandNumberField("Кислотное число ПОСЛЕ", value: $vm.sorbentTargetAcid, suffix: "мг КОН/г")

                BrandHairLine()

                Button("Рассчитать") { vm.calculateSorbent() }
                    .buttonStyle(BrandPrimaryButtonStyle())
                    .frame(maxWidth: .infinity)

                BrandResultTile(label: "Сорбента", value: vm.sorbentMass, unit: nil)
            }
        }
    }

    // MARK: - Вакуум

    struct VacuumCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            BrandCard {
                BrandSectionHeader(title: "Время вакуумирования",
                              subtitle: "Расчет времени накачки вакуума")

                BrandNumberField("Объем бака", value: $vm.vacuumTankVolume, suffix: "м³")
                BrandNumberField("Производительность насоса", value: $vm.vacuumPumpSpeed, suffix: "м³/ч")
                BrandNumberField("Целевое давление", value: $vm.vacuumTargetPressure, suffix: "Па")

                BrandHairLine()

                Button("Рассчитать") { vm.calculateVacuum() }
                    .buttonStyle(BrandPrimaryButtonStyle())
                    .frame(maxWidth: .infinity)

                HStack(spacing: 10) {
                    BrandResultTile(label: "Минут", value: vm.vacuumMinutes, unit: nil)
                    BrandResultTile(label: "Часов", value: vm.vacuumHours, unit: nil)
                }
            }
        }
    }

    // MARK: - tg δ

    struct TanDeltaCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            BrandCard {
                BrandSectionHeader(title: "Температурная коррекция tg δ",
                              subtitle: "Приведение к 90 °C")

                BrandNumberField("Измеренный tg δ", value: $vm.tanMeasured, suffix: "%")
                BrandNumberField("Температура измерения", value: $vm.tempMeasured, suffix: "°C")
                BrandNumberField("Привести к температуре", value: $vm.tempTarget, suffix: "°C")

                BrandHairLine()

                BrandResultTile(label: "Приведенный tg δ", value: vm.tanConverted, unit: nil)
            }
        }
    }

    // MARK: - Точка росы

    struct DewPointCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            BrandCard {
                BrandSectionHeader(title: "Точка росы",
                              subtitle: "Связь влагосодержания газа и точки росы")

                Picker("Режим", selection: $vm.dewMode) {
                    Text("Влага → точка росы").tag(0)
                    Text("Точка росы → влага").tag(1)
                }
                .pickerStyle(.segmented)
                .padding(.horizontal)

                if vm.dewMode == 0 {
                    BrandNumberField("Влагосодержание газа", value: $vm.dewMoisturePpm, suffix: "ppm")
                    BrandNumberField("Давление в системе", value: $vm.dewPressurePa, suffix: "Па")
                } else {
                    BrandNumberField("Точка росы", value: $vm.dewPointC, suffix: "°C")
                    BrandNumberField("Давление в системе", value: $vm.dewPressurePa, suffix: "Па")
                }

                BrandHairLine()

                BrandResultTile(label: vm.dewMode == 0 ? "Точка росы" : "Влагосодержание газа",
                           value: vm.dewResult, unit: nil)

                BrandSpecRow(label: "Ориентир для вакуумной сушки", value: "точка росы ≤ −40 °C")
            }
        }
    }

    // MARK: - Вспышка

    struct FlashPointCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            BrandCard {
                BrandSectionHeader(title: "Температура вспышки",
                              subtitle: "Контроль накопления легких фракций")

                Picker("Класс напряжения", selection: $vm.flashVoltageClassIndex) {
                    Text("до 35 кВ").tag(0)
                    Text("110 кВ и выше").tag(1)
                }
                .pickerStyle(.segmented)
                .padding(.horizontal)

                BrandNumberField("Вспышка свежего масла (паспорт)", value: $vm.flashInitial, suffix: "°C")
                BrandNumberField("Доля легких фракций", value: $vm.flashLightFraction, suffix: "%")

                BrandHairLine()

                BrandResultTile(label: "Расчетная вспышка", value: vm.flashEstimated, unit: nil)
                BrandSpecRow(label: "Норматив для класса", value: vm.flashLimit)
                BrandSpecRow(label: "Оценка",
                             value: vm.flashPassing ? "соответствует" : "ниже норматива",
                             valueColor: vm.flashPassing ? BrandColor.success : BrandColor.danger)
            }
        }
    }
}
