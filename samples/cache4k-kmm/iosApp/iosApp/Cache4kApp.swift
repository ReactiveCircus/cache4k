import SwiftUI
import shared

@main
struct Cache4k: App {
    
    let userCache = UserCacheKt.userCache
    
    init() {
        userCache.put(key: 1, value: User(id: 1, name: "Javier"))
    }
    
    var body: some Scene {
        
        let user: User = userCache.get(key: 1) as? User ?? User(id: 99, name: "Unknown")
        
        
        WindowGroup {
            UserView(user: user)
        }
    }
}
