package avill.ladv.chordo.data.local.shared

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class MySharedPreferences (context: Context, name: String) {
    companion object {
        @Volatile
        private var instance: MySharedPreferences? = null
        fun getInstance(context: Context, name: String): MySharedPreferences {
            return instance ?: synchronized(this) {
                instance ?: MySharedPreferences(context, name).also { instance = it }
            }
        }
    }
    private val sharedPref: SharedPreferences = context.getSharedPreferences(
        name, Context.MODE_PRIVATE
    )

    fun getString(dataKey: String,defaultValue:String  = ""): String {
        return sharedPref.getString(dataKey, defaultValue)?:defaultValue
    }
    fun saveString(dataKey: String, data: String) {
        sharedPref.edit { putString(dataKey, data) }
    }
    fun getBoolean(dataKey: String, defaultValue: Boolean = false): Boolean {
        return sharedPref.getBoolean(dataKey, defaultValue)
    }
    fun saveBoolean(dataKey:String,data: Boolean){
        sharedPref.edit {
            putBoolean(dataKey,data)
        }
    }
    fun getInt(dataKey: String,defaultValue:Int = -1): Int {
        return sharedPref.getInt(dataKey, defaultValue)
    }
    fun saveInt(dataKey: String, data: Int) {
        sharedPref.edit { putInt(dataKey, data) }
    }
    fun getFloat(s: String, i: Float): Float {
        return sharedPref.getFloat(s, i)
    }
    fun saveFloat(s: String, fl: Float) {
        sharedPref.edit().apply {
            putFloat(s, fl)
            apply()
        }
    }
    fun getLong(s: String, i: Long): Long {
        return sharedPref.getLong(s, i)
    }
    fun saveLong(s: String, fl: Long) {
        sharedPref.edit().apply {
            putLong(s, fl)
            apply()
        }
    }
    //
    fun getSavedDateString(name: String): String {
        return sharedPref.getString(name, "")?:""
    }
}
