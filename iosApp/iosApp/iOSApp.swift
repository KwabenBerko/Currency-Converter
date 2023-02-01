import SwiftUI
import shared

enum Destination: Hashable {
    case converter
    case sync
}

final class Navigator: ObservableObject {
    @Published var stack: NavigationPath
    
    init(_ stack: NavigationPath) {
        self.stack = stack
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) private var appDelegate
    @StateObject private var navigator = Navigator(.init([Destination.converter]))
    
    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $navigator.stack) {
                EmptyView()
                    .navigationDestination(for: Destination.self){ destination in
                        switch destination {
                        case .converter: ConverterView()
                        case .sync: SyncView()
                        }
                    }
            }
            .environmentObject(navigator)
        }
    }
}

