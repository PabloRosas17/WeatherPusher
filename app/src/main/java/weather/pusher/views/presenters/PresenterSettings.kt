package weather.pusher.views.presenters

/* Created by evolandlupiz on 02/13/19
 * This file is subject to the terms and conditions defined in
 * file 'WEATHERPUSHER_LICENSE.txt', which is part of this source code package.*/

import android.widget.Toast
import com.google.android.material.switchmaterial.SwitchMaterial
import weather.pusher.R
import weather.pusher.controllers.ViewModelSettings
import weather.pusher.views.ViewSettings

/* @desc helper used for settings view */
class PresenterSettings(var context: ViewSettings, mVmSettings: ViewModelSettings) {

    /* routine used to fire cache on gmail username */
    fun fireOnToggleCacheGmailUsername(){
        val mCacheSm = this.context.findViewById<SwitchMaterial>(R.id.material_sm_cache_gmail_username)
        mCacheSm.isEnabled = false
        Toast.makeText(this.context,"Thank you for checking it out, cache Gmail Username is under development, check back soon.", Toast.LENGTH_LONG).show()
    }

    /* routine used to fire cache on users location */
    fun fireOnToggleCacheLocation(){
        val mCacheSm = this.context.findViewById<SwitchMaterial>(R.id.material_sm_cache_location)
        mCacheSm.isEnabled = false
        Toast.makeText(this.context,"Thank you for checking it out, cache location is under development, check back soon.", Toast.LENGTH_LONG).show()
    }
}