package avill.ladv.chordo.di

import android.app.Application
import android.content.Context
import avill.ladv.chordo.MyApplication
import avill.ladv.chordo.data.Repository
import avill.ladv.chordo.data.local.LocalDataSource
import avill.ladv.chordo.data.local.db.room.AppDatabase
import avill.ladv.chordo.util.LocationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {
    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): MyApplication {
        return app as MyApplication
    }
    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
    @Singleton
    @Provides
    fun provideLocalDataSource(application: Application) = LocalDataSource(application.baseContext)
    @Singleton
    @Provides
    fun provideDao(database: AppDatabase) = database.noteDao()

    @Singleton
    @Provides
    fun provideRepository(application: Application): Repository {
        return Repository(application.baseContext)
    }

    @Singleton
    @Provides
    fun provideLocationHelper(@ApplicationContext context: Context): LocationHelper {
        return LocationHelper(context)
    }
    @Singleton
    @Provides
    fun providePermissionHelper(@ApplicationContext context: Context): avill.ladv.chordo.util.PermissionHelper {
        return avill.ladv.chordo.util.PermissionHelper(context)
    }

    @Singleton
    @Provides
    fun provideNetworkHelper(@ApplicationContext context: Context): avill.ladv.chordo.util.NetworkHelper {
        return avill.ladv.chordo.util.NetworkHelper(context)
    }

    @Singleton
    @Provides
    fun provideFileScannerHelper(@ApplicationContext context: Context): avill.ladv.chordo.util.FileScannerHelper {
        return avill.ladv.chordo.util.FileScannerHelper(context)
    }

    @Singleton
    @Provides
    fun provideIntentHelper(@ApplicationContext context: Context): avill.ladv.chordo.util.IntentHelper {
        return avill.ladv.chordo.util.IntentHelper(context)
    }
}