package avill.ladv.chordo.util.tools

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

object MyClipboardManager {
    fun copy(context: Context, tag: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(tag, text)
        clipboard.setPrimaryClip(clip)
    }

    fun paste(context: Context): String {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var pasteData = ""
        if (!clipboard.hasPrimaryClip()) {
            // if clipboard doesn't contain any data
            Toast.makeText(context, "No Data to Paste", Toast.LENGTH_SHORT).show()
        } else if (!clipboard.primaryClipDescription!!.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            // Clipboard has data but it is not plain text
            Toast.makeText(context, "Data is not Plain text", Toast.LENGTH_SHORT).show()
        } else {
            // Clipboard has plain text
            val item = clipboard.primaryClip!!.getItemAt(0)
            pasteData = item.text.toString()
        }
        return pasteData
    }
}
