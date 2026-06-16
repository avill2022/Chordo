package avill.ladv.chordo.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import avill.ladv.chordo.apps.app.ChordoApp
import avill.ladv.chordo.apps.app.ChordoViewModel
import avill.ladv.chordo.apps.app.MainViewModel
import avill.ladv.chordo.apps.app.TempoViewModel
import avill.ladv.chordo.apps.app.helpers.AudioHelper
import avill.ladv.chordo.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
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
            mainViewModel.init()
            chordoViewModel.getTabs()
            splashScreen.setKeepOnScreenCondition { false }
        }
        audioHelper = AudioHelper(this@MainActivity)
        // Check initial permission status
        val isPermissionGranted = checkSelfPermission(Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED
        mainViewModel.updatePermissionStatus(isPermissionGranted)

        setContent {
            AppTheme {
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
