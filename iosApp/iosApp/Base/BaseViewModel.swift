//
//  BaseViewModel.swift
//  iosApp
//
//  Created by Kwabena Berko on 25/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import Combine
import CombineSchedulers

class BaseViewModel<T>: ObservableObject {
    @Published public private(set) var state: T
    var scheduler: AnySchedulerOf<DispatchQueue>
    var cancellables: Set<AnyCancellable> = Set()
    
    init(
        initialState: T,
        scheduler: AnySchedulerOf<DispatchQueue> = .main
    ){
        self.state = initialState
        self.scheduler = scheduler
    }
    
    deinit {
        for cancellable in cancellables {
            cancellable.cancel()
        }
    }
    
    internal func setState(newState: T){
        self.scheduler.schedule {
            self.state = newState
        }
    }
    
    internal func getState() -> T {
        return state
    }
}
