package avill.ladv.chordo.data.network.socket

import android.os.AsyncTask
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket

class SocketClient(private val serverIp: String, private val serverPort: Int, private val name: String, private val message: String, private val color: String) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg voids: Void): Void? {
        try {
            val socket = Socket(serverIp, serverPort)
            val outputStream: OutputStream = socket.getOutputStream()
            // Create a JSON message
            val jsonMessage = createJsonMessage(name, message, color)
            // Send the JSON message
            val writer = PrintWriter(OutputStreamWriter(outputStream))
            writer.println(jsonMessage)
            writer.flush()
            // Close the socket
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun createJsonMessage(name: String, message: String, color: String): String {
        // Create a simple JSON message
        return "{\"message\":\"$message\", \"name\":\"$name\", \"color\":\"$color\"}"
    }
}
