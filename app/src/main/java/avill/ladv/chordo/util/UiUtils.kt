package avill.ladv.chordo.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

/**
 * Utility functions related to the User Interface (UI), Keyboard, and System Bars.
 */

/**
 * Closes the software keyboard for the given view.
 */
fun View.closeKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * Hides the Status Bar and enables Full Screen mode for the Activity.
 */
fun Activity.hideStatusBar() {
    val lp = window.attributes
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
    window.attributes = lp
    
    @Suppress("DEPRECATION")
    val fullScreenUiOptions = (View.SYSTEM_UI_FLAG_LOW_PROFILE
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_IMMERSIVE)
    
    window.decorView.systemUiVisibility = fullScreenUiOptions
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

/**
 * Toggles between Dark Mode and Light Mode.
 * Note: This triggers an activity recreation.
 */
fun Context.toggleDarkMode() {
    val currentMode = AppCompatDelegate.getDefaultNightMode()
    if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
        Toast.makeText(this, "Switching to Light Mode", Toast.LENGTH_SHORT).show()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    } else {
        Toast.makeText(this, "Switching to Dark Mode", Toast.LENGTH_SHORT).show()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}
