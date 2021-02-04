package weather.pusher.util

/* Created by evolandlupiz on 02/13/19
 * This file is subject to the terms and conditions defined in
 * file 'WEATHERPUSHER_LICENSE.txt', which is part of this source code package.*/

import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView

/* @desc this class will enforce the bottom navigation menu expectations */
interface BtmNavIf: BottomNavigationView.OnNavigationItemSelectedListener {

    /* the bottom navigation view associated with this */
    var mBtmNavView: BottomNavigationView

    /* generalized action for when nav item is selected */
    override fun onNavigationItemSelected(item: MenuItem): Boolean

    /* find the element and register listener with the ui */
    fun registerUi()
}