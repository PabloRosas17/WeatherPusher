package weather.pusher.core

/* Created by evolandlupiz on 02/13/19
 * This file is subject to the terms and conditions defined in
 * file 'WEATHERPUSHER_LICENSE.txt', which is part of this source code package.*/

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import weather.pusher.controllers.ViewModelAccount
import weather.pusher.controllers.ViewModelSettings
import weather.pusher.services.WeatherService

/* this class will serve as the top main application */
/* hardly anything should touch this class , please be considerate and double check */

class MainActivity: Application() {

    /* modules that will inject into the application */
    val mViewModels = module {
        viewModel { ViewModelAccount(get()) }
        viewModel { ViewModelSettings() }
        factory { OkHttpClient.Builder().build() }
        factory {
            Retrofit.Builder()
                .client(get<OkHttpClient>())
                .baseUrl(OWM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build() }
        factory { get<Retrofit>().create(WeatherService::class.java) }
    }

    /* list of of modules that will load through koin */
    val mModules = listOf(mViewModels)

    /* koin dependent method used to instantiate an instance of
     * this application and link the modules it will use*/
    override fun onCreate() {
        super.onCreate()

        /* framework for date and time targeting API lower than 26 */
        AndroidThreeTen.init(this)

        startKoin {
            /* @androidLogger Android Logger for Koin*/
            /* @androidContext reference to caller context */
            /* @modules reference to modules available */
            androidLogger()
            androidContext(this@MainActivity)
            loadKoinModules(mModules)
        }
    }

    /* base url for retrofit client */
    companion object {
        private const val OWM_BASE_URL = "https://api.openweathermap.org/"
    }
}