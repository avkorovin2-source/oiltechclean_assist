import SwiftUI

struct ContentView: View {
    var body: some View {
        TabView {
            CalculatorsView()
                .tabItem {
                    Image(systemName: "function")
                    Text("Расчеты")
                }
                .tag(0)

            DiagnosticsView()
                .tabItem {
                    Image(systemName: "microscope")
                    Text("Диагностика")
                }
                .tag(1)

            EquipmentView()
                .tabItem {
                    Image(systemName: "cpu")
                    Text("Установки")
                }
                .tag(2)

            JournalView()
                .tabItem {
                    Image(systemName: "doc.text")
                    Text("Журнал")
                }
                .tag(3)
        }
    }
}