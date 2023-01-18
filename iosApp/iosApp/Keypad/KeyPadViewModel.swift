//
//  KeyPadViewModel.swift
//  iosApp
//
//  Created by Kwabena Berko on 28/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared
import Combine
import KMPNativeCoroutinesCombine
import CombineSchedulers

final class KeyPadViewModel: BaseViewModel<KeyPadViewModel.State> {
    private var amountEngine: AmountInputEngine
    
    init(scheduler: AnySchedulerOf<DispatchQueue> = .main){
        self.amountEngine = AmountInputEngine()
        super.init(initialState: .idle, scheduler: scheduler)
        subscribeToAmount()
    }
    
    func add(value: Character){
        amountEngine.add(character: value.utf16.first!)
    }
    
    func pop(){
        amountEngine.pop()
    }
    
    private func subscribeToAmount() {
        createPublisher(for: self.amountEngine.amountNative)
            .receive(on: scheduler)
            .sink(
                receiveCompletion: {_ in },
                receiveValue: {[weak self] value in
                    let newState = State.content(
                        amount: value.text,
                        isValid: value.isValid
                    )
                    self?.setState(newState: newState)
                }
            ).store(in: &cancellables)
    }
    
    enum State: Equatable {
        case idle
        case content(
            amount: String,
            isValid: Bool
        )
    }
}
