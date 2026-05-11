package avill.ladv.chordo.apps.app.navigation

sealed class MainScreen(val route: String) {
    object Main : MainScreen("main")
    object Splash : MainScreen("splash")
    object OnBoarding : MainScreen("onboarding")
    object Permissions : MainScreen("permissions")

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

sealed class ProfileScreen(val route: String) {
    object Settings : ProfileScreen("settings")
    object About : ProfileScreen("about")
}

// Keep existing ones for compatibility if they are being used elsewhere, 
// but consolidate them or mark them as legacy if needed.
sealed class CompositionsScreen(val route: String) {
    object CompositionsIndex : CompositionsScreen("compositions/index")
}

sealed class TutorialsScreen(val route: String) {
    object TutorialIndex : TutorialsScreen("tutorial/index")
}

sealed class AnimationsScreen(val route: String) {
    object AnimationIndex : AnimationsScreen("animation/index")
}
