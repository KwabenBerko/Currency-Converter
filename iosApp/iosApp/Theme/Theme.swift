//
//  ThemeManager.swift
//  iosApp
//
//  Created by Kwabena Berko on 26/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct ColorTheme {
    var background: Color
    var primary: Color
    var onPrimary: Color
    var secondary: Color
}

let redColorTheme = ColorTheme (
    background: Colors.redPrimary,
    primary: Colors.redPrimary,
    onPrimary: Colors.redOnPrimary,
    secondary: Colors.redSecondary
)

let whiteColorTheme = ColorTheme (
    background: Colors.whitePrimary,
    primary: Colors.whitePrimary,
    onPrimary: Colors.whiteOnPrimary,
    secondary: Colors.whiteSecondary
)

private struct ThemeKey: EnvironmentKey {
    static let defaultValue = redColorTheme
}

extension EnvironmentValues {
    var colorTheme: ColorTheme {
        get { self[ThemeKey.self] }
        set { self[ThemeKey.self] = newValue }
    }
}

extension View {
    func colorTheme(_ theme: ColorTheme) -> some View {
        environment(\.colorTheme, theme)
    }
}

