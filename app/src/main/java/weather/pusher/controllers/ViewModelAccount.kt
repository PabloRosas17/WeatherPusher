package weather.pusher.controllers

/* Created by evolandlupiz on 02/13/19
 * This file is subject to the terms and conditions defined in
 * file 'WEATHERPUSHER_LICENSE.txt', which is part of this source code package.*/

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import weather.pusher.BuildConfig
import weather.pusher.models.OwmRsp
import weather.pusher.services.WeatherService
import weather.pusher.views.ViewAccount

/* ViewModel class will serve as the primary link between the account view and the account model */
class ViewModelAccount(val mWeatherService: WeatherService): ViewModel() {

    /* Koin dependency injection for this class */
    val mModuleVmAccounts: Module = module {
        viewModel { ViewModelAccount(mWeatherService) }
    }

    /* routine to make the network call on the view model scope */
    fun fire(view: ViewAccount, mCityText: TextInputEditText, mForecastData: OwmRsp?) {
        viewModelScope.launch {
            val call = mWeatherService.getWeatherData(
                    mCityText.text.toString() + ",US"
                    , BuildConfig.OWM_API_KEY
                    , "imperial"
                )
            call.enqueue(object : Callback<OwmRsp> {
                override fun onResponse(call: Call<OwmRsp>, response: Response<OwmRsp>) {
                    view.mBinding.mPresenter?.pass(response.body())
                }
                override fun onFailure(call: Call<OwmRsp>, t: Throwable) {
                }
            })
        }
    }
}