@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package avill.ladv.chordo.apps.app

import android.content.res.Configuration
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import avill.ladv.chordo.apps.app.navigation.NavHostMain

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NamePreviewDark() {
    AppName(
        viewModel = hiltViewModel() // Assuming MainViewModel has a default constructor
    )
}
@Preview
@Composable
fun NamePreview() {
    AppName(
        viewModel = hiltViewModel() // Assuming MainViewModel has a default constructor
    )
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AppName(viewModel: AppNameViewModel) {
    val context = LocalContext.current

    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHostMain(Modifier, navController) {

    }
}