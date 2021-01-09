import SwiftUI
import shared

struct UserView: View {
    
    let user: User
    
    init(user: User = User(id: 99, name: "Unknown")) {
        self.user = user
    }
    
    var body: some View {
        Text(user.name)
            .font(/*@START_MENU_TOKEN@*/.title/*@END_MENU_TOKEN@*/)
    }
}

struct UserView_Previews: PreviewProvider {
    static var previews: some View {
        UserView()
    }
}
