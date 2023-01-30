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

extension Container {
    static let shared = ContainerFactory().makeContainer()
}


class AppDelegate: NSObject, UIApplicationDelegate {
    private var cancellables = Set<AnyCancellable>()

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {

        registerBackgroundTask()

        createPublisher(for: Container.shared.hasCompletedInitialSync.invokeNative())
            .sink(
                receiveCompletion: {_ in },
                receiveValue: { hasCompleted in
                    if(hasCompleted.boolValue){
                        self.scheduleSync()
                    }
                }
            )
            .store(in: &self.cancellables)

        return true
    }

    func applicationWillTerminate(_ application: UIApplication) {
        for cancellable in cancellables {
            cancellable.cancel()
        }
    }

    private func registerBackgroundTask() {
        BGTaskScheduler.shared.register(forTaskWithIdentifier: "sync", using: nil){ [self] task in
            task.expirationHandler = {
                task.setTaskCompleted(success: false)
            }

            createFuture(for: Container.shared.sync.invokeNative())
                .sink(
                    receiveCompletion: {_ in },
                    receiveValue: {result in
                        if(result.boolValue){
                            task.setTaskCompleted(success: true)
                            self.scheduleSync()
                        } else {
                            task.setTaskCompleted(success: false)
                        }
                    }
                )
                .store(in: &self.cancellables)
        }
    }

    private func scheduleSync() {
        let request = BGAppRefreshTaskRequest(identifier: "sync")
        request.earliestBeginDate = Date(timeIntervalSinceNow: 1)
        try? BGTaskScheduler.shared.submit(request)
        print("Submitting To Sync Scheduler")
    }

}
