//
//  KeypadView.swift
//  iosApp
//
//  Created by Kwabena Berko on 27/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI

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
    @StateObject private var viewModel = KeyPadViewModel()
    
    var body: some View {
        KeyPadContentView(
            state: viewModel.state,
            onAppend: viewModel.add,
            onUndo: viewModel.pop,
            onBackClick: {
                navigator.popBackStack()
            },
            onDoneClick: { _ in
                navigator.popBackStack()
            }
        )
        .toolbar(.hidden)
        .colorTheme(redColorTheme)
    }
}

private struct KeyPadContentView: View {
    @Environment(\.verticalSizeClass) var verticalSizeClass
    @Environment(\.colorTheme) var theme
    
    var state: KeyPadViewModel.State
    var onAppend: (Character) -> Void = {_ in }
    var onUndo: () -> Void = {}
    var onBackClick: () -> Void = {}
    var onDoneClick: (Double) -> Void = {_ in }
    
    var body: some View {
        let scale = UIScreen.main.scale
        let shouldAdjustSize = verticalSizeClass == .regular && scale <= 2.0
        
        return ZStack {
            theme.background.ignoresSafeArea()
            switch state {
            case .idle: EmptyView()
            case .content(let amount, let isValid):
                VStack {
                    
                    TapToDeleteView(onClick: onUndo)
                    
                    AmountTextFieldView(
                        amount: amount,
                        fontSize: shouldAdjustSize ? 78 : 88
                    )
                    
                    ForEach(keys, id: \.self){ row in
                        HStack(alignment: .center, spacing: 20) {
                            ForEach(row, id: \.self) { key in
                                if key == done {
                                    DoneKeyButtonView(
                                        isEnabled: isValid,
                                        size: shouldAdjustSize ? 58 : 68
                                    ){
                                        if(isValid){
                                            onDoneClick(Double(amount)!)
                                        }
                                    }
                                } else {
                                    TextKeyButtonView(
                                        text: key,
                                        size: shouldAdjustSize ? 58 : 68
                                    ){
                                        onAppend(key.first!)
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
        Group {
            KeyPadContentView(
                state: .content(
                    amount: "2000",
                    isValid: true
                )
            )
            KeyPadContentView(
                state: .content(
                    amount: "",
                    isValid: false
                )
            )
        }
    }
}

