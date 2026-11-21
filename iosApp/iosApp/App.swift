import SwiftUI
import SwiftData

@main
struct OilTechApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
        .modelContainer(SwiftDataProvider.shared.container)
    }
}
