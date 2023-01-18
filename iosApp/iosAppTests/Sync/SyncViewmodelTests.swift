//
//  SyncViewmodelTests.swift
//  iosAppTests
//
//  Created by Kwabena Berko on 25/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import XCTest
@testable import iosApp
@testable import shared
@testable import CombineExpectations

class SyncViewModelTests: XCTestCase {
    private var sync: FakeSync!
    
    override func setUp() {
        sync = FakeSync()
        sync.result = false
    }
    
    override func tearDown() {
        sync = nil
    }
    
    
    func test_syncError_state_is_published_when_sync_is_not_successful() async throws {
        let sut = createViewModel()
        let recorder = sut.$state.record()
        
        XCTAssertEqual(SyncViewModel.State.idle, try wait(for: recorder.next(), timeout: 0.1))
        XCTAssertEqual(SyncViewModel.State.syncing, try wait(for: recorder.next(), timeout: 0.1))
        XCTAssertEqual(SyncViewModel.State.syncError, try wait(for: recorder.next(), timeout: 0.1))
    }
    
    func test_syncSuccess_state_is_published_when_sync_is_not_successful() async throws {
        sync.result = true
        let sut = createViewModel()
        let recorder = sut.$state.record()
                
        XCTAssertEqual(SyncViewModel.State.idle, try wait(for: recorder.next(), timeout: 0.1))
        XCTAssertEqual(SyncViewModel.State.syncing, try wait(for: recorder.next(), timeout: 0.1))
        XCTAssertEqual(SyncViewModel.State.syncSuccess, try wait(for: recorder.next(), timeout: 0.1))
    }
    
    private func createViewModel() -> SyncViewModel {
        return SyncViewModel(sync: sync)
    }
}
