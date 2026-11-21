import SwiftUI
import Shared

struct CalculatorsView: View {
    @StateObject private var vm = CalculatorsViewModel()

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 14) {
                    Picker("Р Р°СЃС‡РµС‚", selection: $vm.selectedTab) {
                        Text("Р¦РёРєР»С‹").tag(0)
                        Text("РЎРѕСЂР±РµРЅС‚").tag(1)
                        Text("Р’Р°РєСѓСѓРј").tag(2)
                        Text("tg Оґ").tag(3)
                        Text("РўРѕС‡РєР° СЂРѕСЃС‹").tag(4)
                        Text("Р’СЃРїС‹С€РєР°").tag(5)
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
            .navigationTitle("Р Р°СЃС‡РµС‚С‹")
            .navigationBarTitleDisplayMode(.large)
        }
    }

    // MARK: - Р¦РёРєР»С‹ Рё РІСЂРµРјСЏ РѕР±СЂР°Р±РѕС‚РєРё (РЈР’РЎРњ)

    struct UvsmCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "Р¦РёРєР»С‹ Рё РІСЂРµРјСЏ РѕР±СЂР°Р±РѕС‚РєРё",
                              subtitle: "РЈР’РЎРњ: РІР°РєСѓСѓРјРЅР°СЏ СЃСѓС€РєР° Рё РґРµРіР°Р·Р°С†РёСЏ")

                Picker("РўРёРї С‚СЂР°РЅСЃС„РѕСЂРјР°С‚РѕСЂР°", selection: $vm.uvsmSelectedTransformerLabel) {
                    ForEach(vm.transformerOptions, id: \.self) { Text($0) }
                }
                .pickerStyle(.menu)
                .onChange(of: vm.uvsmSelectedTransformerLabel) { _, newValue in
                    vm.selectTransformer(by: newValue)
                }

                NumberField("РћР±СЉРµРј РјР°СЃР»Р° РІ Р±Р°РєРµ", value: $vm.uvsmVolume, suffix: "РјВі")
                NumberField("РќР°С‡Р°Р»СЊРЅРѕРµ РІР»Р°РіРѕСЃРѕРґРµСЂР¶Р°РЅРёРµ", value: $vm.uvsmMStart, suffix: "ppm")
                NumberField("Р¦РµР»РµРІРѕРµ РІР»Р°РіРѕСЃРѕРґРµСЂР¶Р°РЅРёРµ", value: $vm.uvsmMTarget, suffix: "ppm")
                NumberField("РџСЂРѕРёР·РІРѕРґРёС‚РµР»СЊРЅРѕСЃС‚СЊ СѓСЃС‚Р°РЅРѕРІРєРё", value: $vm.uvsmCapacity, suffix: "РјВі/С‡")

                HairLine()

                Button("Р Р°СЃСЃС‡РёС‚Р°С‚СЊ") { vm.calculateUvsm() }
                    .buttonStyle(.borderedProminent)
                    .frame(maxWidth: .infinity)

                HStack(spacing: 10) {
                    ResultTile(label: "Р¦РёРєР»РѕРІ", value: vm.uvsmCycles, unit: nil)
                    ResultTile(label: "Р’СЂРµРјСЏ", value: vm.uvsmHours, unit: nil)
                }
                SpecRow(label: "РЎСѓРјРјР°СЂРЅС‹Р№ РѕР±СЉРµРј РїСЂРѕРєР°С‡РєРё", value: vm.uvsmTotalVolume)
            }
        }
    }

    // MARK: - РЎРѕСЂР±РµРЅС‚

    struct SorbentCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "РњР°СЃСЃР° СЃРѕСЂР±РµРЅС‚Р°",
                              subtitle: "Р Р°СЃС‡РµС‚ Р°РґСЃРѕСЂР±РµРЅС‚Р° РґР»СЏ СЂРµРіРµРЅРµСЂР°С†РёРё РјР°СЃР»Р°")

                Toggle("РўРѕР»СЊРєРѕ С‚РµСЂРјРѕРІР°РєСѓСѓРјРЅР°СЏ СЃСѓС€РєР° (С†РµРѕР»РёС‚)", isOn: $vm.sorbentZeoliteOnly)
                NumberField("РњР°СЃСЃР° РјР°СЃР»Р°", value: $vm.sorbentOilMass, suffix: "С‚РѕРЅРЅ")
                NumberField("РљРёСЃР»РѕС‚РЅРѕРµ С‡РёСЃР»Рѕ Р”Рћ", value: $vm.sorbentInitialAcid, suffix: "РјРі РљРћРќ/Рі")
                NumberField("РљРёСЃР»РѕС‚РЅРѕРµ С‡РёСЃР»Рѕ РџРћРЎР›Р•", value: $vm.sorbentTargetAcid, suffix: "РјРі РљРћРќ/Рі")

                HairLine()

                Button("Р Р°СЃСЃС‡РёС‚Р°С‚СЊ") { vm.calculateSorbent() }
                    .buttonStyle(.borderedProminent)
                    .frame(maxWidth: .infinity)

                ResultTile(label: "РЎРѕСЂР±РµРЅС‚Р°", value: vm.sorbentMass, unit: nil)
            }
        }
    }

    // MARK: - Р’Р°РєСѓСѓРј

    struct VacuumCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "Р’СЂРµРјСЏ РІР°РєСѓСѓРјРёСЂРѕРІР°РЅРёСЏ",
                              subtitle: "Р Р°СЃС‡РµС‚ РІСЂРµРјРµРЅРё РЅР°РєР°С‡РєРё РІР°РєСѓСѓРјР°")

                NumberField("РћР±СЉРµРј Р±Р°РєР°", value: $vm.vacuumTankVolume, suffix: "РјВі")
                NumberField("РџСЂРѕРёР·РІРѕРґРёС‚РµР»СЊРЅРѕСЃС‚СЊ РЅР°СЃРѕСЃР°", value: $vm.vacuumPumpSpeed, suffix: "РјВі/С‡")
                NumberField("Р¦РµР»РµРІРѕРµ РґР°РІР»РµРЅРёРµ", value: $vm.vacuumTargetPressure, suffix: "РџР°")

                HairLine()

                Button("Р Р°СЃСЃС‡РёС‚Р°С‚СЊ") { vm.calculateVacuum() }
                    .buttonStyle(.borderedProminent)
                    .frame(maxWidth: .infinity)

                HStack(spacing: 10) {
                    ResultTile(label: "РњРёРЅСѓС‚", value: vm.vacuumMinutes, unit: nil)
                    ResultTile(label: "Р§Р°СЃРѕРІ", value: vm.vacuumHours, unit: nil)
                }
            }
        }
    }

    // MARK: - tg Оґ

    struct TanDeltaCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "РўРµРјРїРµСЂР°С‚СѓСЂРЅР°СЏ РєРѕСЂСЂРµРєС†РёСЏ tg Оґ",
                              subtitle: "РџСЂРёРІРµРґРµРЅРёРµ Рє 90 В°C")

                NumberField("РР·РјРµСЂРµРЅРЅС‹Р№ tg Оґ", value: $vm.tanMeasured, suffix: "%")
                NumberField("РўРµРјРїРµСЂР°С‚СѓСЂР° РёР·РјРµСЂРµРЅРёСЏ", value: $vm.tempMeasured, suffix: "В°C")
                NumberField("РџСЂРёРІРµСЃС‚Рё Рє С‚РµРјРїРµСЂР°С‚СѓСЂРµ", value: $vm.tempTarget, suffix: "В°C")

                HairLine()

                ResultTile(label: "РџСЂРёРІРµРґРµРЅРЅС‹Р№ tg Оґ", value: vm.tanConverted, unit: nil)
            }
        }
    }

    // MARK: - РўРѕС‡РєР° СЂРѕСЃС‹

    struct DewPointCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "РўРѕС‡РєР° СЂРѕСЃС‹",
                              subtitle: "РЎРІСЏР·СЊ РІР»Р°РіРѕСЃРѕРґРµСЂР¶Р°РЅРёСЏ РіР°Р·Р° Рё С‚РѕС‡РєРё СЂРѕСЃС‹")

                Picker("Р РµР¶РёРј", selection: $vm.dewMode) {
                    Text("Р’Р»Р°РіР° в†’ С‚РѕС‡РєР° СЂРѕСЃС‹").tag(0)
                    Text("РўРѕС‡РєР° СЂРѕСЃС‹ в†’ РІР»Р°РіР°").tag(1)
                }
                .pickerStyle(.segmented)
                .padding(.horizontal)

                if vm.dewMode == 0 {
                    NumberField("Р’Р»Р°РіРѕСЃРѕРґРµСЂР¶Р°РЅРёРµ РіР°Р·Р°", value: $vm.dewMoisturePpm, suffix: "ppm")
                    NumberField("Р”Р°РІР»РµРЅРёРµ РІ СЃРёСЃС‚РµРјРµ", value: $vm.dewPressurePa, suffix: "РџР°")
                } else {
                    NumberField("РўРѕС‡РєР° СЂРѕСЃС‹", value: $vm.dewPointC, suffix: "В°C")
                    NumberField("Р”Р°РІР»РµРЅРёРµ РІ СЃРёСЃС‚РµРјРµ", value: $vm.dewPressurePa, suffix: "РџР°")
                }

                HairLine()

                ResultTile(label: vm.dewMode == 0 ? "РўРѕС‡РєР° СЂРѕСЃС‹" : "Р’Р»Р°РіРѕСЃРѕРґРµСЂР¶Р°РЅРёРµ РіР°Р·Р°",
                           value: vm.dewResult, unit: nil)

                SpecRow(label: "РћСЂРёРµРЅС‚РёСЂ РґР»СЏ РІР°РєСѓСѓРјРЅРѕР№ СЃСѓС€РєРё", value: "С‚РѕС‡РєР° СЂРѕСЃС‹ в‰¤ в€’40 В°C")
            }
        }
    }

    // MARK: - Р’СЃРїС‹С€РєР°

    struct FlashPointCard: View {
        @ObservedObject var vm: CalculatorsViewModel

        var body: some View {
            SectionCard {
                SectionHeader(title: "РўРµРјРїРµСЂР°С‚СѓСЂР° РІСЃРїС‹С€РєРё",
                              subtitle: "РљРѕРЅС‚СЂРѕР»СЊ РЅР°РєРѕРїР»РµРЅРёСЏ Р»РµРіРєРёС… С„СЂР°РєС†РёР№")

                Picker("РљР»Р°СЃСЃ РЅР°РїСЂСЏР¶РµРЅРёСЏ", selection: $vm.flashVoltageClassIndex) {
                    Text("РґРѕ 35 РєР’").tag(0)
                    Text("110 РєР’ Рё РІС‹С€Рµ").tag(1)
                }
                .pickerStyle(.segmented)
                .padding(.horizontal)

                NumberField("Р’СЃРїС‹С€РєР° СЃРІРµР¶РµРіРѕ РјР°СЃР»Р° (РїР°СЃРїРѕСЂС‚)", value: $vm.flashInitial, suffix: "В°C")
                NumberField("Р”РѕР»СЏ Р»РµРіРєРёС… С„СЂР°РєС†РёР№", value: $vm.flashLightFraction, suffix: "%")

                HairLine()

                ResultTile(label: "Р Р°СЃС‡РµС‚РЅР°СЏ РІСЃРїС‹С€РєР°", value: vm.flashEstimated, unit: nil)
                SpecRow(label: "РќРѕСЂРјР°С‚РёРІ РґР»СЏ РєР»Р°СЃСЃР°", value: vm.flashLimit)
                SpecRow(label: "РћС†РµРЅРєР°",
                        value: vm.flashPassing ? "СЃРѕРѕС‚РІРµС‚СЃС‚РІСѓРµС‚" : "РЅРёР¶Рµ РЅРѕСЂРјР°С‚РёРІР°")
                    .foregroundColor(vm.flashPassing ? .green : .red)
            }
        }
    }

    // MARK: - Р‘Р°Р·РѕРІС‹Рµ UI-РєРѕРјРїРѕРЅРµРЅС‚С‹

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
