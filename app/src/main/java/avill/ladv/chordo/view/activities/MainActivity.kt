package avill.ladv.chordo.view.activities

// AudioHelper.kt
import android.Manifest
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import avill.ladv.chordo.apps.app.ChordoApp
import avill.ladv.chordo.apps.app.ChordoViewModel
import avill.ladv.chordo.apps.app.MainViewModel
import avill.ladv.chordo.apps.app.TempoViewModel
import avill.ladv.chordo.apps.app.TunerApp
import avill.ladv.chordo.apps.app.helpers.AudioHelper
import avill.ladv.chordo.ui.theme.AppNameTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    val chordoViewModel: ChordoViewModel by viewModels()
    val tempoViewModel: TempoViewModel by viewModels()
    lateinit var audioHelper: AudioHelper

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        mainViewModel.updatePermissionStatus(isGranted)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ViewModelConstructorInComposable")
    @OptIn(
        ExperimentalFoundationApi::class, ExperimentalAnimationApi::class,
        ExperimentalMaterial3Api::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }
        
        CoroutineScope(Dispatchers.Main).launch {
            splashScreen.setKeepOnScreenCondition { false }
        }
        audioHelper = AudioHelper(this@MainActivity)
        // Check initial permission status
        val isPermissionGranted = checkSelfPermission(Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED
        mainViewModel.updatePermissionStatus(isPermissionGranted)

        setContent {
            AppNameTheme {
                ChordoApp(
                    mainViewModel = mainViewModel,
                    viewModel = chordoViewModel,
                    tempoViewModel = tempoViewModel,
                    audioHelper = audioHelper,
                    onRequestPermission = {
                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                )
            }
        }
    }
}
