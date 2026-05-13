package avill.ladv.chordo.apps.app.navigation

sealed class Chordo(val route: String) {
    object Main : Chordo("main")
    object Splash : Chordo("splash")
    object OnBoarding : Chordo("onboarding")
    object Permissions : Chordo("permissions")
    object List : Chordo("list")
    object Edit : Chordo("edit")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}

sealed class UiScreen(val route: String) {
    object UiIndex : UiScreen("ui/index")
    object First : UiScreen("ui/second")
    object Second : UiScreen("ui/second")
}

sealed class AuthScreen(val route: String) {
    object Root : AuthScreen("auth")
    object Login : AuthScreen("login")
    object Register : AuthScreen("register")
    object ForgotPassword : AuthScreen("forgot_password")
}

sealed class BottomBarScreen(val route: String, val title: String) {
    object Home : BottomBarScreen("home", "Home")
    object Search : BottomBarScreen("search", "Search")
    object Create : BottomBarScreen("create", "Create")
    object Notifications : BottomBarScreen("notifications", "Notifications")
    object Profile : BottomBarScreen("profile", "Profile")
}


