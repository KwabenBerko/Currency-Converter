//
//  Typography.swift
//  iosApp
//
//  Created by Kwabena Berko on 27/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI


extension Font {
    static func appFont(size: CGFloat) -> Font {
        return Font.custom("SF Regular", size: size)
    }
    
    static let labelLarge = appFont(size:20)
    static let labelMedium = appFont(size: 18)
    static let labelSmall = appFont(size: 14)
}
