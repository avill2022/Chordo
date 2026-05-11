package avill.ladv.chordo.data.local.files

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class RawFileManager(private val context: Context) {

    fun readRawFile(resourceId: Int): String {
        val content = StringBuilder()

        try {
            // Open the raw resource using Resources
            val inputStream = context.resources.openRawResource(resourceId)

            // Use BufferedReader to read the raw file line by line
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                content.append(line)
                content.append('\n')
            }

            // Close the resources
            inputStream.close()
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return content.toString()
    }
}
