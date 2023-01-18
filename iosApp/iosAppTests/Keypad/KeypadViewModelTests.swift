//
//  KeypadViewModelTests.swift
//  iosAppTests
//
//  Created by Kwabena Berko on 28/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import XCTest
@testable import iosApp
@testable import shared
@testable import CombineExpectations

class KeypadViewModelTests: XCTestCase {

    func test_amount_is_published_when_character_is_added() throws {
        let sut = createViewModel()
        let recorder = sut.$state.record()

  
        XCTAssertEqual(KeyPadViewModel.State.idle, try wait(for: recorder.next(), timeout: 0))
        XCTAssertEqual(
            KeyPadViewModel.State.content(amount: "", isValid: false),
            try wait(for: recorder.next(), timeout: 0)
        )
        
        sut.add(value: "1")
        XCTAssertEqual(
            KeyPadViewModel.State.content(amount: "1", isValid: true),
            try wait(for: recorder.next(), timeout: 0)
        )
    }
    
    func test_amount_is_published_when_pop_operation_occurs() throws {
        let sut = createViewModel()
        let recorder = sut.$state.record()
        
        XCTAssertEqual(KeyPadViewModel.State.idle, try recorder.next().get())
        XCTAssertEqual(
            KeyPadViewModel.State.content(amount: "", isValid: false),
            try wait(for: recorder.next(), timeout: 0)
        )
        
        sut.add(value: "1")
        XCTAssertEqual(
            KeyPadViewModel.State.content(amount: "1", isValid: true),
            try wait(for: recorder.next(), timeout: 0)
        )
        
        sut.pop()
        XCTAssertEqual(
            KeyPadViewModel.State.content(amount: "", isValid: false),
            try wait(for: recorder.next(), timeout: 0)
        )
    }
    
    private func createViewModel() -> KeyPadViewModel {
        return KeyPadViewModel(scheduler: .immediate)
    }
}
