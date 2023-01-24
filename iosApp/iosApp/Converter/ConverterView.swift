//
//  ConverterView.swift
//  iosApp
//
//  Created by Kwabena Berko on 26/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import KMMViewModelSwiftUI
import shared

struct ConverterView: View {
    @EnvironmentObject private var navigator: Navigator
    @EnvironmentViewModel private var viewModel: ConverterViewModel
    
    var body: some View {
        ConverterContentView(
            state: viewModel.state as! ConverterViewModel.State,
            onFirstCurrencyClick: { currency in
                navigator.navigate(
                    .currencies(
                        selectedCurrency: currency,
                        conversionMode: .firstToSecond
                    )
                )
            },
            onFirstAmountClick: {
                navigator.navigate(.keypad(conversionMode: .firstToSecond))
            },
            onSecondCurrencyClick: { currency in
                navigator.navigate(
                    .currencies(
                        selectedCurrency: currency,
                        conversionMode: .secondToFirst
                    )
                )
            },
            onSecondAmountClick: {
                navigator.navigate(.keypad(conversionMode: .secondToFirst))
            },
            onSyncRequired: {
                navigator.navigate(.sync)
            }
        )
        .toolbar(.hidden)
    }
}

private struct ConverterContentView: View {
    @Environment(\.verticalSizeClass) var verticalSizeClass
    var state: ConverterViewModel.State
    var onFirstCurrencyClick: (Currency) -> Void = {_ in }
    var onFirstAmountClick: () -> Void = {}
    var onSecondCurrencyClick: (Currency) -> Void = {_ in }
    var onSecondAmountClick: () -> Void = {}
    var onSyncRequired: () -> Void = {}
    
    var body: some View {
        let scale = UIScreen.main.scale
        let shouldAdjustSize = verticalSizeClass == .regular && scale <= 2.0
        
        return ZStack {
            redColorTheme.background.ignoresSafeArea()
            switch state {
            case is ConverterViewModel.StateIdle: EmptyView()
            case is ConverterViewModel.StateRequiresSync: EmptyView()
            case let content as ConverterViewModel.StateContent:
                let firstMoneyItem = content.firstMoneyItem
                let secondMoneyItem = content.secondMoneyItem
                
                ZStack {
                    VStack {
                        VStack {
                            let currency = firstMoneyItem.money.currency
                            let formattedAmount = firstMoneyItem.formattedAmount
                            
                            CurrencyNameView(
                                name: currency.name,
                                textColor: redColorTheme.onPrimary,
                                backgroundColor: redColorTheme.background,
                                fontSize: shouldAdjustSize ? 22 : 24,
                                onClick: {
                                    onFirstCurrencyClick(currency)
                                }
                            )
                            
                            Spacer()
                            
                            CurrencyAmountView(
                                amount: formattedAmount,
                                amountFontSize: shouldAdjustSize ? 78 : 88,
                                symbol: currency.symbol,
                                symbolFontSize: shouldAdjustSize ? 20 : 24,
                                amountTextColor: redColorTheme.onPrimary,
                                symbolTextColor: redColorTheme.secondary,
                                backgroundColor: redColorTheme.background,
                                onClick: onFirstAmountClick
                            )
                            
                            Spacer()
                            
                            CurrencyCodeView(
                                code: currency.code,
                                textColor: redColorTheme.secondary,
                                backgroundColor: redColorTheme.background
                            )
                            .offset(y: -36)
                        }
                        .padding()
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .background(redColorTheme.background)
                        
                        
                        VStack {
                            let currency = secondMoneyItem.money.currency
                            let formattedAmount = secondMoneyItem.formattedAmount
                            
                            CurrencyCodeView(
                                code: currency.code,
                                textColor: whiteColorTheme.secondary,
                                backgroundColor: whiteColorTheme.background.opacity(1)
                            )
                            .offset(y: 36)
                            
                            Spacer()
                            
                            CurrencyAmountView(
                                amount: formattedAmount,
                                amountFontSize: shouldAdjustSize ? 78 : 88,
                                symbol: currency.symbol,
                                symbolFontSize: shouldAdjustSize ? 20 : 24,
                                amountTextColor: whiteColorTheme.onPrimary,
                                symbolTextColor: whiteColorTheme.secondary,
                                backgroundColor: whiteColorTheme.background,
                                onClick: onSecondAmountClick
                            )
                            
                            Spacer()
                            
                            CurrencyNameView(
                                name: currency.name,
                                textColor: whiteColorTheme.onPrimary,
                                backgroundColor: whiteColorTheme.background,
                                fontSize: shouldAdjustSize ? 22 : 24,
                                onClick:  {
                                    onSecondCurrencyClick(currency)
                                }
                            )
                            
                        }
                        .padding()
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .background(whiteColorTheme.background)
                        
                    }
                    
                    ConversionDirection(conversionMode: content.conversionMode)
                }
            default: EmptyView()
            }
        }
        .onChange(of: state){ currentState in
            if(currentState is ConverterViewModel.StateRequiresSync){
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

private struct ConversionDirection: View {
    var conversionMode: ConversionMode
    
    var body: some View {
        ZStack {
            let icon = conversionMode == ConversionMode.firstToSecond ? Icons.longArrowDown : Icons.longArrowUp
            
            Image(icon)
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

struct ConverterContentView_Preview: PreviewProvider {
    static var previews: some View {
        ConverterContentView(state: ConverterViewModel.StateIdle())
    }
}

