//
//  SyncViewModel.swift
//  iosApp
//
//  Created by Kwabena Berko on 25/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared
import KMPNativeCoroutinesAsync

final class SyncViewModel: BaseViewModel<SyncViewModel.State> {
    private var sync: Sync
    
    init(sync: Sync){
        self.sync = sync
        super.init(initialState: .idle)
        
        startSync()
    }
    
    func startSync() {
        Task {
            setState(newState: .syncing)
            
            do {
                let isSuccess = try await asyncFunction(for: sync.invokeNative()).boolValue
                if(!isSuccess){
                    setState(newState: .syncError)
                } else {
                    setState(newState: .syncSuccess)
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
    
    enum State {
        case idle
        case syncing
        case syncError
        case syncSuccess
    }
}
