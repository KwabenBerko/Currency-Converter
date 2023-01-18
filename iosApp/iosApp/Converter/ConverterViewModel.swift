//
//  ConverterViewModel.swift
//  iosApp
//
//  Created by Kwabena Berko on 25/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared
import KMPNativeCoroutinesCombine

final class ConverterViewModel: BaseViewModel<ConverterViewModel.State> {
    private let formatter = CompactNumberFormatter()
    private var hasCompletedInitialSync: HasCompletedInitialSync
    private var getDefaultCurrencies: GetDefaultCurrencies
    private var convertMoney: ConvertMoney
    
    init(
        hasCompletedInitialSync: HasCompletedInitialSync,
        getDefaultCurrencies: GetDefaultCurrencies,
        convertMoney: ConvertMoney
    ){
        self.hasCompletedInitialSync = hasCompletedInitialSync
        self.getDefaultCurrencies = getDefaultCurrencies
        self.convertMoney = convertMoney
        super.init(initialState: .idle)
        
        loadConverter()
    }
    
    private func loadConverter(){
        createPublisher(for: hasCompletedInitialSync.invokeNative())
            .sink(
                receiveCompletion: { _ in },
                receiveValue: {[weak self] hasCompleted in
                    if(!hasCompleted.boolValue){
                        self?.setState(newState: .requireSync)
                    } else {
                        self?.setState(newState: .content)
                    }
                }
            )
            .store(in: &cancellables)
    }
    
    enum State: Equatable {
        case idle
        case requireSync
        case content
    }
}
