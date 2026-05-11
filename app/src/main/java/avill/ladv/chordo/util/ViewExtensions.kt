package avill.ladv.chordo.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import com.bumptech.glide.Glide
import kotlin.math.abs

/**
 * Extension functions for Android Views and Graphics.
 */

/**
 * Loads an image from a URL into an ImageView using Glide.
 */
fun ImageView.load(url: String) {
    if (url.isNotEmpty()) {
        Glide.with(this.context).load(url).into(this)
    }
}

/**
 * Simplified listener for text changes in an EditText.
 */
fun EditText.onTextChanged(listener: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            listener(s.toString())
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

/**
 * Draws a standard quadratic Bezier curve from one offset to another.
 */
fun Path.standardQuadFromTo(from: Offset, to: Offset) {
    quadraticBezierTo(
        from.x,
        from.y,
        abs(from.x + to.x) / 2f,
        abs(from.y + to.y) / 2f
    )
}
