import SwiftUI

struct ContentView: View {
    var body: some View {
        TabView {
            CalculatorsView()
                .tabItem {
                    Label("Расчеты", systemImage: "function")
                }
                .tag(0)

            DiagnosticsView()
                .tabItem {
                    Label("Диагностика", systemImage: "waveform.path.ecg")
                }
                .tag(1)

            EquipmentView()
                .tabItem {
                    Label("Установки", systemImage: "wrench.and.screwdriver.fill")
                }
                .tag(2)

            JournalView()
                .tabItem {
                    Label("Журнал", systemImage: "doc.text.fill")
                }
                .tag(3)

            FeedbackView()
                .tabItem {
                    Label("Связь", systemImage: "bubble.left.and.bubble.right.fill")
                }
                .tag(4)
        }
        .tint(BrandColor.indigo)
        .preferredColorScheme(.light)
    }
}
