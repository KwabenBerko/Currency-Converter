//
//  ConverterViewModelTests.swift
//  iosAppTests
//
//  Created by Kwabena Berko on 25/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import XCTest
@testable import iosApp
@testable import CombineExpectations
@testable import shared

class ConverterViewModelTests: XCTestCase {
    
    private let eur = CurrencyFactory.shared.makeEuroCurrency()
    private let ghs = CurrencyFactory.shared.makeCediCurrency()
    
    private var hasCompletedInitialSync: FakeHasCompletedInitialSync!
    private var getDefaultCurrencies: FakeGetDefaultCurrencies!
    private var convertMoney: FakeConvertMoney!
    
    override func setUp() {
        hasCompletedInitialSync = FakeHasCompletedInitialSync()
        getDefaultCurrencies = FakeGetDefaultCurrencies()
        convertMoney = FakeConvertMoney()
        
        hasCompletedInitialSync.result = true
        getDefaultCurrencies.result = DefaultCurrencies(base: ghs, target: ghs)
        convertMoney.result = Money(currency: ghs, amount: 1.0)
    }
    
    override func tearDown() {
        hasCompletedInitialSync = nil
        getDefaultCurrencies = nil
        convertMoney = nil
    }
    
    func test_requiresSync_state_is_published_when_converter_is_loaded() throws {
        hasCompletedInitialSync.result = false
        let sut = createViewModel()
        let recorder = sut.$state.record()
        
        XCTAssertEqual(ConverterViewModel.State.idle, try wait(for: recorder.next(), timeout: 0.1))
        XCTAssertEqual(
            ConverterViewModel.State.requireSync, try wait(for: recorder.next(), timeout: 0.1)
        )
    }
    
    func test_content_state_is_published_when_converter_is_loaded() throws {
        let sut = createViewModel()
        let recorder = sut.$state.record()
    
        XCTAssertEqual(ConverterViewModel.State.idle, try wait(for: recorder.next(), timeout: 0.1))
        XCTAssertEqual(
            ConverterViewModel.State.content, try wait(for: recorder.next(), timeout: 0.1)
        )
    }
    
    private func createViewModel() -> ConverterViewModel {
        return ConverterViewModel(
            hasCompletedInitialSync: hasCompletedInitialSync,
            getDefaultCurrencies: getDefaultCurrencies,
            convertMoney: convertMoney
        )
    }
}
