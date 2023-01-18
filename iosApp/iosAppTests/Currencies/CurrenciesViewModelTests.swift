//
//  CurrenciesViewModelTests.swift
//  iosAppTests
//
//  Created by Kwabena Berko on 24/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import XCTest
@testable import OrderedCollections
@testable import CombineExpectations
@testable import iosApp
@testable import shared

class CurrenciesViewModelTests: XCTestCase {
    private let usd = CurrencyFactory.shared.makeDollarCurrency()
    private let eur = CurrencyFactory.shared.makeEuroCurrency()
    private let ghs = CurrencyFactory.shared.makeCediCurrency()
    private var getCurrencies: FakeGetCurrencies!
    
    override func setUp() {
        getCurrencies = FakeGetCurrencies()
    }
    
    override func tearDown() {
        getCurrencies = nil
    }
    
    func test_content_state_is_published_when_currencies_are_loaded() throws {
        getCurrencies.result = BuildersKt_.flowOf(value: [eur, ghs, usd])
        let sut = createViewModel()
        let recorder = sut.$state.record()
        
        XCTAssertEqual(CurrenciesViewModel.State.idle, try wait(for: recorder.next(), timeout: 0.1))
        XCTAssertEqual(
            CurrenciesViewModel.State.content(
                currencies: [
                    "E": [eur],
                    "G": [ghs],
                    "U": [usd]
                ]
            ),
            try wait(for: recorder.next(), timeout: 0.4)
        )
    }
    
    private func createViewModel() -> CurrenciesViewModel {
        return CurrenciesViewModel(getCurrencies: getCurrencies)
    }
}

