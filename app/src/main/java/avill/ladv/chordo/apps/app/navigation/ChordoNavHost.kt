package avill.ladv.chordo.apps.app.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
        startDestination = Chordo.Splash.route
    ) {
        composable(Chordo.Splash.route) {

        }
        composable(Chordo.OnBoarding.route) {

        }
        composable(Chordo.Permissions.route) {

        }
        //Components
        addNewScreens(
            navController = navController
        )
    }
}
