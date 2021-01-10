import SwiftUI
import shared

@main
struct Cache4k: App {
    
    let userCache = UserCacheKt.userCache
    
    var body: some Scene {
        
        WindowGroup {
            Form {
                SaveUserView(onClickSave: { user in userCache.put(key: user.id, value: user) } )
                LoadUserView(onClickLoad: { id in userCache.get(key: id) as! User? } )
                DeleteUserView(onClickDelete: { id in userCache.invalidate(key: id) } )
            }
        }
    }
}

struct SaveUserView: View {
    
    let onClickSave: (User) -> Void
    
    init(onClickSave: @escaping (User) -> Void) {
        self.onClickSave = onClickSave
    }
    
    @State var user: User? = nil
    
    @State var id: String = ""
    @State var name: String = ""
    
    func buildUser() -> Void {
        if (!id.isEmpty && !name.isEmpty) { user = User(id: id, name: name) }
    }
    
    var body: some View {
        Section(header: Text("Save user")) {
            Section { TextField("ID", text: $id).onChange(of: id) { _ in buildUser() } }
            Section { TextField("name", text: $name).onChange(of: name) { _ in buildUser() }  }
            Section { Button( "Save", action: { if let user = self.user { onClickSave(user) } } ) }
        }
    }
}

struct LoadUserView: View {
    
    let onClickLoad: (String) -> User?
    
    init(onClickLoad: @escaping (String) -> User?) {
        self.onClickLoad = onClickLoad
    }
    
    @State var user: User? = nil
    
    @State var initialLaunch: Bool = true
    
    @State var id: String = ""
    
    var body: some View {
        Section(header: Text("Load user")) {
            Section { TextField("ID", text: $id) }
            Section {
                Button(
                    "Load",
                    action: {
                        initialLaunch = false
                        user = onClickLoad(id)
                    }
                )
            }
            Section { if (!initialLaunch) { Text(user?.name ?? "Not found") } }
        }
    }
}

struct DeleteUserView: View {
    let onClickDelete: (String) -> Void
    
    init(onClickDelete: @escaping (String) -> Void) {
        self.onClickDelete = onClickDelete
    }
    
    @State var id: String = ""
    
    var body: some View {
        Section(header: Text("Delete user")) {
            Section { TextField("ID", text: $id) }
            Section { Button("Delete", action: { onClickDelete(id) }) }
        }
    }
}
