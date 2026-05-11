package avill.ladv.chordo.apps.app.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun NavHostMain(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    turnOnDarkMode: (Boolean) -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MainScreen.Splash.route
    ) {
        composable(MainScreen.Splash.route) {

        }
        composable(MainScreen.OnBoarding.route) {

        }
        composable(MainScreen.Permissions.route) {

        }
        //Components
        addNewScreens(
            navController = navController
        )
    }
}
