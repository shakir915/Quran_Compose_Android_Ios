import SwiftUI
import ComposeApp

@main
struct iOSApp: App {


    init() {


        ShareUtilKt.shareText = {text in
            let itemsToShare = [text]

                      // Create a UIActivityViewController with the items and excluded activities
                      let activityViewController = UIActivityViewController(activityItems: itemsToShare, applicationActivities: nil)

                      // Present the activity view controller
                      if let
           presentingViewController = UIApplication.shared.windows.first?.rootViewController {
                          presentingViewController.present(activityViewController, animated: true, completion: nil)
                      }
        }

        
      
        
        
    }


    var body: some Scene {
        WindowGroup {
            ContentView() .ignoresSafeArea(edges: .all)
                                                 .ignoresSafeArea(.keyboard)
        }
    }
}
