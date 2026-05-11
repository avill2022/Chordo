package avill.ladv.chordo.data.network

import android.util.Log
import avill.ladv.chordo.data.network.okhttpclient.MyOkHttpInterface
import avill.ladv.chordo.data.network.retrofit.APIClients
import avill.ladv.chordo.data.network.socket.SocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RemoteDataSource {

    companion object{
        const val chords_api = "https://avillsoftware.com/apis/"
    }
    fun networkErrorHandled(e: String): String {
        return when {
            e.contains("Failed to connect to") -> "El servidor no responde"
            e.contains("Unable") -> "Conexión sin internet"
            e.contains("timeout") -> "Error de conexión"
            e.contains("http:") -> "Error en el servidor"
            else -> e
        }
    }
    //firebase
    //var authProvider: AuthProvider = AuthProvider()
    //var firebaseProvider: FirebaseProvider = FirebaseProvider(true,firebase_transactions)
    //okhttp
    var myOkHttpInterface: MyOkHttpInterface = MyOkHttpInterface()

    var apiChords = APIClients.getChordsApiClient(chords_api)

    //socket
    var socketClient: SocketClient = SocketClient("11.11.11.11",5050,"name","message","#43433")
    //test
    fun okHttpTest() {
        //todo: http request
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val img = myOkHttpInterface.getRandomPuppyImage()
                Log.v(RemoteDataSource::class.java.simpleName, img)
            } catch (e: java.lang.Exception) {
                Log.e(RemoteDataSource::class.simpleName,"Error${e}")
            }
        }
    }
}