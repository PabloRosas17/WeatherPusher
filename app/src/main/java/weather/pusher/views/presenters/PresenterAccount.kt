package weather.pusher.views.presenters

/* Created by evolandlupiz on 02/13/19
 * This file is subject to the terms and conditions defined in
 * file 'WEATHERPUSHER_LICENSE.txt', which is part of this source code package.*/

import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import weather.pusher.R
import weather.pusher.controllers.ViewModelAccount
import weather.pusher.models.OwmRsp
import weather.pusher.util.GoogleCalendarLoader
import weather.pusher.util.FilterUtil
import weather.pusher.views.ViewAccount

/* @desc helper used to perform ui element routines when called*/
class PresenterAccount(private var context: ViewAccount, private val mVmAccounts: ViewModelAccount) {
    private val mGmailAuthSwitch = this.context.findViewById<SwitchMaterial>(R.id.material_sm_account_authenticate)
    private val mCitySwitch = this.context.findViewById<SwitchMaterial>(R.id.material_sm_select_city)
    private val mPullDataSwitch = this.context.findViewById<SwitchMaterial>(R.id.material_sm_pull_data)
    private val mMinTemperature = this.context.findViewById<Slider>(R.id.material_slider_min_temperature)
    private val mMaxTemperature = this.context.findViewById<Slider>(R.id.material_slider_max_temperature)
    private val mConfirmUpdatesSwitch = this.context.findViewById<SwitchMaterial>(R.id.material_sm_confirm_update)
    private val inflater = LayoutInflater.from(this.context)
    private val view = inflater.inflate(R.layout.dialog_theme, null)
    private val mCityText = view.findViewById<TextInputEditText>(R.id.material_tiet_city_name)
    private val mGoogleCalendarLoader = GoogleCalendarLoader()
    private val mFilterUtil = FilterUtil()
    var mForecastData: OwmRsp? = null

    /* routine for ui element that wishes to authenticate their calendar */
    fun fireOnToggleAccountAuthenticate(){
        mGoogleCalendarLoader.fireAuthentication(this.context)
        mGmailAuthSwitch.isEnabled = false
        mCitySwitch.isEnabled = true
    }

    /* routine for ui element that wishes to toggle the alert dialog for selecting a city */
    fun fireOnToggleSelectCity(){
        this.fireOnCityDialogBuild()
    }

    /* routine for ui element that wishes to pull data from the api */
    fun fireOnToggleDataPull(){
        this.launchNetworkServices()
        mPullDataSwitch.isEnabled = false
        mMinTemperature.isEnabled = true
        mMaxTemperature.isEnabled = true
        mConfirmUpdatesSwitch.isEnabled = true
    }

    /* routine for ui element that wishes to confirm updates and create the event */
    fun fireOnToggleConfirmUpdates(){
        val filtered = this.mFilterUtil
            .filterForecastOnSliderResults(mMinTemperature.value.toInt(), mMaxTemperature.value.toInt(), mForecastData)

        if(filtered.isJsonNull || filtered.size() == 0){
            this.fireOnErrorDialogBuild()
        } else {
            this.mGoogleCalendarLoader.fireCreateEvent(filtered, this.context)
            mMinTemperature.isEnabled = false
            mMaxTemperature.isEnabled = false
            mConfirmUpdatesSwitch.isEnabled = false
        }
    }

    /* routine for ui element that wishes to generate a dialog builder */
    private fun fireOnCityDialogBuild(): AlertDialog {
        val builder = MaterialAlertDialogBuilder(this.context, R.style.MaterialTheme_MtrlDialog)
        var isValid = true

        builder.setView(view)
        builder.setTitle("WeatherPusher")
        builder.setMessage("We will need to know your city to know where to fetch data from.")
        builder.setPositiveButton("Proceed") { _, _ ->
            when {
                mCityText.text.toString() == "" -> {
                    mCitySwitch.isChecked = false
                    Toast.makeText(this.context, "Whoops, you have entered an empty city, try again!", Toast.LENGTH_SHORT).show()
                }
                mCityText.text.toString().length < 4 -> {
                    mCitySwitch.isChecked = false
                    Toast.makeText(this.context, "Whoops, you have entered a city with length 3 (unavailable), try agian!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    isValid = false
                    mCitySwitch.isChecked = true
                    mCitySwitch.isEnabled = false
                    mPullDataSwitch.isEnabled = true
                }
            }
        }
        builder.setOnDismissListener {
            if(mCitySwitch.isChecked && isValid){
                mCitySwitch.isChecked = false
            }
        }
        return builder.show()
    }

    /* error dialog to let the user know they've entered unhandled ranges for their temperature */
    private fun fireOnErrorDialogBuild() {
        val builder = MaterialAlertDialogBuilder(this.context, R.style.MaterialTheme_MtrlDialog)

        builder.setView(view)
        builder.setTitle("WeatherPusher")
        builder.setMessage("You've entered min max that are out of range to filter.")
        builder.setPositiveButton("Proceed") { _, _ ->
            this.context.finish()
        }
        builder.setOnDismissListener {
            this.context.finish()
        }
    }

    /* routine that will launch a network service */
    private fun launchNetworkServices(){
        this.mVmAccounts.fire(context, mCityText, mForecastData)
    }

    /* get the data from the network service */
    fun pass(body: OwmRsp?) {
        this.mForecastData = body
    }
}