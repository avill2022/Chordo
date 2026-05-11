package avill.ladv.chordo

import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import java.util.Random

/**
 * Read card info
 * @param tag
 * @param SECTOR
 * @param passwdKey
 * @return
 * @throws IOException
 *
 * @author
 * @version
 *
 * @param  nombre del parámetro    descripción de su significado y uso
 * @return     descripción de lo que se devuelve
 * @exception  nombre de la excepción  excepciones que pueden lanzarse
 * @throws
 */



/**
 * Parte descriptiva.
 * Que puede consistir de varias frases o párrafos.
 *
 * @etiqueta texto específico de la etiqueta
 */
object Constants {
    object Parameters {
        var LOCATION_ID = 100
        var SW_VERSION = "0.0.1"
        var CODE_VERSION: Int = 1
        var IMEI: String? = null
        var MAC_ADDRESS = "02:00:00:00:00:00"
        var DEVICE_NAME = ""
        var MODEL = ""
    }
    //all apps
    val gson = Gson()
    val random = Random()
    const val DEBUGGING = true // FIXME false
    const val FIRST_TIME: String = "FIRST_TIME"
    const val OK: Int = 0
    const val ERROR: Int = -1
    const val TOKEN_LOST: Int = -2

    //app attendance
    val f1 = arrayListOf("Angelina","Itzel","Elvira","Miguelina")
    const val ATTENDANCE_APP = "ATTENDANCE_APP"
    val f2 = arrayListOf("Karen","Giovanni","Ulises","Melissa","Eduardo","Yair","Roberto","Valeria","Emiliano","René")
    //other app
    var removeTransactions = false
    const val TOKEN_DEVICE: String = "TOKEN_DEVICE"
    const val DATE_TOKEN_DEVICE: String = "DATE_TOKEN_DEVICE"

    const val DATA_STORE_ONBOARDING = "on_boarding_pref"

    val AppBarCollapsedHeight = 86.dp
    val AppBarExpendedHeight = 400.dp

    val apiKey = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJkNzJhMmM4MDI2ZmJlZjViNmM5M2M4M2UwZDFjOGVmNCIsIm5iZiI6MTc2ODcwNjkyNS40MzIsInN1YiI6IjY5NmM1MzZkNjczZDkwZmE4YTM2YWFhMyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.fo2nB-TNnSZReEgiZFIadEreJuPs6Oy3fd7JP8SVN68"
}
