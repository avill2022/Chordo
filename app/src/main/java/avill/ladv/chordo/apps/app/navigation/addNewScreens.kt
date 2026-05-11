package avill.ladv.chordo.apps.app.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

//addUIScreens
//----------------------------------------------------------------
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
fun NavGraphBuilder.addNewScreens(
    navController: NavHostController
) {
    navigation(
        route = UiScreen.UiIndex.route,
        startDestination = UiScreen.First.route
    ) {
        composable(UiScreen.Second.route) {

        }
    }
}