import SwiftUI
import KMMViewModelSwiftUI
import shared

extension Container {
    static let shared = ContainerFactory().makeContainer()
}

enum Destination: Hashable {
    case converter
    case sync
    case keypad(conversionMode: ConversionMode)
    case currencies(selectedCurrency: Currency, conversionMode: ConversionMode)
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
    @StateViewModel private var viewModel = ConverterViewModel(
        hasCompletedInitialSync: Container.shared.hasCompletedInitialSync,
        getDefaultCurrencies: Container.shared.getDefaultCurrencies,
        convertMoney: Container.shared.convertMoney
    )
    
    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $navigator.stack) {
                ConverterView()
                    .navigationDestination(for: Destination.self){ destination in
                        switch destination {
                        case .converter: ConverterView()
                        case .sync: SyncView()
                        case .currencies(let selectedCurrency, let conversionMode):
                            CurrenciesView(
                                selectedCurrencyCode: selectedCurrency.code,
                                conversionMode: conversionMode
                            )
                        case .keypad(let conversionMode):
                            KeyPadView(conversionMode: conversionMode)
                        }
                    }
            }
            .environmentViewModel($viewModel)
            .environmentObject(navigator)
        }
    }
}
