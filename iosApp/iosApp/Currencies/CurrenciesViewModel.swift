//
//  CurrenciesViewModel.swift
//  iosApp
//
//  Created by Kwabena Berko on 24/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared
import OrderedCollections
import Combine
import KMPNativeCoroutinesCombine

final class CurrenciesViewModel: BaseViewModel<CurrenciesViewModel.State> {
    private var getCurrencies: GetCurrencies
    private let filterQueryPublisher = CurrentValueSubject<String, Never>("")
    
    init(getCurrencies: GetCurrencies){
        self.getCurrencies = getCurrencies
        super.init(initialState: .idle)
        subscribeToCurrencies()
    }
    
    
    func filterCurrencies(query: String) {
        filterQueryPublisher.send(query)
    }
    
    private func subscribeToCurrencies(){
        filterQueryPublisher
            .debounce(for: .milliseconds(300), scheduler: DispatchQueue.main)
            .map {[weak self] query in
                createPublisher(for: self!.getCurrencies.invokeNative(filter: query))
            }
            .switchToLatest()
            .removeDuplicates()
            .sink(
                receiveCompletion: {_ in},
                receiveValue: {[weak self] currencies in
                    let groupedCurrencies = OrderedDictionary(
                        grouping: currencies,
                        by: {$0.name.first!}
                    )
                    
                    self?.setState(newState: .content(currencies: groupedCurrencies))
                }
            ).store(in: &cancellables)
    }
    
    enum State: Equatable {
        case idle
        case content(currencies: OrderedDictionary<Character, [Currency]>)
    }
}
