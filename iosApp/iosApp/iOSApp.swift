import SwiftUI
import shared

extension Container {
    static let shared = ContainerFactory().makeContainer()
}

enum Destination: Hashable {
    case converter
    case sync
    case keypad
    case currencies
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
                        case .currencies: CurrenciesView()
                        case .keypad: KeyPadView()
                        }
                    }
            }
            .environmentObject(navigator)
        }
    }
}
