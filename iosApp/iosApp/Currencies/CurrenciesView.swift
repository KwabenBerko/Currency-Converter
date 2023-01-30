//
//  CurrenciesView.swift
//  iosApp
//
//  Created by Kwabena Berko on 24/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import KMMViewModelSwiftUI
import KMMViewModelCore
import shared

struct CurrenciesView: View {
    @EnvironmentObject private var navigator: Navigator
    @ObservedViewModel private var converterViewModel: ConverterViewModel
    @StateViewModel private var viewModel: CurrenciesViewModel
    private var conversionMode: ConversionMode
    
    init(
        selectedCurrencyCode: String,
        conversionMode: ConversionMode,
        converterViewModel: ObservableViewModel<ConverterViewModel>.Projection
    ){
        self.conversionMode = conversionMode
        self._converterViewModel = ObservedViewModel(converterViewModel)
        self._viewModel = StateViewModel(
            wrappedValue: CurrenciesViewModel(
                selectedCurrencyCode: selectedCurrencyCode,
                getCurrencies: Container.shared.getCurrencies
            )
        )
    }
    
    
    var body: some View {
        CurrenciesContentView(
            state: viewModel.stateNativeValue,
            onBackClick: {
                navigator.stack.removeLast()
            },
            onFilterCurrencies: { query in
                viewModel.filterCurrencies(query: query)
            },
            onCurrencyClick: { currency in
                switch conversionMode {
                case .firstToSecond:
                    converterViewModel.convertFirstMoney(currency: currency)
                    break
                case .secondToFirst:
                    converterViewModel.convertSecondMoney(currency: currency)
                    break
                default:
                    break
                }
                navigator.stack.removeLast()
            }
        )
        .toolbar(.hidden)
        .colorTheme(
            conversionMode == ConversionMode.firstToSecond ? redColorTheme : whiteColorTheme
        )
    }
}

private struct CurrenciesContentView: View {
    @Environment(\.colorTheme) var theme
    
    @State private var isSearchBarVisible: Bool = false
    var state: CurrenciesViewModel.State
    var onBackClick: () -> Void = {}
    var onFilterCurrencies: (String) -> Void = {_ in}
    var onCurrencyClick: (Currency) -> Void = {_ in }
    
    var body: some View {
        ZStack {
            theme.background.ignoresSafeArea()
            switch state {
            case is CurrenciesViewModel.StateIdle:EmptyView()
            case let content as CurrenciesViewModel.StateContent:
                
                VStack {
                    VStack {
                        if isSearchBarVisible {
                            SearchBarView(
                                onQueryChange: { query in
                                    onFilterCurrencies(query)
                                }, onCloseClick: {
                                    withAnimation {
                                        isSearchBarVisible.toggle()
                                    }
                                }
                            ).transition(.asymmetric(
                                insertion: .move(edge: .trailing),
                                removal: .move(edge: .trailing))
                            )
                        }
                        else {
                            ToolbarView(
                                onBackClick: onBackClick,
                                onSearchClick: {
                                    withAnimation(.easeInOut) {
                                        isSearchBarVisible.toggle()
                                    }
                                }
                            )
                        }
                    }
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    .frame(height: 68)
                    
                    CurrencyListView(
                        groupedCurrencies: content.currencies,
                        selectedCurrency: content.selectedCurrency,
                        onCurrencyClick: onCurrencyClick
                    )
                    .padding(.horizontal, 20)
                }
            default: EmptyView()
            }
        }
    }
}

private struct ToolbarView: View {
    @Environment(\.colorTheme) var theme
    var onBackClick: () -> Void
    var onSearchClick: () -> Void
    
    
    var body: some View {
        HStack {
            Button(action: onBackClick){
                
                Image(Icons.longArrowLeft)
                    .font(.system(size: 36, weight: .light))
                    .foregroundColor(theme.onPrimary)
            }
            
            Spacer()
            
            Button(action: onSearchClick){
                
                Image(Icons.search)
                    .font(.system(size: 36, weight: .light))
                    .foregroundColor(theme.secondary)
            }
        }
    }
}

private struct SearchBarView: View {
    @Environment(\.colorTheme) var theme
    @FocusState private var isFocused: Bool
    @State private var query: String = ""
    var onQueryChange: (String) -> Void
    var onCloseClick: () -> Void
    
    var body: some View {
        
        ZStack(alignment: .trailing) {
            TextField("Search currencies...", text: $query)
                .onChange(of: query){ query in
                    onQueryChange(query)
                }
                .font(.labelMedium)
                .padding()
                .background(RoundedRectangle(cornerRadius: 8).fill(theme.secondary))
                .foregroundColor(theme.onPrimary)
                .accentColor(theme.onPrimary)
                .autocorrectionDisabled(true)
                .focused($isFocused)
                .onAppear {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.5){
                        isFocused = true
                    }
                }
            Button(action: {
                if !query.isEmpty {
                    query = ""
                } else {
                    onCloseClick()
                }
            }) {
                Image(Icons.close)
                    .font(.system(size: 24, weight: .light))
                    .foregroundColor(theme.onPrimary)
                    .padding()
            }
        }
    }
}

private struct CurrencyListView: View {
    var groupedCurrencies: [String: [Currency]]
    var selectedCurrency: Currency
    var onCurrencyClick: (Currency) ->  Void
    
    var body: some View {
        ScrollView {
            LazyVStack(pinnedViews: [.sectionHeaders] ) {
                ForEach(groupedCurrencies.keys.sorted(), id: \.self){ key in
                    let currencies = groupedCurrencies[key]!
                    
                    Section(
                        header: VStack {
                            CharacterHeaderView(header: key)
                            ListDividerView()
                        }
                    ){
                        ForEach(currencies, id: \.code){ currency in
                            CurrencyItemView(
                                currency: currency,
                                isSelected: currency == selectedCurrency,
                                onClick: {
                                    onCurrencyClick(currency)
                                }
                            )
                            ListDividerView()
                        }
                    }
                }
            }
            .animation(.default, value: groupedCurrencies)
        }
    }
}

private struct CharacterHeaderView: View {
    @Environment(\.colorTheme) private var theme
    var header: String
    
    var body: some View {
        ZStack(alignment: .leading){
            theme.background
            Text(header)
                .foregroundColor(theme.onPrimary)
                .font(.labelLarge)
                .padding(12)
        }
    }
}

let emptyString: String = " "
private struct CurrencyItemView: View {
    @Environment(\.colorTheme) private var theme
    var currency: Currency
    var isSelected: Bool
    var onClick: () -> Void
    
    var body: some View {
        Button(action: onClick){
            ZStack(alignment: .leading) {
                theme.background
                HStack {
                    (
                        Text(currency.name)
                            .foregroundColor(theme.onPrimary)
                            .font(.labelLarge)
                        + Text(emptyString + emptyString)
                        + Text(currency.code)
                            .foregroundColor(theme.secondary)
                            .font(.labelLarge)
                    )
                    .multilineTextAlignment(.leading)
                    
                    Spacer()
                    
                    if(isSelected){
                        Image(Icons.check)
                            .font(.system(size: 32))
                            .foregroundColor(theme.onPrimary)
                    }
                }
            }
            .padding(12)
        }
    }
}

private struct ListDividerView: View {
    @Environment(\.colorTheme) private var theme
    var body: some View {
        Divider().overlay(theme.secondary)
    }
}

struct CurrenciesContentView_Preview: PreviewProvider {
    static var previews: some View {
        CurrenciesContentView(state: CurrenciesViewModel.StateIdle())
    }
}
