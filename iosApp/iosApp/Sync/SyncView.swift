//
//  SyncView.swift
//  iosApp
//
//  Created by Kwabena Berko on 26/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import KMMViewModelSwiftUI
import shared

struct SyncView: View {
    @EnvironmentObject private var navigator: Navigator
    @StateViewModel private var viewModel = SyncViewModel(sync: Container.shared.sync)
    
    var body: some View {
        SyncContentView(
            state: viewModel.state as! SyncViewModel.State,
            onSyncCompleted: {
                navigator.stack.append(Destination.converter)
            },
            onRetryClick: viewModel.startSync
        )
        .toolbar(.hidden)
    }
}

private struct SyncContentView: View {
    var state: SyncViewModel.State
    var onSyncCompleted: () -> Void = {}
    var onRetryClick: () -> Void = {}
    
    var body: some View {
        ZStack {
            redColorTheme.background.ignoresSafeArea()
            
            switch state {
            case is SyncViewModel.StateIdle: EmptyView()
            case is SyncViewModel.StateSyncing: SyncingView()
            case is SyncViewModel.StateSyncSuccess: EmptyView()
            case is SyncViewModel.StateSyncError: ErrorView(onRetryClick: onRetryClick)
            default: EmptyView()
            }
        }
        .onChange(of: state){ currentState in
            if(currentState is SyncViewModel.StateSyncSuccess){
                onSyncCompleted()
            }
        }
    }
}

private struct SyncingView: View {
    var body: some View {
        ProgressView()
            .progressViewStyle(.circular)
            .tint(redColorTheme.onPrimary)
    }
}

private struct ErrorView: View {
    var onRetryClick: () -> Void
    
    var body: some View {
        VStack {
            Text("Uhh ho! Something went wrong! Please retry")
                .foregroundColor(redColorTheme.onPrimary)
                .font(.labelMedium)
            
            Button(action: onRetryClick) {
                Text("Retry")
                    .font(.labelSmall)
                    .fontWeight(.medium)
            }
            .padding(.horizontal, 10)
            .padding(.vertical, 8)
            .background(RoundedRectangle(cornerRadius: 4).fill(redColorTheme.onPrimary))
            .foregroundColor(redColorTheme.primary)
            .accentColor(redColorTheme.primary)
            
        }.padding(.horizontal, 40)
    }
}
