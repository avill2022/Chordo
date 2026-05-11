package avill.ladv.chordo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import avill.ladv.chordo.data.local.LocalDataSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyService: Service() {

    //inject the repository
    @Inject
    lateinit var repository:LocalDataSource

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}