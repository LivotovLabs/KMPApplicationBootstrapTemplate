import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        PlatformDIKt.loadKoinSwiftModules()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}