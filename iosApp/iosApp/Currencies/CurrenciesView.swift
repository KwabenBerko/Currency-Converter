//
//  CurrenciesView.swift
//  iosApp
//
//  Created by Kwabena Berko on 24/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import OrderedCollections
import shared

struct CurrenciesView: View {
    @EnvironmentObject private var navigator: Navigator
    @StateObject private var viewModel: CurrenciesViewModel
    
    init(){
        _viewModel = StateObject(
            wrappedValue: CurrenciesViewModel(
                getCurrencies: Container.shared.getCurrencies
            )
        )
    }
    
    var body: some View {
        CurrenciesContentView(
            state: viewModel.state,
            onBackClick: {
                navigator.popBackStack()
            },
            onFilterCurrencies: { query in
                viewModel.filterCurrencies(query: query)
            }
        )
        .toolbar(.hidden)
        .colorTheme(redColorTheme)
    }
}

private struct CurrenciesContentView: View {
    @Environment(\.colorTheme) var theme
    
    @State private var isSearchBarVisible: Bool = false
    var state: CurrenciesViewModel.State
    var onBackClick: () -> Void = {}
    var onFilterCurrencies: (String) -> Void = {_ in}
    
    var body: some View {
        ZStack {
            theme.background.ignoresSafeArea()
            switch state {
            case .idle:EmptyView()
            case .content(let groupedCurrencies):
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
                    .frame(height: 68)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    
                    CurrencyListView(groupedCurrencies: groupedCurrencies)
                        .padding(.horizontal, 20)
                }
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
                    .font(.system(size: 32, weight: .light))
                    .foregroundColor(theme.onPrimary)
            }
            
            Spacer()
            
            Button(action: onSearchClick){
                
                Image(Icons.search)
                    .font(.system(size: 32, weight: .light))
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
                .keyboardType(.webSearch)
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
    var groupedCurrencies: OrderedDictionary<Character, [Currency]>
    
    var body: some View {
        ScrollView {
            LazyVStack(pinnedViews: [.sectionHeaders] ) {
                ForEach(groupedCurrencies.elements, id: \.key){ character, currencies in
                    Section(
                        header: VStack {
                            CharacterHeaderView(character: character)
                            ListDividerView()
                        }
                    ){
                        ForEach(currencies, id: \.code){ currency in
                            CurrencyItemView(
                                currency: currency,
                                onClick: {
                                    print("\(currency.name) has been clicked.")
                                }
                            )
                            ListDividerView()
                        }
                    }
                }
            }.id(UUID())
        }.clipped()
    }
}

private struct CharacterHeaderView: View {
    @Environment(\.colorTheme) var theme
    var character: Character
    
    var body: some View {
        ZStack(alignment: .leading){
            theme.background
            Text(String(character))
                .foregroundColor(theme.onPrimary)
                .font(.labelLarge)
                .padding(12)
        }
    }
}

let emptyString: String = " "
private struct CurrencyItemView: View {
    @Environment(\.colorTheme) var theme
    var currency: Currency
    var onClick: () -> Void
    
    var body: some View {
        ZStack(alignment: .leading) {
            theme.background
            HStack {
                Text(currency.name)
                    .foregroundColor(theme.onPrimary)
                    .font(.labelLarge)
                + Text(emptyString) + Text(emptyString) + Text(currency.code)
                    .foregroundColor(theme.secondary)
                    .font(.labelLarge)
                
                Spacer()
                
                if(1 == 0){
                    Image(Icons.check)
                        .font(.system(size: 32))
                        .foregroundColor(theme.onPrimary)
                }
            }.onTapGesture(perform: onClick)
        }.padding(12)
    }
}

private struct ListDividerView: View {
    @Environment(\.colorTheme) var theme
    var body: some View {
        Divider().overlay(theme.secondary)
    }
}
