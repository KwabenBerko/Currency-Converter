//
//  KeypadView.swift
//  iosApp
//
//  Created by Kwabena Berko on 27/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared
import KMMViewModelSwiftUI
import KMMViewModelCore


private let dot = "."
private let done = "DONE"
private let keys = [
    ["1", "2", "3"],
    ["4", "5", "6"],
    ["7", "8", "9"],
    [dot, "0", done]
]
struct KeyPadView: View {
    @EnvironmentObject private var navigator: Navigator
    @ObservedViewModel private var converterViewModel: ConverterViewModel
    @StateViewModel private var viewModel = KeypadViewModel()
    private var conversionMode: ConversionMode
    
    init(
        conversionMode: ConversionMode,
        converterViewModel: ObservableViewModel<ConverterViewModel>.Projection
    ){
        self.conversionMode = conversionMode
        self._converterViewModel = ObservedViewModel(converterViewModel)
    }
    
    var body: some View {
        KeyPadContentView(
            state: viewModel.stateNativeValue,
            onAppend: viewModel.append,
            onRemoveLast: viewModel.removeLast,
            onBackClick: {
                navigator.stack.removeLast()
            },
            onDoneClick: { amount in
                switch conversionMode {
                case .firstToSecond:
                    converterViewModel.convertFirstMoney(amount: amount)
                case .secondToFirst:
                    converterViewModel.convertSecondMoney(amount: amount)
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

private struct KeyPadContentView: View {
    @Environment(\.verticalSizeClass) var verticalSizeClass
    @Environment(\.colorTheme) var theme
    
    var state: KeypadViewModel.State
    var onAppend: (String) -> Void = {_ in }
    var onRemoveLast: () -> Void = {}
    var onBackClick: () -> Void = {}
    var onDoneClick: (Double) -> Void = {_ in }
    
    var body: some View {
        let scale = UIScreen.main.scale
        let shouldAdjustSize = false
        
        return ZStack {
            theme.background.ignoresSafeArea()
            VStack {
                
                TapToDeleteView(onClick: onRemoveLast)
                
                AmountTextFieldView(
                    amount: state.text,
                    fontSize: shouldAdjustSize ? 78 : 88
                )
                
                ForEach(keys, id: \.self){ row in
                    HStack(alignment: .center, spacing: 20) {
                        ForEach(row, id: \.self) { key in
                            if key == done {
                                DoneKeyButtonView(
                                    isEnabled: state.isValid,
                                    size: shouldAdjustSize ? 58 : 78
                                ){
                                    if(state.isValid){
                                        onDoneClick(Double(state.text)!)
                                    }
                                }
                            } else {
                                TextKeyButtonView(
                                    text: key,
                                    size: shouldAdjustSize ? 58 : 78
                                ){
                                    onAppend(key)
                                }
                            }
                        }
                    }
                    .padding(.vertical, 8)
                }
                
                Spacer()
                
                Button(action: onBackClick) {
                    Image(Icons.chevronDown)
                        .foregroundColor(theme.onPrimary)
                        .font(.system(size: shouldAdjustSize ? 30 : 40))
                }
                
                Spacer()
            }
            .padding()
        }
    }
    
    private struct TapToDeleteView: View {
        @Environment(\.colorTheme) var theme
        var onClick: () -> Void
        
        var body: some View {
            HStack {
                Button(action: onClick){
                    Text("tap to delete")
                        .font(.labelMedium)
                        .foregroundColor(theme.secondary)
                        .padding()
                }
            }
        }
    }
    
    private struct AmountTextFieldView: View {
        @Environment(\.colorTheme) var theme
        var amount: String
        var fontSize: CGFloat
        
        var body: some View {
            HStack {
                Text(amount.isEmpty ? " " : amount)
                    .lineLimit(1)
                    .font(.appFont(size: fontSize))
                    .foregroundColor(theme.onPrimary)
                    .accentColor(theme.secondary)
                
            }.background(theme.background)
        }
    }
    
    
    private struct KeyButtonView: View {
        var contentView: AnyView
        var backgroundColor: Color
        var size: CGFloat
        
        var body: some View {
            VStack {
                contentView
            }
            .frame(width:size, height: size)
            .background(Circle().fill(backgroundColor))
        }
    }
    
    private struct DoneKeyButtonView: View {
        @Environment(\.colorTheme) var theme
        var isEnabled: Bool
        var size: CGFloat = 68
        var onClick: () -> Void
        
        var body: some View {
            Button(action: onClick) {
                KeyButtonView(
                    contentView: AnyView(
                        Image(Icons.check)
                            .font(.system(size: 36))
                            .foregroundColor(
                                isEnabled ? theme.primary :
                                    theme.primary.opacity(0.5)
                            )
                    ),
                    backgroundColor: theme.onPrimary,
                    size: size
                )
            }
        }
    }
    
    private struct TextKeyButtonView: View {
        @Environment(\.colorTheme) var theme
        var text: String
        var size: CGFloat = 68
        var onClick: () -> Void
        
        var body: some View {
            Button(action: onClick) {
                KeyButtonView(
                    contentView: AnyView(
                        Text(text)
                            .font(.system(size: 26))
                            .foregroundColor(theme.onPrimary)
                    ),
                    backgroundColor: theme.secondary,
                    size: size
                )
            }
        }
    }
}

struct KeyPadContentView_Preview: PreviewProvider {
    static var previews: some View {
        KeyPadContentView(
            state: KeypadViewModel.companion.mockState
        )
    }
}

