//
//  ConverterView.swift
//  iosApp
//
//  Created by Kwabena Berko on 26/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct ConverterView: View {
    @EnvironmentObject private var navigator: Navigator
    @StateObject private var viewModel = ConverterViewModel(
        hasCompletedInitialSync: Container.shared.hasCompletedInitialSync,
        getDefaultCurrencies: Container.shared.getDefaultCurrencies,
        convertMoney: Container.shared.convertMoney
    )
    
    var body: some View {
        ConverterContentView(
            state: viewModel.state,
            onFirstCurrencyClick: {
                navigator.navigate(.currencies)
            },
            onFirstAmountClick: {
                navigator.navigate(.keypad)
            },
            onSecondCurrencyClick: {
                navigator.navigate(.currencies)
            },
            onSecondAmountClick: {
                navigator.navigate(.keypad)
            },
            onSyncRequired: {
                navigator.navigate(.sync)
            }
        )
    }
}

private struct ConverterContentView: View {
    @Environment(\.verticalSizeClass) var verticalSizeClass
    var state: ConverterViewModel.State
    var onFirstCurrencyClick: () -> Void = {}
    var onFirstAmountClick: () -> Void = {}
    var onSecondCurrencyClick: () -> Void = {}
    var onSecondAmountClick: () -> Void = {}
    var onSyncRequired: () -> Void = {}
    
    var body: some View {
        let scale = UIScreen.main.scale
        let shouldAdjustSize = verticalSizeClass == .regular && scale <= 2.0
        
        return ZStack {
            redColorTheme.background.ignoresSafeArea()
            switch state {
            case .idle: EmptyView()
            case .requireSync: EmptyView()
            case .content:
                ZStack {
                    VStack {
                        VStack {
                            CurrencyNameView(
                                name: "United States Dollar",
                                textColor: redColorTheme.onPrimary,
                                backgroundColor: redColorTheme.background,
                                fontSize: shouldAdjustSize ? 22 : 24,
                                onClick: onFirstCurrencyClick
                            )
                            
                            Spacer()
                            
                            CurrencyAmountView(
                                amount: "500K",
                                amountFontSize: shouldAdjustSize ? 78 : 88,
                                symbol: "$",
                                symbolFontSize: shouldAdjustSize ? 20 : 24,
                                amountTextColor: redColorTheme.onPrimary,
                                symbolTextColor: redColorTheme.secondary,
                                backgroundColor: redColorTheme.background,
                                onClick: onFirstAmountClick
                            )
                            
                            Spacer()
                            
                            CurrencyCodeView(
                                code: "USD",
                                textColor: redColorTheme.secondary,
                                backgroundColor: redColorTheme.background
                            )
                            .offset(y: -36)
                        }
                        .padding()
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .background(redColorTheme.background)
                        
                        
                        VStack {
                            CurrencyCodeView(
                                code: "GHS",
                                textColor: whiteColorTheme.secondary,
                                backgroundColor: whiteColorTheme.background.opacity(1)
                            )
                            .offset(y: 36)
                            
                            Spacer()
                            
                            CurrencyAmountView(
                                amount: "1.5M",
                                amountFontSize: shouldAdjustSize ? 78 : 88,
                                symbol: "GHS",
                                symbolFontSize: shouldAdjustSize ? 20 : 24,
                                amountTextColor: whiteColorTheme.onPrimary,
                                symbolTextColor: whiteColorTheme.secondary,
                                backgroundColor: whiteColorTheme.background,
                                onClick: onSecondAmountClick
                            )
                            
                            Spacer()
                            
                            CurrencyNameView(
                                name: "Ghanaian Cedi",
                                textColor: whiteColorTheme.onPrimary,
                                backgroundColor: whiteColorTheme.background,
                                fontSize: shouldAdjustSize ? 22 : 24,
                                onClick: onSecondCurrencyClick
                            )
                            
                        }
                        .padding()
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .background(whiteColorTheme.background)
                        
                    }
                    
                    ZStack {
                        Image(Icons.longArrowDown)
                            .font(.system(size: 46))
                            .foregroundColor(Color.red)
                    }
                    .padding(20)
                    .background(Circle().fill(whiteColorTheme.background))
                    .overlay(
                        Circle()
                            .stroke(redColorTheme.primary, lineWidth: 6)
                    )
                }
            }
        }
        .onChange(of: state){ currentState in
            if(currentState == .requireSync){
                onSyncRequired()
            }
        }
    }
}

private struct CurrencyNameView: View {
    var name: String
    var textColor: Color
    var backgroundColor: Color
    var fontSize: CGFloat
    var onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            Text(name)
                .font(.appFont(size: fontSize))
                .foregroundColor(textColor)
        }
    }
}

private struct CurrencyAmountView: View {
    var amount: String
    var amountFontSize: CGFloat
    var symbol: String
    var symbolFontSize: CGFloat
    var amountTextColor: Color
    var symbolTextColor: Color
    var backgroundColor: Color
    var onClick: () -> Void
    
    var body: some View {
        ZStack {
            Button(action: onClick) {
                HStack {
                    Text(amount)
                        .font(.appFont(size: amountFontSize))
                        .foregroundColor(amountTextColor)
                    + Text(symbol)
                        .font(.appFont(size: symbolFontSize))
                        .foregroundColor(symbolTextColor)
                }
            }
        }
    }
}

private struct CurrencyCodeView: View {
    var code: String
    var textColor: Color
    var backgroundColor: Color
    
    var body: some View {
        Text(code)
            .font(.labelLarge)
            .foregroundColor(textColor)
    }
}

struct ConverterContentView_Preview: PreviewProvider {
    static var previews: some View {
        ConverterContentView(state: .content)
    }
}
