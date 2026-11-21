import SwiftUI
import SwiftData

@main
struct OilTechApp: App {
    init() {
        BrandAppearance.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
        .modelContainer(SwiftDataProvider.shared.container)
    }
}
