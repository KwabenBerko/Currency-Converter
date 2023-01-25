import SwiftUI
import KMMViewModelSwiftUI
import KMPNativeCoroutinesCombine
import shared

extension Container {
    static let shared = ContainerFactory().makeContainer()
}

enum Destination: Hashable {
    case converter
    case sync
}

final class Navigator: ObservableObject {
    @Published var stack = NavigationPath()
    
    func navigate(_ destination: Destination) {
        stack.append(destination)
    }
    
    func popBackStack() {
        stack.removeLast()
    }
}

@main
struct iOSApp: App {
    @StateObject private var navigator = Navigator()
    
    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $navigator.stack) {
                ConverterView()
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
