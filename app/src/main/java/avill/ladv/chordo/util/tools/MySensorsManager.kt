package avill.ladv.chordo.util.tools

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class MySensorsManager(context: Context) : SensorEventListener {
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private lateinit var sensor: Sensor

    fun light(listener: SensorEventListener) {
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!!
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun orientation(listener: SensorEventListener) {
        sensorManager.registerListener(
            listener,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    fun accelerometer(listener: SensorEventListener) {
        sensorManager.registerListener(
            listener,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    fun unregisterListener(listener: SensorEventListener) {
        sensorManager.unregisterListener(listener)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        val millibarsOfPressure = sensorEvent.values[0]
        Log.d(MySensorsManager::class.java.simpleName, millibarsOfPressure.toString())
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        Log.d(MySensorsManager::class.java.simpleName, "--$accuracy")
    }

    fun printAllSensors() {
        val sensorList: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        for (s in sensorList) {
            Log.e(MySensorsManager::class.java.simpleName, s.toString())
        }
    }
    /*Todo: for move*/
    // TYPE_ACCELEROMETER,TYPE_ACCELEROMETER_UNCALIBRATED,TYPE_GYROSCOPE,TYPE_GYROSCOPE_UNCALIBRATED,TYPE_GRAVITY,TYPE_ROTATION_VECTOR,TYPE_STEP_COUNTER
    /**
     *
     * TYPE_ACCELEROMETER	SensorEvent.values[0]	Indica la fuerza de aceleración en el eje x (incluida la gravedad).	m/s2
     * SensorEvent.values[1]	Indica la fuerza de aceleración en el eje Y (incluida la gravedad).
     * SensorEvent.values[2]	Indica la fuerza de aceleración en el eje z (incluida la gravedad).
     * TYPE_ACCELEROMETER_UNCALIBRATED	SensorEvent.values[0]	Indica la aceleración medida en el eje X sin compensación de sesgo.	m/s2
     * SensorEvent.values[1]	Indica la aceleración medida en el eje Y sin compensación de sesgo.
     * SensorEvent.values[2]	Indica la aceleración medida en el eje Z sin compensación de sesgo.
     * SensorEvent.values[3]	Indica la aceleración medida en el eje X con compensación de sesgo estimada.
     * SensorEvent.values[4]	Indica la aceleración medida en el eje Y con compensación de sesgo estimada.
     * SensorEvent.values[5]	Indica la aceleración medida en el eje Z con compensación de sesgo estimada.
     * TYPE_GRAVITY	SensorEvent.values[0]	Indica la fuerza de gravedad en el eje X.	m/s2
     * SensorEvent.values[1]	Indica la fuerza de gravedad en el eje y.
     * SensorEvent.values[2]	Indica la fuerza de gravedad en el eje z.
     * TYPE_GYROSCOPE	SensorEvent.values[0]	Indica la rotación alrededor del eje x.	rad/s
     * SensorEvent.values[1]	Indica la rotación alrededor del eje y.
     * SensorEvent.values[2]	Indica la rotación alrededor del eje z.
     * TYPE_GYROSCOPE_UNCALIBRATED	SensorEvent.values[0]	Indica la velocidad de rotación (sin compensación de variación) alrededor del eje x.	rad/s
     * SensorEvent.values[1]	Indica la velocidad de rotación (sin compensación de variación) alrededor del eje y.
     * SensorEvent.values[2]	Indica la velocidad de rotación (sin compensación de variación) alrededor del eje z.
     * SensorEvent.values[3]	Indica la variación estimada alrededor del eje x.
     * SensorEvent.values[4]	Indica la variación estimada alrededor del eje y.
     * SensorEvent.values[5]	Indica la variación estimada alrededor del eje z.
     * TYPE_LINEAR_ACCELERATION	SensorEvent.values[0]	Indica la fuerza de aceleración en el eje x (sin incluir la gravedad).	m/s2
     * SensorEvent.values[1]	Indica la fuerza de aceleración en el eje y (sin incluir la gravedad).
     * SensorEvent.values[2]	Indica la fuerza de aceleración en el eje z (sin incluir la gravedad).
     * TYPE_ROTATION_VECTOR	SensorEvent.values[0]	Indica el componente vectorial de rotación junto al eje x (x * sin(θ/2)).	Sin unidades
     * SensorEvent.values[1]	Indica el componente vectorial de rotación junto al eje y (y * sin(θ/2)).
     * SensorEvent.values[2]	Indica el componente vectorial de rotación junto al eje z (z * sin(θ/2)).
     * SensorEvent.values[3]	Indica el componente escalar del vector de rotación ((cos(Tensor/2)).1
     * TYPE_SIGNIFICANT_MOTION	N/A	N/A	N/A
     * TYPE_STEP_COUNTER	SensorEvent.values[0]	Cantidad de pasos que dio el usuario desde el último reinicio mientras estaba activado el sensor.	Pasos**/
    //todo: for position
    /**TYPE_GAME_ROTATION_VECTOR	SensorEvent.values[0]	Indica el componente vectorial de rotación junto al eje x (x * sin(θ/2)).	Sin unidades
    SensorEvent.values[1]	Indica el componente vectorial de rotación junto al eje y (y * sin(θ/2)).
    SensorEvent.values[2]	Indica el componente vectorial de rotación junto al eje z (z * sin(θ/2)).
    TYPE_GEOMAGNETIC_ROTATION_VECTOR	SensorEvent.values[0]	Indica el componente vectorial de rotación junto al eje x (x * sin(θ/2)).	Sin unidades
    SensorEvent.values[1]	Indica el componente vectorial de rotación junto al eje y (y * sin(θ/2)).
    SensorEvent.values[2]	Indica el componente vectorial de rotación junto al eje z (z * sin(θ/2)).
    TYPE_MAGNETIC_FIELD	SensorEvent.values[0]	Fuerza del campo geomagnético junto al eje x.	μT
    SensorEvent.values[1]	Fuerza del campo geomagnético junto al eje y.
    SensorEvent.values[2]	Fuerza del campo geomagnético junto al eje z.
    TYPE_MAGNETIC_FIELD_UNCALIBRATED	SensorEvent.values[0]	Fuerza del campo geomagnético (sin calibración de hierro resistente) junto al eje x.	μT
    SensorEvent.values[1]	Fuerza del campo geomagnético (sin calibración de hierro resistente) junto al eje y.
    SensorEvent.values[2]	Fuerza del campo geomagnético (sin calibración de hierro resistente) junto al eje z.
    SensorEvent.values[3]	Estimación del sesgo de hierro junto al eje x.
    SensorEvent.values[4]	Estimación del sesgo de hierro junto al eje y.
    SensorEvent.values[5]	Estimación del sesgo de hierro junto al eje z.
    TYPE_ORIENTATION1	SensorEvent.values[0]	Azimuth (ángulo en torno al eje z).	Grados
    SensorEvent.values[1]	Pitch (ángulo en torno al eje x).
    SensorEvent.values[2]	Roll (ángulo en torno al eje y).
    TYPE_PROXIMITY	SensorEvent.values[0]	Distancia respecto del objeto2	*/

}