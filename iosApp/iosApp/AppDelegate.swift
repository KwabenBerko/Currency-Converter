//
//  AppDelegate.swift
//  iosApp
//
//  Created by Kwabena Berko on 30/01/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import UIKit
import Combine
import KMPNativeCoroutinesCombine
import BackgroundTasks
import shared

private let syncTaskIdentifier = "com.kwabenaberko.sync"

extension Container {
    static let shared = ContainerFactory().makeContainer()
}


class AppDelegate: NSObject, UIApplicationDelegate {
    private var cancellables = Set<AnyCancellable>()
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        
        self.registerSyncTask()
        
        createPublisher(for: Container.shared.hasCompletedInitialSync.invokeNative())
            .sink(
                receiveCompletion: {_ in },
                receiveValue: { hasCompleted in
                    if(hasCompleted.boolValue){
                        self.scheduleSyncTask()
                    }
                }
            )
            .store(in: &self.cancellables)
        
        return true
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        let container = Container.shared
        
        for cancellable in cancellables {
            cancellable.cancel()
        }
    }
    
    private func registerSyncTask() {
        BGTaskScheduler.shared.register(forTaskWithIdentifier: syncTaskIdentifier, using: nil){ [self] task in
            self.executeSyncTask(task: task)
        }
    }
    
    private func executeSyncTask(task: BGTask){
        task.expirationHandler = {
            task.setTaskCompleted(success: false)
        }
        
        print("Task Run At \(Date())")
        createFuture(for: Container.shared.sync.invokeNative())
            .sink(
                receiveCompletion: {_ in },
                receiveValue: {result in
                    if(result.boolValue){
                        task.setTaskCompleted(success: true)
                        self.scheduleSyncTask()
                    } else {
                        task.setTaskCompleted(success: false)
                    }
                }
            )
            .store(in: &self.cancellables)
    }
    
    private func scheduleSyncTask() {
        BGTaskScheduler.shared.getPendingTaskRequests { requests in
            var isEnqueued = false
            for request in requests {
                if request.identifier == syncTaskIdentifier{
                    isEnqueued = true
                    break
                }
            }

            if isEnqueued {
                print("A Task with \(syncTaskIdentifier) identifier is in the queue . No need to submit a new one")
            } else {
                print("No \(syncTaskIdentifier) task in queue. Lets add one.")
                let request = BGProcessingTaskRequest(identifier: syncTaskIdentifier)
                request.requiresNetworkConnectivity = true
                request.requiresExternalPower = false
                request.earliestBeginDate = Date(timeIntervalSinceNow: 10 * 60)
                do {
                    try BGTaskScheduler.shared.submit(request)
                    print("Submitting To Sync Scheduler at \(Date())")
                } catch {
                    print("Could not schedule app refresh: \(error)")
                }
            }
        }
    }
    
}
